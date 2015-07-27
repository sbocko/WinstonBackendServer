package sk.upjs.winston.computation;

import sk.upjs.winston.algorithms.DecisionTreeModel;
import sk.upjs.winston.algorithms.KnnModel;
import sk.upjs.winston.algorithms.LogisticRegressionModel;
import sk.upjs.winston.algorithms.SvmModel;
import sk.upjs.winston.database.DatabaseManager;
import sk.upjs.winston.helper.FileManipulationUtilities;
import sk.upjs.winston.helper.Mailer;
import sk.upjs.winston.model.*;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

/**
 * Created by stefan on 2/16/15.
 */
public class ClassificationModeling extends Modeling {
    private static final double WEIGHT_ATTRIBUTES = 0.4d;
    private static final double WEIGHT_INSTANCES = 0d;
    private static final double WEIGHT_MISSING = 0.2d;
    private static final double WEIGHT_DATATYPE = 0d;
    private static final double WEIGHT_KNN = 0.1d;
    private static final double WEIGHT_DECISION_TREE = 0d;
    private static final double WEIGHT_LOGISTIC_REGRESSION = 0.3d;
    private static final double WEIGHT_SVM = 0d;

    public void performRecommendedDataMiningMethodForAnalysis(Analysis analysis) throws IOException {
        AnalysisResult recommendedMethod = getRecommendedMethod(analysis);
        if (recommendedMethod == null) {
            System.out.println("no method to recommend");
            return;
        }

        BufferedReader r = new BufferedReader(
                new FileReader(getArffFileForAnalysis(analysis)));
        Instances instances = new Instances(r);
        instances.setClassIndex(instances.numAttributes() - 1);
        r.close();

        if (recommendedMethod instanceof KnnResult) {
            KnnResult recommendedKnn = (KnnResult) recommendedMethod;
            int k = recommendedKnn.getK();
            Evaluation trained = (new KnnModel()).knn(instances, k);
            if (trained != null) {
                double rmse = trained.rootMeanSquaredError();
                double meanAbsoluteError = trained.meanAbsoluteError();
                int correct = (int) trained.correct();
                int incorrect = (int) trained.incorrect();
                String summary = trained.toSummaryString();
                KnnResult knn = new KnnResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, k);
                databaseManager.saveAnalysisResult(knn);
            }
        } else if (recommendedMethod instanceof LogisticRegressionResult) {
            LogisticRegressionResult recommendedLogisticRegression = (LogisticRegressionResult) recommendedMethod;
            double ridge = recommendedLogisticRegression.getRidge();
            int maximumNumberOfIterations = recommendedLogisticRegression.getMaximumNumberOfIterations();

            Evaluation trained = (new LogisticRegressionModel()).logisticRegression(instances, ridge, maximumNumberOfIterations);
            if (trained != null) {
                double rmse = trained.rootMeanSquaredError();
                double meanAbsoluteError = trained.meanAbsoluteError();
                int correct = (int) trained.correct();
                int incorrect = (int) trained.incorrect();
                String summary = trained.toSummaryString();
                LogisticRegressionResult logisticRegression = new LogisticRegressionResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, ridge, maximumNumberOfIterations);
                databaseManager.saveAnalysisResult(logisticRegression);
            }
        } else if (recommendedMethod instanceof DecisionTreeResult) {
            DecisionTreeResult recommendedDecisionTree = (DecisionTreeResult) recommendedMethod;
            int m = recommendedDecisionTree.getMinimumNumberOfInstancesPerLeaf();
            float c = (float) recommendedDecisionTree.getConfidenceFactor();
            boolean unpruned = recommendedDecisionTree.isUnpruned();

            Evaluation trained = (new DecisionTreeModel()).j48DecisionTreeAnalysis(instances, m, c, unpruned);
            if (trained != null) {
                double rmse = trained.rootMeanSquaredError();
                double meanAbsoluteError = trained.meanAbsoluteError();
                int correct = (int) trained.correct();
                int incorrect = (int) trained.incorrect();
                String summary = trained.toSummaryString();
                DecisionTreeResult decisionTree = new DecisionTreeResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, c, m, unpruned);
                databaseManager.saveAnalysisResult(decisionTree);
            }
        } else if (recommendedMethod instanceof SvmResult) {
            SvmResult recommendedSvm = (SvmResult) recommendedMethod;
            String kernel = recommendedSvm.getKernel();
            double complexityConstant = recommendedSvm.getComplexityConstant();
            double gamma = recommendedSvm.getGamma();
            Evaluation trained = (new SvmModel()).svm(instances, kernel, complexityConstant, gamma, false);
            if (trained != null) {
                double rmse = trained.rootMeanSquaredError();
                double meanAbsoluteError = trained.meanAbsoluteError();
                int correct = (int) trained.correct();
                int incorrect = (int) trained.incorrect();
                String summary = trained.toSummaryString();
                SvmResult svm = new SvmResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, kernel, complexityConstant, gamma);
                databaseManager.saveAnalysisResult(svm);
            }
        }
    }

    public void performAnalysisWithDefaultHyperparameters(Analysis analysis) throws IOException {
        File arffFile = getArffFileForAnalysis(analysis);

        BufferedReader reader = new BufferedReader(
                new FileReader(arffFile));
        Instances dataInstances = new Instances(reader);
        reader.close();
        dataInstances.setClassIndex(dataInstances.numAttributes() - 1);
        AnalysisResult res;

        Evaluation trained = (new KnnModel()).knn(dataInstances, KnnModel.DEFAULT_KNN_PARAMETER_K);
        if (trained != null) {
            double rmse = trained.rootMeanSquaredError();
            double meanAbsoluteError = trained.meanAbsoluteError();
            int correct = (int) trained.correct();
            int incorrect = (int) trained.incorrect();
            String summary = trained.toSummaryString();
            res = new KnnResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, KnnModel.DEFAULT_KNN_PARAMETER_K);
            databaseManager.saveAnalysisResult(res);
        }

        trained = (new DecisionTreeModel()).j48DecisionTreeAnalysis(dataInstances, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES,
                DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_PRUNING, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED);
        if (trained != null) {
            double rmse = trained.rootMeanSquaredError();
            double meanAbsoluteError = trained.meanAbsoluteError();
            int correct = (int) trained.correct();
            int incorrect = (int) trained.incorrect();
            String summary = trained.toSummaryString();
            res = new DecisionTreeResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_PRUNING,
                    DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED);
            databaseManager.saveAnalysisResult(res);
        }

        trained = (new LogisticRegressionModel()).logisticRegression(dataInstances, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_MAXIMUM_NUMBER_OF_ITERATIONS);
        if (trained != null) {
            double rmse = trained.rootMeanSquaredError();
            double meanAbsoluteError = trained.meanAbsoluteError();
            int correct = (int) trained.correct();
            int incorrect = (int) trained.incorrect();
            String summary = trained.toSummaryString();
            res = new LogisticRegressionResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_MAXIMUM_NUMBER_OF_ITERATIONS);
            databaseManager.saveAnalysisResult(res);
        }

        trained = (new SvmModel()).svm(dataInstances, SvmModel.DEFAULT_SVM_PARAMETER_KERNEL, SvmModel.DEFAULT_SVM_PARAMETER_C_COMPLEXITY_CONSTANT, SvmModel.DEFAULT_SVM_PARAMETER_GAMMA, false);
        if (trained != null) {
            double rmse = trained.rootMeanSquaredError();
            double meanAbsoluteError = trained.meanAbsoluteError();
            int correct = (int) trained.correct();
            int incorrect = (int) trained.incorrect();
            String summary = trained.toSummaryString();
            res = new SvmResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, SvmModel.DEFAULT_SVM_PARAMETER_KERNEL, SvmModel.DEFAULT_SVM_PARAMETER_C_COMPLEXITY_CONSTANT, SvmModel.DEFAULT_SVM_PARAMETER_GAMMA);
            databaseManager.saveAnalysisResult(res);
        }
    }

    protected void gridSearch(Analysis analysis, Instances dataInstances) {
        System.out.println("gridsearch started");
        Set<AnalysisResult> results;
        //kNN
        results = (new KnnModel()).knnSearch(analysis, dataInstances);
        for (AnalysisResult result : results) {
            databaseManager.saveAnalysisResult(result);
        }
        System.out.println("knn done");
        //decision tree
        results = (new DecisionTreeModel()).j48Search(analysis, dataInstances);
        for (AnalysisResult result : results) {
            databaseManager.saveAnalysisResult(result);
        }
        System.out.println("dec tree done");
        //logistic regression
        results = (new LogisticRegressionModel()).logisticRegressionSearch(analysis, dataInstances);
        for (AnalysisResult result : results) {
            databaseManager.saveAnalysisResult(result);
        }
        System.out.println("log reg done");
        //svm
        results = (new SvmModel()).svmSearch(analysis, dataInstances, false);
        for (AnalysisResult result : results) {
            databaseManager.saveAnalysisResult(result);
        }
        System.out.println("svm done");
        analysis.setAnalyzedByGridSearch(true);
        analysis.setGridSearchAnalysisInProgress(false);
        databaseManager.updateAnalysis(analysis);
        System.out.println("gridsearch finished");
    }

    protected double computeDistanceForAnalyses(Analysis analysis1, Analysis analysis2) {
        if (!analysis1.getTask().equals(analysis2.getTask()) && !Analysis.TASK_CLASSIFICATION.equals(analysis1.getTask())) {
            return Double.MAX_VALUE;
        }

        double distance = 0d;
        distance = distance + WEIGHT_ATTRIBUTES * ((Math.abs(analysis1.getNumberOfMissingValues() - analysis2.getNumberOfAttributes())) / ATTRIBUTES_INTERVAL_SIZE);
        distance = distance + WEIGHT_INSTANCES * ((Math.abs(analysis1.getNumberOfInstances() - analysis2.getNumberOfInstances())) / INSTANCES_INTERVAL_SIZE);
        distance = distance + WEIGHT_MISSING * ((Math.abs(analysis1.getNumberOfMissingValues() - analysis2.getNumberOfMissingValues())) / MISSING_VALUES_INTERVAL_SIZE);
        if (analysis1.getDataType() != analysis2.getDataType()) {
            distance = distance + 1 * WEIGHT_DATATYPE;
        }

        distance = distance + (WEIGHT_KNN * (Math.abs(defaultKnnResultForAnalysis(analysis1).getRmse() -
                defaultKnnResultForAnalysis(analysis2).getRmse())));
        distance = distance + (WEIGHT_DECISION_TREE * (Math.abs(defaultDecisionTreeResultForAnalysis(analysis1).getRmse() -
                defaultDecisionTreeResultForAnalysis(analysis2).getRmse())));
        distance = distance + (WEIGHT_LOGISTIC_REGRESSION * (Math.abs(defaultLogisticRegressionResultForAnalysis(analysis1).getRmse() -
                defaultLogisticRegressionResultForAnalysis(analysis2).getRmse())));
        distance = distance + (WEIGHT_SVM * (Math.abs(defaultSvmResultForAnalysis(analysis1).getRmse() -
                defaultSvmResultForAnalysis(analysis2).getRmse())));

        return distance;
    }

    /**
     * HELPER METHODS
     */

    private AnalysisResult defaultKnnResultForAnalysis(Analysis analysis) {
        return databaseManager.defaultKnnResultForAnalysis(analysis);
    }

    private AnalysisResult defaultDecisionTreeResultForAnalysis(Analysis analysis) {
        return databaseManager.defaultDecisionTreeResultForAnalysis(analysis);
    }

    private AnalysisResult defaultLogisticRegressionResultForAnalysis(Analysis analysis) {
        return databaseManager.defaultLogisticRegressionResultForAnalysis(analysis);
    }

    private AnalysisResult defaultSvmResultForAnalysis(Analysis analysis) {
        return databaseManager.defaultSvmResultForAnalysis(analysis);
    }

}

package sk.upjs.winston.computation;

import sk.upjs.winston.algorithms.*;
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
public class RegressionModeling extends Modeling {
    private static final double WEIGHT_ATTRIBUTES = 0.4d;
    private static final double WEIGHT_INSTANCES = 0d;
    private static final double WEIGHT_MISSING = 0.2d;
    private static final double WEIGHT_DATATYPE = 0d;
    protected static final double WEIGHT_LINEAR_REGRESSION = 0.1d;
    protected static final double WEIGHT_REGRESSION_TREE = 0.1d;
    protected static final double WEIGHT_KNN_REGRESSION = 0.1d;
    protected static final double WEIGHT_SVM_REGRESSION = 0.1d;

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
        } else if (recommendedMethod instanceof LinearRegressionResult) {
            LinearRegressionResult recommendedLinearRegression = (LinearRegressionResult) recommendedMethod;
            double ridge = recommendedLinearRegression.getRidge();

            Evaluation trained = (new LinearRegressionModel()).linearRegression(instances, ridge);
            if (trained != null) {
                double rmse = trained.rootMeanSquaredError();
                double meanAbsoluteError = trained.meanAbsoluteError();
                int correct = (int) trained.correct();
                int incorrect = (int) trained.incorrect();
                String summary = trained.toSummaryString();
                LinearRegressionResult linearRegression = new LinearRegressionResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, ridge);
                databaseManager.saveAnalysisResult(linearRegression);
            }
        } else if (recommendedMethod instanceof RegressionTreeResult) {
            RegressionTreeResult recommendedRegressionTree = (RegressionTreeResult) recommendedMethod;
            int m = recommendedRegressionTree.getMinimumNumberOfInstancesPerLeaf();
            double v = recommendedRegressionTree.getMinimumVarianceForSplit();
            int n = recommendedRegressionTree.getNumberOfFolds();

            Evaluation trained = (new RegressionTreeModel()).repTreeAnalysis(instances, m, v, n);
            if (trained != null) {
                double rmse = trained.rootMeanSquaredError();
                double meanAbsoluteError = trained.meanAbsoluteError();
                int correct = (int) trained.correct();
                int incorrect = (int) trained.incorrect();
                String summary = trained.toSummaryString();
                RegressionTreeResult regressionTree = new RegressionTreeResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, m, v, n);
                databaseManager.saveAnalysisResult(regressionTree);
            }
        } else if (recommendedMethod instanceof SvmResult) {
            SvmResult recommendedSvm = (SvmResult) recommendedMethod;
            String kernel = recommendedSvm.getKernel();
            double complexityConstant = recommendedSvm.getComplexityConstant();
            double gamma = recommendedSvm.getGamma();
            Evaluation trained = (new SvmModel()).svm(instances, kernel, complexityConstant, gamma, true);
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

        trained = (new RegressionTreeModel()).repTreeAnalysis(dataInstances, RegressionTreeModel.DEFAULT_REGRESSION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES,
                RegressionTreeModel.DEFAULT_REGRESSION_TREE_MIN_VARIANCE_FOR_SPLIT, RegressionTreeModel.DEFAULT_REGRESSION_NUMBER_OF_FOLDS);
        if (trained != null) {
            double rmse = trained.rootMeanSquaredError();
            double meanAbsoluteError = trained.meanAbsoluteError();
            int correct = (int) trained.correct();
            int incorrect = (int) trained.incorrect();
            String summary = trained.toSummaryString();
            res = new RegressionTreeResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, RegressionTreeModel.DEFAULT_REGRESSION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES,
                    RegressionTreeModel.DEFAULT_REGRESSION_TREE_MIN_VARIANCE_FOR_SPLIT, RegressionTreeModel.DEFAULT_REGRESSION_NUMBER_OF_FOLDS);
            databaseManager.saveAnalysisResult(res);
        }

        trained = (new LinearRegressionModel()).linearRegression(dataInstances, LinearRegressionModel.DEFAULT_LINEAR_REGRESSION_PARAMETER_RIDGE);
        if (trained != null) {
            double rmse = trained.rootMeanSquaredError();
            double meanAbsoluteError = trained.meanAbsoluteError();
            int correct = (int) trained.correct();
            int incorrect = (int) trained.incorrect();
            String summary = trained.toSummaryString();
            res = new LinearRegressionResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, LinearRegressionModel.DEFAULT_LINEAR_REGRESSION_PARAMETER_RIDGE);
            databaseManager.saveAnalysisResult(res);
        }

        trained = (new SvmModel()).svm(dataInstances, SvmModel.DEFAULT_SVM_PARAMETER_KERNEL, SvmModel.DEFAULT_SVM_PARAMETER_C_COMPLEXITY_CONSTANT, SvmModel.DEFAULT_SVM_PARAMETER_GAMMA, true);
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
        results = (new RegressionTreeModel()).REPTreeSearch(analysis, dataInstances);
        for (AnalysisResult result : results) {
            databaseManager.saveAnalysisResult(result);
        }
        System.out.println("reg tree done");
        //logistic regression
        results = (new LinearRegressionModel()).linearRegressionSearch(analysis, dataInstances);
        for (AnalysisResult result : results) {
            databaseManager.saveAnalysisResult(result);
        }
        System.out.println("lin reg done");
        //svm
        results = (new SvmModel()).svmSearch(analysis, dataInstances, true);
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

        distance = distance + (WEIGHT_KNN_REGRESSION * (Math.abs(defaultKnnResultForAnalysis(analysis1).getRmse() -
                defaultKnnResultForAnalysis(analysis2).getRmse())));
        distance = distance + (WEIGHT_REGRESSION_TREE * (Math.abs(defaultRegressionTreeResultForAnalysis(analysis1).getRmse() -
                defaultRegressionTreeResultForAnalysis(analysis2).getRmse())));
        distance = distance + (WEIGHT_LINEAR_REGRESSION * (Math.abs(defaultLinearRegressionResultForAnalysis(analysis1).getRmse() -
                defaultLinearRegressionResultForAnalysis(analysis2).getRmse())));
        distance = distance + (WEIGHT_SVM_REGRESSION * (Math.abs(defaultSvmResultForAnalysis(analysis1).getRmse() -
                defaultSvmResultForAnalysis(analysis2).getRmse())));

        return distance;
    }

    /**
     * HELPER METHODS
     */

    private AnalysisResult defaultKnnResultForAnalysis(Analysis analysis) {
        return databaseManager.defaultKnnResultForAnalysis(analysis);
    }

    private AnalysisResult defaultRegressionTreeResultForAnalysis(Analysis analysis) {
        return databaseManager.defaultRegressionTreeResultForAnalysis(analysis);
    }

    private AnalysisResult defaultLinearRegressionResultForAnalysis(Analysis analysis) {
        return databaseManager.defaultLinearRegressionResultForAnalysis(analysis);
    }

    private AnalysisResult defaultSvmResultForAnalysis(Analysis analysis) {
        return databaseManager.defaultSvmResultForAnalysis(analysis);
    }

}

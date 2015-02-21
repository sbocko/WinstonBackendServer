package sk.upjs.winston.computation;

import sk.upjs.winston.algorithms.DecisionTreeModel;
import sk.upjs.winston.algorithms.KnnModel;
import sk.upjs.winston.algorithms.LogisticRegressionModel;
import sk.upjs.winston.algorithms.SvmModel;
import sk.upjs.winston.database.DatabaseManager;
import sk.upjs.winston.helper.FileManipulationUtilities;
import sk.upjs.winston.helper.Mailer;
import sk.upjs.winston.model.*;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by stefan on 2/16/15.
 */
public class Modelling {
    private DatabaseManager databaseManager = new DatabaseManager();

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
            double rmse = (new KnnModel()).knn(instances, k);
            KnnResult knn = new KnnResult(analysis.getId(), rmse, k);
            databaseManager.saveAnalysisResult(knn);
        } else if (recommendedMethod instanceof LogisticRegressionResult) {
            LogisticRegressionResult recommendedLogisticRegression = (LogisticRegressionResult) recommendedMethod;
            double ridge = recommendedLogisticRegression.getRidge();
            int maximumNumberOfIterations = recommendedLogisticRegression.getMaximumNumberOfIterations();
            double rmse = (new LogisticRegressionModel()).logisticRegression(instances, ridge, maximumNumberOfIterations);
            LogisticRegressionResult logisticRegression = new LogisticRegressionResult(analysis.getId(), rmse, ridge, maximumNumberOfIterations);
            databaseManager.saveAnalysisResult(logisticRegression);
        } else if (recommendedMethod instanceof DecisionTreeResult) {
            DecisionTreeResult recommendedDecisionTree = (DecisionTreeResult) recommendedMethod;
            int m = recommendedDecisionTree.getMinimumNumberOfInstancesPerLeaf();
            float c = (float) recommendedDecisionTree.getConfidenceFactor();
            boolean unpruned = recommendedDecisionTree.isUnpruned();
            double rmse = (new DecisionTreeModel()).j48DecisionTreeAnalysis(instances, m, c, unpruned);
            DecisionTreeResult decisionTree = new DecisionTreeResult(analysis.getId(), rmse, c, m, unpruned);
            databaseManager.saveAnalysisResult(decisionTree);
        } else if (recommendedMethod instanceof SvmResult) {
            SvmResult recommendedSvm = (SvmResult) recommendedMethod;
            String kernel = recommendedSvm.getKernel();
            double complexityConstant = recommendedSvm.getComplexityConstant();
            double gamma = recommendedSvm.getGamma();
            double rmse = (new SvmModel()).svm(instances, kernel, complexityConstant, gamma);
            SvmResult svm = new SvmResult(analysis.getId(), rmse, kernel, complexityConstant, gamma);
            databaseManager.saveAnalysisResult(svm);
        }
    }


    public void performAnalysisWithDefaultHyperparameters(Analysis analysis) throws IOException {
        File arffFile = getArffFileForAnalysis(analysis);

        BufferedReader reader = new BufferedReader(
                new FileReader(arffFile));
        Instances dataInstances = new Instances(reader);
        reader.close();
        dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

        double rmse = (new KnnModel()).knn(dataInstances, KnnModel.DEFAULT_KNN_PARAMETER_K);
        AnalysisResult res = new KnnResult(analysis.getId(), rmse, KnnModel.DEFAULT_KNN_PARAMETER_K);
        databaseManager.saveAnalysisResult(res);

        rmse = (new DecisionTreeModel()).j48DecisionTreeAnalysis(dataInstances, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES,
                DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_PRUNING, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED);
        res = new DecisionTreeResult(analysis.getId(), rmse, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_PRUNING,
                DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED);
        databaseManager.saveAnalysisResult(res);

        rmse = (new LogisticRegressionModel()).logisticRegression(dataInstances, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_MAXIMUM_NUMBER_OF_ITERATIONS);
        res = new LogisticRegressionResult(analysis.getId(), rmse, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_MAXIMUM_NUMBER_OF_ITERATIONS);
        databaseManager.saveAnalysisResult(res);

        rmse = (new SvmModel()).svm(dataInstances, SvmModel.DEFAULT_SVM_PARAMETER_KERNEL, SvmModel.DEFAULT_SVM_PARAMETER_C_COMPLEXITY_CONSTANT, SvmModel.DEFAULT_SVM_PARAMETER_GAMMA);
        res = new SvmResult(analysis.getId(), rmse, SvmModel.DEFAULT_SVM_PARAMETER_KERNEL, SvmModel.DEFAULT_SVM_PARAMETER_C_COMPLEXITY_CONSTANT, SvmModel.DEFAULT_SVM_PARAMETER_GAMMA);
        databaseManager.saveAnalysisResult(res);
    }

    public void performGridsearchAnalysisForFile(Analysis analysis) throws IOException {
        analysis.setGridSearchAnalysisInProgress(true);
        databaseManager.updateAnalysis(analysis);
        File arffFile = getArffFileForAnalysis(analysis);

        BufferedReader reader = new BufferedReader(
                new FileReader(arffFile));
        Instances dataInstances = new Instances(reader);
        reader.close();
        dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

        String email = analysis.getDataset().getUser().getEmail();
        String filename = analysis.getDataFile();
        long analysisId = analysis.getId();

        gridSearch(analysis, dataInstances);
        informUserByEmailAboutGridSearchResults(email, filename, analysisId);
    }

    /**
     * HELPER METHODS
     */

    private AnalysisResult getRecommendedMethod(Analysis analysis) {
        Analysis mostSimilar = getMostSimilarAnalysis(analysis);
        if (mostSimilar == null) {
            return null;
        }
        return getBestMethodForAnalysis(mostSimilar);
    }

    private AnalysisResult getBestMethodForAnalysis(Analysis datasetAnalysis) {
        return databaseManager.bestMethodForAnalysis(datasetAnalysis);
    }

    private Analysis getMostSimilarAnalysis(Analysis analysis) {
        List<Analysis> otherProcessedAnalyses = databaseManager.analysesAnalyzedByGridSearch();

        if (otherProcessedAnalyses == null || otherProcessedAnalyses.size() == 0) {
            return null;
        }

        Analysis mostSimilar = otherProcessedAnalyses.get(0);
        double distance = Double.MAX_VALUE;

        for (Analysis otherProcessedAnalyse : otherProcessedAnalyses) {
            if(otherProcessedAnalyse.getId() != analysis.getId()) {
                double actualDistance = computeDistanceForAnalyses(otherProcessedAnalyse, analysis);
                if (actualDistance < distance) {
                    distance = actualDistance;
                    mostSimilar = otherProcessedAnalyse;
                }
            }
        }

        return mostSimilar;
    }

    private static final double INSTANCES_INTERVAL_SIZE = 12960 - 15d;
    private static final double ATTRIBUTES_INTERVAL_SIZE = 71 - 1d;
    private static final double MISSING_VALUES_INTERVAL_SIZE = 19692d;

    private static final double WEIGHT_ATTRIBUTES = 0.4d;
    private static final double WEIGHT_INSTANCES = 0d;
    private static final double WEIGHT_MISSING = 0.2d;
    private static final double WEIGHT_DATATYPE = 0d;
    private static final double WEIGHT_KNN = 0.1d;
    private static final double WEIGHT_DECISION_TREE = 0d;
    private static final double WEIGHT_LOGISTIC_REGRESSION = 0.3d;
    private static final double WEIGHT_SVM = 0d;

    private double computeDistanceForAnalyses(Analysis analysis1, Analysis analysis2) {
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

    // Inject link generator

    private File getArffFileForAnalysis(Analysis analysis) {
        String filepath = FileManipulationUtilities.PREPARED_DATAFILES_DIRECTORY + "/" + analysis.getDataFile();
        return new File(filepath);
    }

    public void informUserByEmailAboutGridSearchResults(String email, String dataFileName, long analysisId) {
        System.out.println("preparing to send email");
        String subject = "Winston - analysis finished: " + dataFileName;
        String body = "Hello,\n\n results are waiting for you at\n\n" + Mailer.WEB_SERVER_URL + "/winston/analysis/show/" + analysisId + "\n\nThank you!";
        Mailer.sendEmail(email, subject, body);
        System.out.println("mail sent");
    }

    private void gridSearch(Analysis analysis, Instances dataInstances) {
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
        results = (new SvmModel()).svmSearch(analysis, dataInstances);
        for (AnalysisResult result : results) {
            databaseManager.saveAnalysisResult(result);
        }
        System.out.println("svm done");
        analysis.setAnalyzedByGridSearch(true);
        analysis.setGridSearchAnalysisInProgress(false);
        databaseManager.updateAnalysis(analysis);
        System.out.println("gridsearch finished");
    }

}

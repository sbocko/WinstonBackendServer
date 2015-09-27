package sk.upjs.winston.computation;

import sk.upjs.winston.algorithms.*;
import sk.upjs.winston.model.*;
import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.classifiers.Evaluation;
import weka.clusterers.SimpleKMeans;
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
public class PatternMiningModeling extends Modeling {
    private static final double WEIGHT_ATTRIBUTES = 0.4d;
    private static final double WEIGHT_INSTANCES = 0d;
    private static final double WEIGHT_MISSING = 0.2d;
    private static final double WEIGHT_DATATYPE = 0d;
    protected static final double WEIGHT_APRIORI = 0.2d;
    protected static final double WEIGHT_SIMPLE_K_MEANS = 0.2d;

    @Override
    public void performRecommendedDataMiningMethodForAnalysis(Analysis analysis) throws IOException {
        //TODO how to compare results?
    }

    @Override
    public void performAnalysisWithDefaultHyperparameters(Analysis analysis) throws IOException {
        File arffFile = getArffFileForAnalysis(analysis);

        BufferedReader reader = new BufferedReader(
                new FileReader(arffFile));
        Instances dataInstances = new Instances(reader);
        reader.close();
//        dataInstances.setClassIndex(dataInstances.numAttributes() - 1);
        AnalysisResult res;

        SimpleKMeans simpleKMeans = (new SimpleKMeansModel()).simpleKMeans(dataInstances, SimpleKMeansModel.DEFAULT_SIMPLE_K_MEANS_PARAMETER_NUMBER_OF_CLUSTERS, SimpleKMeansModel.DEFAULT_SIMPLE_K_MEANS_PARAMETER_INITIALIZATION_METHOD);
        if (simpleKMeans != null) {
            int numberOfClusters = SimpleKMeansModel.DEFAULT_SIMPLE_K_MEANS_PARAMETER_NUMBER_OF_CLUSTERS;
            int initMethod = SimpleKMeansModel.DEFAULT_SIMPLE_K_MEANS_PARAMETER_INITIALIZATION_METHOD;
            String clusterCentroids = simpleKMeans.getClusterCentroids().toString();
            String clusterSizes = SimpleKMeansModel.getClusterSizesForModel(simpleKMeans);

            res = new SimpleKMeansResult(analysis.getId(), AnalysisResult.RMSE_UNDEFINED, 0, 0, 0, "", numberOfClusters, initMethod, clusterCentroids, clusterSizes);
            databaseManager.saveAnalysisResult(res);
        }

        Apriori apriori = (new AprioriModel()).apriori(dataInstances, AprioriModel.DEFAULT_APRIORI_NUMBER_OF_RULES);
        if (apriori != null) {
            List<AssociationRule> rules = apriori.getAssociationRules().getRules();
            String associationRules = "";
            for (AssociationRule rule : rules) {
                associationRules += rule.toString() + "\n";
            }
            res = new AprioriResult(analysis.getId(), AnalysisResult.RMSE_UNDEFINED, 0, 0, 0, "", apriori.getNumRules(), associationRules);
            databaseManager.saveAnalysisResult(res);
        }
    }

    @Override
    protected void gridSearch(Analysis analysis, Instances dataInstances) {
        System.out.println("gridsearch started");
        Set<AnalysisResult> results;
        //simple k-means
        results = (new SimpleKMeansModel()).simpleKMeansSearch(analysis, dataInstances);
        for (AnalysisResult result : results) {
            databaseManager.saveAnalysisResult(result);
        }
        System.out.println("simple k-means done");
        //apriori
        results = (new AprioriModel()).aprioriSearch(analysis, dataInstances);
        for (AnalysisResult result : results) {
            databaseManager.saveAnalysisResult(result);
        }
        System.out.println("apriori done");

        analysis.setAnalyzedByGridSearch(true);
        analysis.setGridSearchAnalysisInProgress(false);
        databaseManager.updateAnalysis(analysis);
        System.out.println("gridsearch finished");
    }

    @Override
    protected double computeDistanceForAnalyses(Analysis analysis1, Analysis analysis2) {
        return 0;
    }
}

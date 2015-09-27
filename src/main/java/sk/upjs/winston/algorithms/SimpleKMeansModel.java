package sk.upjs.winston.algorithms;

import sk.upjs.winston.model.Analysis;
import sk.upjs.winston.model.AnalysisResult;
import sk.upjs.winston.model.SimpleKMeansResult;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.SelectedTag;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for performing simplekmeans analysis of datasets.
 * Created by stefan on 6/9/14.
 */
public class SimpleKMeansModel extends Model {
    public static final int DEFAULT_SIMPLE_K_MEANS_PARAMETER_NUMBER_OF_CLUSTERS = 3;
    public static final int DEFAULT_SIMPLE_K_MEANS_PARAMETER_INITIALIZATION_METHOD = 0;

    public static final int MIN_NUMBER_OF_CLUSTERS = 2;
    public static final int MAX_NUMBER_OF_CLUSTERS = 20;
    public static final int NUMBER_OF_CLUSTER_STEP = 1;

    /**
     * Performes Simple k-means algorithm and evaluates results 10 times with 10-fold cross validation method.
     * Returnes the evaluation object for given model.
     *
     * @param dataInstances dataset instances
     * @param n             number of clusters parameter for apriori algorithm
     * @param initMethod    initialization method
     * @return SimpleKMeans object with clusters
     */
    public SimpleKMeans simpleKMeans(Instances dataInstances, int n, int initMethod) {
        SimpleKMeans kmeans = new SimpleKMeans();
        try {
            kmeans.setSeed(10);
            kmeans.setPreserveInstancesOrder(true);
            kmeans.setNumClusters(n);
            kmeans.setInitializationMethod(new SelectedTag(initMethod, SimpleKMeans.TAGS_SELECTION));
            kmeans.buildClusterer(dataInstances);

            return kmeans;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Set<AnalysisResult> simpleKMeansSearch(Analysis analysis, Instances dataInstances) {
        Set<AnalysisResult> results = new HashSet<AnalysisResult>();
        for (int n = MIN_NUMBER_OF_CLUSTERS; n <= MAX_NUMBER_OF_CLUSTERS; n += NUMBER_OF_CLUSTER_STEP) {
            SimpleKMeans trained = simpleKMeans(dataInstances, n, SimpleKMeans.CANOPY);
            if (trained != null) {
                AnalysisResult result = createAnalysisResultFromModel(analysis, trained, SimpleKMeans.CANOPY);
                if (result != null) {
                    results.add(result);
                }
            }
            trained = simpleKMeans(dataInstances, n, SimpleKMeans.FARTHEST_FIRST);
            if (trained != null) {
                AnalysisResult result = createAnalysisResultFromModel(analysis, trained, SimpleKMeans.FARTHEST_FIRST);
                if (result != null) {
                    results.add(result);
                }
            }
            trained = simpleKMeans(dataInstances, n, SimpleKMeans.KMEANS_PLUS_PLUS);
            if (trained != null) {
                AnalysisResult result = createAnalysisResultFromModel(analysis, trained, SimpleKMeans.KMEANS_PLUS_PLUS);
                if (result != null) {
                    results.add(result);
                }
            }
        }
        return results;
    }

    private AnalysisResult createAnalysisResultFromModel(Analysis analysis, SimpleKMeans trained, int initializationMethod) {
        try {
            int numberOfClusters = trained.numberOfClusters();
            String clusterCentroids = trained.getClusterCentroids().toString();
            String clusterSizes = getClusterSizesForModel(trained);
            AnalysisResult result = new SimpleKMeansResult(analysis.getId(), AnalysisResult.RMSE_UNDEFINED, 0, 0, 0, "", numberOfClusters, initializationMethod, clusterCentroids, clusterSizes);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getClusterSizesForModel(SimpleKMeans trained) {
        if(trained == null) {
            return null;
        }
        String clusterSizes = "";
        for (int i = 0; i < trained.getClusterSizes().length; i++) {
            clusterSizes += ((int) trained.getClusterSizes()[i]) + "\n";
        }
        return clusterSizes;
    }
}

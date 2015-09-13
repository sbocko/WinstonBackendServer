package sk.upjs.winston.model;

/**
 * Created by stefan on 2/16/15.
 */
public class SimpleKMeansResult extends AnalysisResult {
    public static final int INITIALIZATION_METHOD_RANDOM = 0;
    public static final int INITIALIZATION_METHOD_K_MEANS_PLUS_PLUS = 1;
    public static final int INITIALIZATION_METHOD_CANOPY = 2;
    public static final int INITIALIZATION_METHOD_FARTHEST_FIRST = 3;

    private int numberOfClusters;
    private int initializationMethod;
    String clusterCentroids;
    String clusterSizes;

    public SimpleKMeansResult(long analysis_id, double rmse, double meanAbsoluteError, int correctlyClassified, int incorrectlyClassified, String summary, int numberOfClusters, int initializationMethod, String clusterCentroids, String clusterSizes) {
        super(analysis_id, rmse, meanAbsoluteError, correctlyClassified, incorrectlyClassified, summary);
        this.numberOfClusters = numberOfClusters;
        this.initializationMethod = initializationMethod;
        this.clusterCentroids = clusterCentroids;
        this.clusterSizes = clusterSizes;
    }

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public void setNumberOfClusters(int numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
    }

    public int getInitializationMethod() {
        return initializationMethod;
    }

    public void setInitializationMethod(int initializationMethod) {
        this.initializationMethod = initializationMethod;
    }

    public String getClusterCentroids() {
        return clusterCentroids;
    }

    public void setClusterCentroids(String clusterCentroids) {
        this.clusterCentroids = clusterCentroids;
    }

    public String getClusterSizes() {
        return clusterSizes;
    }

    public void setClusterSizes(String clusterSizes) {
        this.clusterSizes = clusterSizes;
    }
}

package sk.upjs.winston.model;

/**
 * Created by stefan on 2/16/15.
 */
public class RegressionTreeResult extends AnalysisResult {
    private int minimumNumberOfInstancesPerLeaf;
    private double minimumVarianceForSplit;
    private int numberOfFolds;

    public RegressionTreeResult(long analysis_id, double rmse, double meanAbsoluteError, int correctlyClassified, int incorrectlyClassified, String summary, int minimumNumberOfInstancesPerLeaf, double minimumVarianceForSplit, int numberOfFolds) {
        super(analysis_id, rmse, meanAbsoluteError, correctlyClassified, incorrectlyClassified, summary);
        this.minimumNumberOfInstancesPerLeaf = minimumNumberOfInstancesPerLeaf;
        this.minimumVarianceForSplit = minimumVarianceForSplit;
        this.numberOfFolds = numberOfFolds;
    }

    public int getMinimumNumberOfInstancesPerLeaf() {
        return minimumNumberOfInstancesPerLeaf;
    }

    public void setMinimumNumberOfInstancesPerLeaf(int minimumNumberOfInstancesPerLeaf) {
        this.minimumNumberOfInstancesPerLeaf = minimumNumberOfInstancesPerLeaf;
    }

    public double getMinimumVarianceForSplit() {
        return minimumVarianceForSplit;
    }

    public void setMinimumVarianceForSplit(double minimumVarianceForSplit) {
        this.minimumVarianceForSplit = minimumVarianceForSplit;
    }

    public int getNumberOfFolds() {
        return numberOfFolds;
    }

    public void setNumberOfFolds(int numberOfFolds) {
        this.numberOfFolds = numberOfFolds;
    }
}

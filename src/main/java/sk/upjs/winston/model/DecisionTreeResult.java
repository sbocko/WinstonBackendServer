package sk.upjs.winston.model;

/**
 * Created by stefan on 2/16/15.
 */
public class DecisionTreeResult extends AnalysisResult {
    private double confidenceFactor;
    private int minimumNumberOfInstancesPerLeaf;
    private boolean unpruned;

    public DecisionTreeResult(long analysis_id, double rmse, double meanAbsoluteError, int correctlyClassified, int incorrectlyClassified, String summary, double confidenceFactor, int minimumNumberOfInstancesPerLeaf, boolean unpruned) {
        super(analysis_id, rmse, meanAbsoluteError, correctlyClassified, incorrectlyClassified, summary);
        this.confidenceFactor = confidenceFactor;
        this.minimumNumberOfInstancesPerLeaf = minimumNumberOfInstancesPerLeaf;
        this.unpruned = unpruned;
    }

    public double getConfidenceFactor() {
        return confidenceFactor;
    }

    public void setConfidenceFactor(double confidenceFactor) {
        this.confidenceFactor = confidenceFactor;
    }

    public int getMinimumNumberOfInstancesPerLeaf() {
        return minimumNumberOfInstancesPerLeaf;
    }

    public void setMinimumNumberOfInstancesPerLeaf(int minimumNumberOfInstancesPerLeaf) {
        this.minimumNumberOfInstancesPerLeaf = minimumNumberOfInstancesPerLeaf;
    }

    public boolean isUnpruned() {
        return unpruned;
    }

    public void setUnpruned(boolean unpruned) {
        this.unpruned = unpruned;
    }
}

package sk.upjs.winston.model;

/**
 * Created by stefan on 2/15/15.
 */
public class AnalysisResult {
    private long analysis_id;
    private double rmse;

    public AnalysisResult(long analysis_id, double rmse) {
        this.analysis_id = analysis_id;
        this.rmse = rmse;
    }

    public long getAnalysis_id() {
        return analysis_id;
    }

    public void setAnalysis_id(long analysis_id) {
        this.analysis_id = analysis_id;
    }

    public double getRmse() {
        return rmse;
    }

    public void setRmse(double rmse) {
        this.rmse = rmse;
    }
}

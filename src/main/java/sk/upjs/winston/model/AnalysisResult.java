package sk.upjs.winston.model;

/**
 * Created by stefan on 2/15/15.
 */
public class AnalysisResult {
    public static final double RMSE_UNDEFINED = 1234567890d;
    private long analysis_id;
    private double rmse;
    private double meanAbsoluteError;
    private int correctlyClassified;
    private int incorrectlyClassified;
    private String summary;

    public AnalysisResult(long analysis_id, double rmse, double meanAbsoluteError, int correctlyClassified, int incorrectlyClassified, String summary) {
        this.analysis_id = analysis_id;
        this.rmse = rmse;
        this.meanAbsoluteError = meanAbsoluteError;
        this.correctlyClassified = correctlyClassified;
        this.incorrectlyClassified = incorrectlyClassified;
        this.summary = summary;
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

    public double getMeanAbsoluteError() {
        return meanAbsoluteError;
    }

    public void setMeanAbsoluteError(double meanAbsoluteError) {
        this.meanAbsoluteError = meanAbsoluteError;
    }

    public int getCorrectlyClassified() {
        return correctlyClassified;
    }

    public void setCorrectlyClassified(int correctlyClassified) {
        this.correctlyClassified = correctlyClassified;
    }

    public int getIncorrectlyClassified() {
        return incorrectlyClassified;
    }

    public void setIncorrectlyClassified(int incorrectlyClassified) {
        this.incorrectlyClassified = incorrectlyClassified;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

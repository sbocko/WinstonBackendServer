package sk.upjs.winston.model;

/**
 * Created by stefan on 2/16/15.
 */
public class LinearRegressionResult extends AnalysisResult {
    double ridge;

    public LinearRegressionResult(long analysis_id, double rmse, double meanAbsoluteError, int correctlyClassified, int incorrectlyClassified, String summary, double ridge) {
        super(analysis_id, rmse, meanAbsoluteError, correctlyClassified, incorrectlyClassified, summary);
        this.ridge = ridge;
    }

    public double getRidge() {
        return ridge;
    }

    public void setRidge(double ridge) {
        this.ridge = ridge;
    }
}

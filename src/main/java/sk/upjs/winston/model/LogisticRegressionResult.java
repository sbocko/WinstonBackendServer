package sk.upjs.winston.model;

/**
 * Created by stefan on 2/16/15.
 */
public class LogisticRegressionResult extends AnalysisResult {
    public static final int ITERATE_UNTIL_CONVERGENCE = -1;
    double ridge;
    int maximumNumberOfIterations;

    public LogisticRegressionResult(long analysis_id, double rmse, double meanAbsoluteError, int correctlyClassified, int incorrectlyClassified, String summary, double ridge, int maximumNumberOfIterations) {
        super(analysis_id, rmse, meanAbsoluteError, correctlyClassified, incorrectlyClassified, summary);
        this.ridge = ridge;
        this.maximumNumberOfIterations = maximumNumberOfIterations;
    }

    public double getRidge() {
        return ridge;
    }

    public void setRidge(double ridge) {
        this.ridge = ridge;
    }

    public int getMaximumNumberOfIterations() {
        return maximumNumberOfIterations;
    }

    public void setMaximumNumberOfIterations(int maximumNumberOfIterations) {
        this.maximumNumberOfIterations = maximumNumberOfIterations;
    }
}

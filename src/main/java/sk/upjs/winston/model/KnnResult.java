package sk.upjs.winston.model;

/**
 * Created by stefan on 2/16/15.
 */
public class KnnResult extends AnalysisResult {
    private int k;

    public KnnResult(long analysis_id, double rmse, double meanAbsoluteError, int correctlyClassified, int incorrectlyClassified, String summary, int k) {
        super(analysis_id, rmse, meanAbsoluteError, correctlyClassified, incorrectlyClassified, summary);
        this.k = k;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }
}

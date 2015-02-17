package sk.upjs.winston.model;

/**
 * Created by stefan on 2/16/15.
 */
public class KnnResult extends AnalysisResult {
    private int k;

    public KnnResult(long analysis_id, double rmse, int k) {
        super(analysis_id, rmse);
        this.k = k;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }
}

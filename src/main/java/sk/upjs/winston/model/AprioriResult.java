package sk.upjs.winston.model;

/**
 * Created by stefan on 2/16/15.
 */
public class AprioriResult extends AnalysisResult {
    private int numberOfRules;
    private String associationRules;

    public AprioriResult(long analysis_id, double rmse, double meanAbsoluteError, int correctlyClassified, int incorrectlyClassified, String summary, int numberOfRules, String associationRules) {
        super(analysis_id, rmse, meanAbsoluteError, correctlyClassified, incorrectlyClassified, summary);
        this.numberOfRules = numberOfRules;
        this.associationRules = associationRules;
    }

    public int getNumberOfRules() {
        return numberOfRules;
    }

    public void setNumberOfRules(int numberOfRules) {
        this.numberOfRules = numberOfRules;
    }

    public String getAssociationRules() {
        return associationRules;
    }

    public void setAssociationRules(String associationRules) {
        this.associationRules = associationRules;
    }
}

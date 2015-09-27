package sk.upjs.winston.algorithms;

import sk.upjs.winston.model.Analysis;
import sk.upjs.winston.model.AnalysisResult;
import sk.upjs.winston.model.AprioriResult;
import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.core.Instances;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for performing apriori analysis of datasets.
 * Created by stefan on 6/8/14.
 */
public class AprioriModel extends Model {
    public static final int DEFAULT_APRIORI_NUMBER_OF_RULES = 10;
    public static final int[] NUMBER_OF_RULES = {2, 3, 5, 7, 10, 15, 20, 25};

    /**
     * Performes apriori algorithm and evaluates results 10 times with 10-fold cross validation method.
     * Returnes the evaluation object for given model.
     *
     * @param dataInstances dataset instances
     * @param n             number of rules parameter for apriori algorithm
     * @return trained model
     */
    public Apriori apriori(Instances dataInstances, int n) {
        Apriori apriori = new Apriori();
        apriori.setNumRules(n);
        try {
            apriori.buildAssociations(dataInstances);
            return apriori;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Set<AnalysisResult> aprioriSearch(Analysis analysis, Instances dataInstances) {
        Set<AnalysisResult> results = new HashSet<AnalysisResult>();

        for (int rules : NUMBER_OF_RULES) {
            Apriori trained = apriori(dataInstances, rules);
            if (trained != null) {
                String associations = "";
                for (AssociationRule associationRule : trained.getAssociationRules().getRules()) {
                    associations += associationRule + "\n";
                }
                AnalysisResult aprioriResult = new AprioriResult(analysis.getId(), AnalysisResult.RMSE_UNDEFINED, 0, 0, 0, "", rules, associations);
                if (aprioriResult != null) {
                    results.add(aprioriResult);
                }
            }
        }
        return results;
    }
}

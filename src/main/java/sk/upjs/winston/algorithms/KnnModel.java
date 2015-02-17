package sk.upjs.winston.algorithms;

import sk.upjs.winston.model.Analysis;
import sk.upjs.winston.model.AnalysisResult;
import sk.upjs.winston.model.KnnResult;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Class for performing knn analysis of datasets.
 * Created by stefan on 6/8/14.
 */
public class KnnModel extends Model {
    public static final int DEFAULT_KNN_PARAMETER_K = 3;
    public static final int MIN_K = 1;
    public static final int MAX_K = 100;

    /**
     * Performes kNN algorithm and evaluates results 10 times with 10-fold cross validation method.
     * Returnes the mean squared error for given model.
     *
     * @param dataInstances dataset instances
     * @param k             k parameter for kNN algorithm
     * @return root mean squared error
     */
    public double knn(Instances dataInstances, int k) {
        IBk ibk = new IBk(k);
        Evaluation evaluation;
        try {
            evaluation = new Evaluation(dataInstances);
            evaluation.crossValidateModel(ibk, dataInstances, 10, new Random(1));

        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_DURING_CLASSIFICATION;
        }

        return evaluation.rootMeanSquaredError();
    }

    /**
     * Performs knn for k=1..{@MAX_K} and returns RMSE for every value.
     * When something goes wrong during search, the result of this search is not included in result set.
     *
     * @param analysis      analysis details which belongs to returned search result
     * @param dataInstances dataset instances
     * @return Set of KnnResult instances
     */
    public Set<AnalysisResult> knnSearch(Analysis analysis, Instances dataInstances) {
        Set<AnalysisResult> results = new HashSet<AnalysisResult>();
        for (int k = MIN_K; k <= MAX_K; k++) {
            double rmse = knn(dataInstances, k);
            if (rmse != ERROR_DURING_CLASSIFICATION) {
                AnalysisResult res = new KnnResult(analysis.getId(), rmse, k);
                results.add(res);
            }

        }

        return results;
    }

    /**
     * Performes kNN algorithm with random
     * hyperparameter values and evaluates results 10 times with 10-fold cross validation method.
     * Returnes the KnnResult object for given model.
     *
     * @param dataInstances dataset instances
     * @param analysis      analysis details which belongs to returned search result
     * @return knn result object
     */
    public AnalysisResult knnRandomAnalysis(Instances dataInstances, Analysis analysis) {
        int k = getRandomParameterK(MIN_K, MAX_K * 5);
        double rmse = knn(dataInstances, k);
        return new KnnResult(analysis.getId(), rmse, k);
    }

    /**
     * Generates the random value from interval <from,to)
     * which represents the K parameter of the knn algorithm.
     *
     * @param from min value for the generated random number (inclusive)
     * @param to   max value for the generated random number (exclusive)
     * @return the random int value
     */
    public int getRandomParameterK(int from, int to) {
        if (from > to) {
            int f = from;
            from = to;
            to = f;
        }

        return from + (int) (Math.random() * (to - from));
    }
}

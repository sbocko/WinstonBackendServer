package sk.upjs.winston.algorithms;

import sk.upjs.winston.model.Analysis;
import sk.upjs.winston.model.AnalysisResult;
import sk.upjs.winston.model.LogisticRegressionResult;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * Class for performing logistic regression analysis of datasets.
 * Created by stefan on 6/14/14.
 */
public class LogisticRegressionModel extends Model {
    public static final double DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE = 0.05;
    public static final int DEFAULT_LOGISTIC_REGRESSION_PARAMETER_MAXIMUM_NUMBER_OF_ITERATIONS = -1;

    public static final double RIDGE_STEP = 0.05d;
    public static final double MIN_RIDGE = 0d;
    public static final double MAX_RIDGE = 1d;

    /**
     * Performes logistic regression algorithm and evaluates results 10 times with 10-fold cross validation method.
     * Returnes the Evaluation object for given model.
     *
     * @param dataInstances             dataset instances
     * @param ridge                     ridge parameter for logistic regression algorithm
     * @param maximumNumberOfIterations maximum number of iterations parameter
     *                                  for logistic regression algorithm, -1 for iteration until convergence
     * @return evaluation
     */
    public Evaluation logisticRegression(Instances dataInstances, double ridge, int maximumNumberOfIterations) {
        Logistic logistic = new Logistic();
        logistic.setRidge(ridge);
        logistic.setMaxIts(maximumNumberOfIterations);

        Evaluation evaluation;
        try {
            evaluation = new Evaluation(dataInstances);
            evaluation.crossValidateModel(logistic, dataInstances, 10, new Random(1));
        } catch (Exception e) {
//            e.printStackTrace()
            return null;
        }
        return evaluation;
    }

    /**
     * Performs logistic regression for r=0..1 with step {RIDGE_STEP} and returns RMSE for every value.
     * When something goes wrong during search, the result of this search is not included in result set.
     *
     * @param analysis      analysis details which belongs to returned search result
     * @param dataInstances dataset instances
     * @return Set of LogisticRegressionResult instances
     */
    public Set<AnalysisResult> logisticRegressionSearch(Analysis analysis, Instances dataInstances) {
        Set<AnalysisResult> results = new HashSet<AnalysisResult>();
        for (double r = MIN_RIDGE; r <= MAX_RIDGE; r += RIDGE_STEP) {
            Evaluation trained = logisticRegression(dataInstances, r, LogisticRegressionResult.ITERATE_UNTIL_CONVERGENCE);
            if (trained != null) {
                double rmse = trained.rootMeanSquaredError();
                double meanAbsoluteError = trained.meanAbsoluteError();
                int correct = (int) trained.correct();
                int incorrect = (int) trained.incorrect();
                String summary = trained.toSummaryString();
                AnalysisResult res = new LogisticRegressionResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, r, LogisticRegressionResult.ITERATE_UNTIL_CONVERGENCE);
                results.add(res);
            }
        }
        return results;
    }

    /**
     * Performes logistic regression algorithm with random parameter values.
     * Maximum number of iterations is set to default value (-1), which means,
     * that algorithm will run until the convergence. Evaluates the results
     * 10 times with 10-fold cross validation method.
     * Returnes the LogisticRegressionResult object for given model.
     *
     * @param dataInstances dataset instances
     * @param analysis      analysis details which belongs to returned search result
     * @return logistic regression search result object
     */
    public AnalysisResult logisticRegressionRandomAnalysis(Instances dataInstances, Analysis analysis) {
        double ridge = getRandomParameterRidge(MIN_RIDGE, MAX_RIDGE * 5);
        Evaluation trained = logisticRegression(dataInstances, ridge, LogisticRegressionResult.ITERATE_UNTIL_CONVERGENCE);
        if (trained != null) {
            double rmse = trained.rootMeanSquaredError();
            double meanAbsoluteError = trained.meanAbsoluteError();
            int correct = (int) trained.correct();
            int incorrect = (int) trained.incorrect();
            String summary = trained.toSummaryString();
            AnalysisResult res = new LogisticRegressionResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, ridge, LogisticRegressionResult.ITERATE_UNTIL_CONVERGENCE);
            return res;
        }
        return null;
    }

    /**
     * Generates the random value from interval <from,to)
     * which represents the ridge parameter
     * of the logistic regression algorithm.
     *
     * @param from min value for the generated random number (inclusive)
     * @param to   max value for the generated random number (exclusive)
     * @return the random double value
     */
    public double getRandomParameterRidge(double from, double to) {
        if (from > to) {
            double f = from;
            from = to;
            to = f;
        }
        return from + Math.random() * (to - from);
    }

}

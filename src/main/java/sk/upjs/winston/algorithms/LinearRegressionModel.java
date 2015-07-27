package sk.upjs.winston.algorithms;

import sk.upjs.winston.model.Analysis;
import sk.upjs.winston.model.AnalysisResult;
import sk.upjs.winston.model.LinearRegressionResult;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * Class for performing linear regression analysis of datasets.
 * Created by stefan on 24/7/15.
 */
public class LinearRegressionModel extends Model {
    public static final double DEFAULT_LINEAR_REGRESSION_PARAMETER_RIDGE = 0.05;

    public static final double RIDGE_STEP = 0.05d;
    public static final double MIN_RIDGE = 0d;
    public static final double MAX_RIDGE = 1d;

    /**
     * Performes linear regression algorithm and evaluates results 10 times with 10-fold cross validation method.
     * Returnes the Evaluation object for given model.
     *
     * @param dataInstances             dataset instances
     * @param ridge                     ridge parameter for linear regression algorithm
     * @return evaluation
     */
    public Evaluation linearRegression(Instances dataInstances, double ridge) {
        LinearRegression linearRegression = new LinearRegression();
        linearRegression.setRidge(ridge);

        Evaluation evaluation;
        try {
            evaluation = new Evaluation(dataInstances);
            evaluation.crossValidateModel(linearRegression, dataInstances, 10, new Random(1));
        } catch (Exception e) {
//            e.printStackTrace()
            return null;
        }
        return evaluation;
    }

    /**
     * Performs linear regression for r=0..1 with step {RIDGE_STEP} and returns RMSE for every value.
     * When something goes wrong during search, the result of this search is not included in result set.
     *
     * @param analysis      analysis details which belongs to returned search result
     * @param dataInstances dataset instances
     * @return Set of LinearRegressionResult instances
     */
    public Set<AnalysisResult> linearRegressionSearch(Analysis analysis, Instances dataInstances) {
        Set<AnalysisResult> results = new HashSet<AnalysisResult>();
        for (double r = MIN_RIDGE; r <= MAX_RIDGE; r += RIDGE_STEP) {
            Evaluation trained = linearRegression(dataInstances, r);
            if (trained != null) {
                double rmse = trained.rootMeanSquaredError();
                double meanAbsoluteError = trained.meanAbsoluteError();
                int correct = (int) trained.correct();
                int incorrect = (int) trained.incorrect();
                String summary = trained.toSummaryString();
                AnalysisResult res = new LinearRegressionResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, r);
                results.add(res);
            }
        }
        return results;
    }

    /**
     * Performes linear regression algorithm with random parameter values.
     * Evaluates the results 10 times with 10-fold cross validation method.
     * Returnes the LinearRegressionResult object for given model.
     *
     * @param dataInstances dataset instances
     * @param analysis      analysis details which belongs to returned search result
     * @return linear regression search result object
     */
    public AnalysisResult linearRegressionRandomAnalysis(Instances dataInstances, Analysis analysis) {
        double ridge = getRandomParameterRidge(MIN_RIDGE, MAX_RIDGE * 5);
        Evaluation trained = linearRegression(dataInstances, ridge);
        if (trained != null) {
            double rmse = trained.rootMeanSquaredError();
            double meanAbsoluteError = trained.meanAbsoluteError();
            int correct = (int) trained.correct();
            int incorrect = (int) trained.incorrect();
            String summary = trained.toSummaryString();
            AnalysisResult res = new LinearRegressionResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, ridge);
            return res;
        }
        return null;
    }

    /**
     * Generates the random value from interval <from,to)
     * which represents the ridge parameter
     * of the linear regression algorithm.
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

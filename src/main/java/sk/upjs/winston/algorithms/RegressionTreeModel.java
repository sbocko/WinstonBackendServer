package sk.upjs.winston.algorithms;

import sk.upjs.winston.model.Analysis;
import sk.upjs.winston.model.AnalysisResult;
import sk.upjs.winston.model.RegressionTreeResult;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Class for performing regression tree analysis of datasets using REPTree algorithm.
 * Created by stefan on 24/7/15.
 */
public class RegressionTreeModel extends Model {
    public static final int DEFAULT_REGRESSION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES = 2;
    public static final double DEFAULT_REGRESSION_TREE_MIN_VARIANCE_FOR_SPLIT = 1e-3d;
    public static final int DEFAULT_REGRESSION_NUMBER_OF_FOLDS = 3;

    public static final int MIN_NUMBER_OF_INSTANCES_PER_LEAF_STEP = 5;
    public static final int MIN_NUMBER_OF_INSTANCES_PER_LEAF_MIN = 0;
    public static final int MIN_NUMBER_OF_INSTANCES_PER_LEAF_MAX = 1000;

    public static final double MIN_VARIANCE_FOR_SPLIT_STEP = 1e-4d;
    public static final double MIN_VARIANCE_FOR_SPLIT_MIN = 1e-4d;
    public static final double MIN_VARIANCE_FOR_SPLIT_MAX = 1e-2d;

    public static final int NUMBER_OF_FOLDS_STEP = 1;
    public static final int NUMBER_OF_FOLDS_MIN = 1;
    public static final int NUMBER_OF_FOLDS_MAX = 10;

    /**
     * Performes REPTree regression tree algorithm and evaluates results 10 times with 10-fold cross validation method.
     * Returnes the Evaluation object for given model.
     *
     * @param dataInstances analysis instances
     * @param m             minimum number of instances per leaf parameter of REPTree algorithm
     * @param v             minimum variance for split parameter of REPTree algorithm
     * @param n             number of folds parameter of REPTree algorithm
     * @return evaluation
     */
    public Evaluation repTreeAnalysis(Instances dataInstances, int m, double v, int n) {
        REPTree repTree = new REPTree();
        repTree.setMinNum(m);
        repTree.setMinVarianceProp(v);
        repTree.setNumFolds(n);
        Evaluation evaluation;
        try {
            evaluation = new Evaluation(dataInstances);
            evaluation.crossValidateModel(repTree, dataInstances, 10, new Random(1));
        } catch (Exception e) {
            return null;
        }
        return evaluation;
    }

    /**
     * Performs REPTree regression tree analysis for m=0..1000 (MIN_NUMBER_OF_INSTANCES_PER_LEAF_MIN and MIN_NUMBER_OF_INSTANCES_PER_LEAF_MAX)
     * with step 5 (MIN_NUMBER_OF_INSTANCES_PER_LEAF_STEP), v=1e-4..1e-2 (MIN_VARIANCE_FOR_SPLIT_MIN and MIN_VARIANCE_FOR_SPLIT_MAX)
     * with step of 1e-4 (MIN_VARIANCE_FOR_SPLIT_STEP) and n=1..10 (NUMBER_OF_FOLDS_MIN and NUMBER_OF_FOLDS_MAX)
     * with step of 1 (NUMBER_OF_FOLDS_STEP)
     * When something goes wrong during search, the result of this search is not included in result set.
     *
     * @param analysis      analysis details which belongs to returned search result
     * @param dataInstances dataset instances
     * @return Set of RegressionTreeResult instances
     */
    public Set<AnalysisResult> REPTreeSearch(Analysis analysis, Instances dataInstances) {
        Set<AnalysisResult> results = new HashSet<AnalysisResult>();

        for (int m = MIN_NUMBER_OF_INSTANCES_PER_LEAF_MIN; m <= MIN_NUMBER_OF_INSTANCES_PER_LEAF_MAX; m += MIN_NUMBER_OF_INSTANCES_PER_LEAF_STEP) {
            for (double v = MIN_VARIANCE_FOR_SPLIT_MIN; v <= MIN_VARIANCE_FOR_SPLIT_MAX; v += MIN_VARIANCE_FOR_SPLIT_STEP) {
                for (int n = NUMBER_OF_FOLDS_MIN; n <= NUMBER_OF_FOLDS_MAX; n += NUMBER_OF_FOLDS_STEP) {
                    Evaluation trained = repTreeAnalysis(dataInstances, m, v, n);
                    if (trained != null) {
                        double rmse = trained.rootMeanSquaredError();
                        double meanAbsoluteError = trained.meanAbsoluteError();
                        int correct = (int) trained.correct();
                        int incorrect = (int) trained.incorrect();
                        String summary = trained.toSummaryString();
                        AnalysisResult res = new RegressionTreeResult(analysis.getId(), rmse, meanAbsoluteError, correct, incorrect, summary, m, v, n);
                        results.add(res);
                    }
                }
            }
        }
        return results;
    }
}

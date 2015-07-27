package sk.upjs.winston.computation;

import sk.upjs.winston.algorithms.DecisionTreeModel;
import sk.upjs.winston.algorithms.KnnModel;
import sk.upjs.winston.algorithms.LogisticRegressionModel;
import sk.upjs.winston.algorithms.SvmModel;
import sk.upjs.winston.model.*;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

/**
 * Created by stefan on 2/16/15.
 */
public class PatternMiningModeling extends Modeling {


    @Override
    public void performRecommendedDataMiningMethodForAnalysis(Analysis analysis) throws IOException {

    }

    @Override
    public void performAnalysisWithDefaultHyperparameters(Analysis analysis) throws IOException {

    }

    @Override
    protected void gridSearch(Analysis analysis, Instances dataInstances) {

    }

    @Override
    protected double computeDistanceForAnalyses(Analysis analysis1, Analysis analysis2) {
        return 0;
    }
}

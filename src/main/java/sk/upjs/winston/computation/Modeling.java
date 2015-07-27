package sk.upjs.winston.computation;

import sk.upjs.winston.database.DatabaseManager;
import sk.upjs.winston.helper.FileManipulationUtilities;
import sk.upjs.winston.helper.Mailer;
import sk.upjs.winston.model.Analysis;
import sk.upjs.winston.model.AnalysisResult;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by stefan on 2/16/15.
 */
public abstract class Modeling {



    //pattern mining weights
    protected static final double WEIGHT_APRIORI = 0.5d;
    protected static final double WEIGHT_SIMPLE_K_MEANS = 0.5d;




    protected static final double INSTANCES_INTERVAL_SIZE = 12960 - 15d;
    protected static final double ATTRIBUTES_INTERVAL_SIZE = 71 - 1d;
    protected static final double MISSING_VALUES_INTERVAL_SIZE = 19692d;
    protected DatabaseManager databaseManager = new DatabaseManager();

    public abstract void performRecommendedDataMiningMethodForAnalysis(Analysis analysis) throws IOException;
    public abstract void performAnalysisWithDefaultHyperparameters(Analysis analysis) throws IOException;
    protected abstract void gridSearch(Analysis analysis, Instances dataInstances);
    protected abstract double computeDistanceForAnalyses(Analysis analysis1, Analysis analysis2);

    public void performGridsearchAnalysisForFile(Analysis analysis) throws IOException {
        analysis.setGridSearchAnalysisInProgress(true);
        databaseManager.updateAnalysis(analysis);
        File arffFile = getArffFileForAnalysis(analysis);

        BufferedReader reader = new BufferedReader(
                new FileReader(arffFile));
        Instances dataInstances = new Instances(reader);
        reader.close();
        dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

        String email = analysis.getDataset().getUser().getEmail();
        String filename = analysis.getDataFile();
        long analysisId = analysis.getId();

        gridSearch(analysis, dataInstances);
        informUserByEmailAboutGridSearchResults(email, filename, analysisId);
    }

    protected AnalysisResult getRecommendedMethod(Analysis analysis) {
        Analysis mostSimilar = getMostSimilarAnalysis(analysis);
        if (mostSimilar == null) {
            return null;
        }
        return getBestMethodForAnalysis(mostSimilar);
    }

    protected Analysis getMostSimilarAnalysis(Analysis analysis) {
        List<Analysis> processedAnalyses = databaseManager.analysesAnalyzedByGridSearchForTask(analysis.getTask());

        if (processedAnalyses == null || processedAnalyses.size() == 0) {
            return null;
        }

        Analysis mostSimilar = processedAnalyses.get(0);
        double distance = Double.MAX_VALUE;

        for (Analysis processedAnalysis : processedAnalyses) {
            if (processedAnalysis.getId() != analysis.getId()) {
                double actualDistance = computeDistanceForAnalyses(processedAnalysis, analysis);
                if (actualDistance < distance) {
                    distance = actualDistance;
                    mostSimilar = processedAnalysis;
                }
            }
        }
        return mostSimilar;
    }

    /**
     * HELPER METHODS
     */

    private AnalysisResult getBestMethodForAnalysis(Analysis datasetAnalysis) {
        return databaseManager.bestMethodForAnalysis(datasetAnalysis);
    }

    protected File getArffFileForAnalysis(Analysis analysis) {
        String filepath = FileManipulationUtilities.PREPARED_DATAFILES_DIRECTORY + "/" + analysis.getDataFile();
        return new File(filepath);
    }

    public void informUserByEmailAboutGridSearchResults(String email, String dataFileName, long analysisId) {
        System.out.println("preparing to send email");
        String subject = "Winston - analysis finished: " + dataFileName;
        String body = "Hello,\n\n results are waiting for you at\n\n" + Mailer.WEB_SERVER_URL + "/winston/analysis/show/" + analysisId + "\n\nThank you!";
        Mailer.sendEmail(email, subject, body);
        System.out.println("mail sent");
    }
}

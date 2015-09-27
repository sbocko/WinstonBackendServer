package sk.upjs.winston.server;

import sk.upjs.winston.computation.*;
import sk.upjs.winston.database.DatabaseManager;
import sk.upjs.winston.helper.FileManipulationUtilities;
import sk.upjs.winston.model.Analysis;
import sk.upjs.winston.model.Attribute;
import sk.upjs.winston.model.Dataset;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by stefan on 2/14/15.
 */
public class RequestProcessor implements Runnable {
    private static final String COMMAND_PREPROCESS = "preprocess";
    private static final String COMMAND_GRID_SEARCH = "grid_search";
    private static final String COMMAND_FILE_REQUEST = "file_request";

    private static final String RETURN_CODE_OK = "200: OK";
    private static final String RETURN_CODE_ERR = "400: ERR";

    private Socket connection;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;

    public RequestProcessor(Socket connection) throws IOException {
        this.connection = connection;
        InputStream in = connection.getInputStream();
        dataInput = new DataInputStream(in);
        OutputStream out = connection.getOutputStream();
        dataOutput = new DataOutputStream(out);
    }

    public void run() {
        try {
            String command = dataInput.readUTF();
            System.out.println("RECEIVED: " + command);

            if (COMMAND_PREPROCESS.equals(command)) {
                processCommandPreprocessing();
            } else if (COMMAND_GRID_SEARCH.equals(command)) {
                processCommandGridSearch();
            } else if (COMMAND_FILE_REQUEST.equals(command)) {
                processCommandFileRequest();
            } else {
                System.out.println("UNKNOWN COMMAND: " + command);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                sendResponseCode(RETURN_CODE_ERR);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("THREAD FINISHED...");
        }
    }


    /**
     * HELPER METHODS
     */

    private void processCommandFileRequest() throws IOException {
        String filename = dataInput.readUTF();
        String filepath = FileManipulationUtilities.PREPARED_DATAFILES_DIRECTORY + "/" + filename;
        sendDataFile(filepath);
    }

    private void processCommandGridSearch() throws IOException {
        long analysisId = dataInput.readLong();
        DatabaseManager databaseManager = new DatabaseManager();

        Analysis toAnalyze = databaseManager.getAnalysis(analysisId);
        System.out.println("GRID SEARCH FOR: " + toAnalyze);

        String task = toAnalyze.getTask();
        Modeling modeling;
        if (Analysis.TASK_CLASSIFICATION.equals(task)) {
            modeling = new ClassificationModeling();
        } else if (Analysis.TASK_REGRESSION.equals(task)) {
            modeling = new RegressionModeling();
        } else if (Analysis.TASK_PATTERN_MINING.equals(task)) {
            modeling = new PatternMiningModeling();
        } else {
            sendResponseCode(RETURN_CODE_ERR);
            return;
        }
        modeling.performGridsearchAnalysisForFile(toAnalyze);

        sendResponseCode(RETURN_CODE_OK);
    }

    private void processCommandPreprocessing() throws IOException {
        long datasetId = dataInput.readLong();
        DatabaseManager databaseManager = new DatabaseManager();

        Dataset toPreprocess = databaseManager.getDataset(datasetId);
        String task = dataInput.readUTF();

        System.out.println("PREPROCESSING: " + toPreprocess + ", TASK: " + task);

        Attribute target = null;
        Map<Attribute, Boolean> attributesToSplit = new HashMap<Attribute, Boolean>();

        if (!Analysis.TASK_PATTERN_MINING.equals(task)) {
            long targetAttributeId = dataInput.readLong();
            target = databaseManager.getAttribute(targetAttributeId);
            System.out.println("TARGET ATTRIBUTE POSITION: " + target.getPositionInDataFile());

            int numberOfAttributesToBinarize = dataInput.readInt();
            System.out.println("ATTRS TO BINARIZE: " + numberOfAttributesToBinarize);
            Set<Long> attributeIds = new HashSet<Long>(numberOfAttributesToBinarize);
            for (int i = 0; i < numberOfAttributesToBinarize; i++) {
                System.out.println("ATTRIBUTE IDS: " + attributeIds);
                attributeIds.add(dataInput.readLong());
            }
            attributesToSplit = new HashMap<Attribute, Boolean>();
            for (Attribute attribute : toPreprocess.getAttributes()) {
                if (attributeIds.contains(attribute.getId())) {
                    attributesToSplit.put(attribute, true);
                } else {
                    attributesToSplit.put(attribute, false);
                }
            }
            System.out.println(attributesToSplit);
        }


        File dataFile = receiveDataFile(toPreprocess.getArffDataFile());

        System.out.println("file received");

        Analyzer analyzer = new Analyzer();

        if (task.equals(Analysis.TASK_CLASSIFICATION) || task.equals(Analysis.TASK_REGRESSION) || task.equals(Analysis.TASK_PATTERN_MINING)) {
            System.out.println("DETECTED TASK: " + task);
            analyzer.generateDefaultAnalysis(toPreprocess, task, dataFile, attributesToSplit, target);
            sendResponseCode(RETURN_CODE_OK);

            analyzer.generateAnalyzes(toPreprocess, task, dataFile, attributesToSplit, target);
            return;
        }

        System.out.println("UNKNOWN TASK");
        sendResponseCode(RETURN_CODE_ERR);
    }

    private File receiveDataFile(String filename) throws IOException {
        File received = FileManipulationUtilities.createFileForPath(FileManipulationUtilities.DATA_FILES_DIRECTORY + "/" + filename);
        FileOutputStream fos = new FileOutputStream(received);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        int bufferSize = connection.getReceiveBufferSize();
        byte[] bytes = new byte[bufferSize];
        int count;
        while ((count = dataInput.read(bytes)) > 0) {
            bos.write(bytes, 0, count);
        }

        bos.flush();
        bos.close();
        fos.close();
        return received;
    }

    private void sendResponseCode(String returnCode) throws IOException {
        dataOutput.writeUTF(returnCode);
    }

    private void sendDataFile(String filepath) throws IOException {
        File file = new File(filepath);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            System.out.println("File is too large.");
            return;
        }

        byte[] bytes = new byte[(int) length];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);

        int count;
        while ((count = bis.read(bytes)) > 0) {
            dataOutput.write(bytes, 0, count);
        }

        dataOutput.flush();
        fis.close();
        bis.close();
    }

}

package sk.upjs.winston.server;

import sk.upjs.winston.computation.Analyzer;
import sk.upjs.winston.database.DatabaseManager;
import sk.upjs.winston.helper.FileManipulationUtilities;
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
    private static final String COMMAND_GET_FILE = "get_file";

    private static final String RETURN_CODE_OK = "200: OK";
    private static final String RETURN_CODE_ERR = "400: ERR";

    private Socket connection;
    private DataInputStream dataInput;

    public RequestProcessor(Socket connection) throws IOException {
        this.connection = connection;
        InputStream in = connection.getInputStream();
        dataInput = new DataInputStream(in);
    }

    @Override
    public void run() {
        try {
            String command = dataInput.readUTF();
            System.out.println("RECEIVED: " + command);

            if (COMMAND_PREPROCESS.equals(command)) {
                processCommandPreprocessing();
            } else if (COMMAND_GRID_SEARCH.equals(command)) {
                // TODO
            } else if (COMMAND_GET_FILE.equals(command)) {
                // TODO
            }
            sendResponseCode(RETURN_CODE_OK);
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

    private void processCommandPreprocessing() throws IOException {
        long datasetId = dataInput.readLong();
        DatabaseManager databaseManager = new DatabaseManager();

        Dataset toPreprocess = databaseManager.getDataset(datasetId);
        System.out.println("PREPROCESSING: " + toPreprocess);

        long targetAttributeId = dataInput.readLong();
        Attribute target = databaseManager.getAttribute(targetAttributeId);

        int numberOfAttributesToBinarize = dataInput.readInt();
        Set<Long> attributeIds = new HashSet<Long>(numberOfAttributesToBinarize);
        for (int i = 0; i < numberOfAttributesToBinarize; i++) {
            attributeIds.add(dataInput.readLong());
        }
        Map<Attribute, Boolean> attributesToSplit = new HashMap<Attribute, Boolean>();
        for (Attribute attribute : toPreprocess.getAttributes()) {
            if (attributeIds.contains(attribute.getId())) {
                attributesToSplit.put(attribute, true);
            } else {
                attributesToSplit.put(attribute, false);
            }
        }
        System.out.println(attributesToSplit);

        File dataFile = receiveDataFile(toPreprocess.getArffDataFile());

        Analyzer analyzer = new Analyzer();
        analyzer.generateAnalyzes(toPreprocess, dataFile, attributesToSplit, target);
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
        OutputStream out = connection.getOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(out);
        dataOutput.writeUTF(returnCode);
    }

}

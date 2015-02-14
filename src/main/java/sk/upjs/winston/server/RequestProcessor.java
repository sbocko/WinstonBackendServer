package sk.upjs.winston.server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by stefan on 2/14/15.
 */
public class RequestProcessor implements Runnable {
    private static final String COMMAND_PREPROCESS = "preprocess";
    private static final String COMMAND_GRID_SEARCH = "grid_search";
    private static final String COMMAND_GET_FILE = "get_file";

    private static final String RETURN_CODE_OK = "200: OK";
    private static final String RETURN_CODE_ERR = "400: ERR";

    private static final String DATA_FILES_DIRECTORY = "datasets";

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
        }
    }

    /**
     * HELPER METHODS
     */

    private void processCommandPreprocessing() throws IOException {
        long datasetId = dataInput.readLong();
        long targetAttributeId = dataInput.readLong();
        int numberOfAttributesToBinarize = dataInput.readInt();

        long[] attributeIds = new long[numberOfAttributesToBinarize];
        for (int i = 0; i < attributeIds.length; i++) {
            attributeIds[i] = dataInput.readLong();
        }

        File dataFile = receiveDataFile("test.txt");

        System.out.println(Arrays.toString(attributeIds));
    }

    private File receiveDataFile(String filename) throws IOException {
        File received = createFileForPath(DATA_FILES_DIRECTORY + "/" + filename);
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

    private File createFileForPath(String filepath) throws IOException {
        File created = new File(filepath);
        created.getParentFile().mkdirs();
        created.createNewFile();
        return created;
    }

    private void sendResponseCode(String returnCode) throws IOException {
        OutputStream out = connection.getOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(out);
        dataOutput.writeUTF(returnCode);
    }

}

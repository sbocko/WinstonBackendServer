package sk.upjs.winston;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int DEFAULT_PORT_NUMBER = 4322;
    private static int portNumber;



    public static void main(String[] args) throws IOException {
        assignPortNumber(args);
        ServerSocket server = createServerSocket(portNumber);

        while (true) {
            processRequest(server);
        }

    }

    /**
     * HELPER METHODS
     */

    private static void processRequest(ServerSocket serverSocket) {
        try {
            Socket client = serverSocket.accept();

            InputStream in = client.getInputStream();
            DataInputStream dIn = new DataInputStream(in);
            String received = dIn.readUTF();
            System.out.println("RECEIVED: " + received);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ServerSocket createServerSocket(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        return serverSocket;
    }

    private static void assignPortNumber(String[] args) {
        try {
            portNumber = Integer.parseInt(args[0]);
            return;
        } catch (Exception ignore) {
            System.out.println("usign default port (" + DEFAULT_PORT_NUMBER + ")");
        }
        portNumber = DEFAULT_PORT_NUMBER;
    }
}

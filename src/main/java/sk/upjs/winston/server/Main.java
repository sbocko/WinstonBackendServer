package sk.upjs.winston.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int DEFAULT_PORT_NUMBER = 4322;
    private static int portNumber;


    public static void main(String[] args) throws IOException {
        assignPortNumber(args);
        ServerSocket server = createServerSocket(portNumber);
        while (true) {
            acceptRequest(server);
        }
    }

    /**
     * HELPER METHODS
     */

    private static void acceptRequest(ServerSocket serverSocket) {
            try {
                Socket client = serverSocket.accept();
                RequestProcessor request = new RequestProcessor(client);
                Thread async = new Thread(request);
                async.start();
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

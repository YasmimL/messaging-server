package br.com.ifce.network.socket;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketServer {

    private static final SocketServer INSTANCE = new SocketServer();

    private static final int PORT = 1234;

    private ServerSocket socket;

    public static SocketServer getInstance() {
        return INSTANCE;
    }

    public void start() {
        try {
            this.socket = new ServerSocket(PORT);
            this.listenForClients();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForClients() {
        try {
            while (true) {
                final var client = this.socket.accept();
                final var clientHandler = new ClientHandler(client);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

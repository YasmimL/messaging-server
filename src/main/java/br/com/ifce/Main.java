package br.com.ifce;

import br.com.ifce.network.rmi.OfflineMessagingServiceProvider;
import br.com.ifce.network.socket.SocketServer;

public class Main {
    public static void main(String[] args) {
        OfflineMessagingServiceProvider.lookup();

        final var server = SocketServer.getInstance();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
    }
}
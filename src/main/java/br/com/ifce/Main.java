package br.com.ifce;

import br.com.ifce.network.socket.SocketServer;

public class Main {
    public static void main(String[] args) {
        final var server = SocketServer.getInstance();
        server.start();
//        getRuntime().addShutdownHook(new Thread(server::close));
    }
}
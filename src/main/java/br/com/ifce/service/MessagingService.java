package br.com.ifce.service;

import br.com.ifce.network.socket.ClientHandler;

import java.util.HashMap;
import java.util.List;

public class MessagingService {

    private final HashMap<String, ClientHandler> clients = new HashMap<>();

    private static final MessagingService INSTANCE = new MessagingService();

    public static MessagingService getInstance() {
        return INSTANCE;
    }

    public void addClient(String username, ClientHandler handler) {
        this.clients.put(username, handler);
        // TODO: registar usuário no servidor de mensagens offline
    }

    public ClientHandler getClient(String username) {
        return this.clients.get(username);
    }

    public void removeClient(String username) {
        this.clients.remove(username);
    }

    public List<String> getAllUsernames() {
        return this.clients.keySet().stream().toList();
    }

    public List<ClientHandler> getAllClientsExcept(String username) {
        return this.clients.keySet().stream()
            .filter(client -> !client.equals(username))
            .map(this.clients::get)
            .toList();
    }
}

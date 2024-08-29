package br.com.ifce.network.socket;

import br.com.ifce.model.ChatMessage;
import br.com.ifce.model.Message;
import br.com.ifce.model.enums.ClientStatus;
import br.com.ifce.model.enums.MessageType;
import br.com.ifce.service.MessagingService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    private boolean interrupted = false;

    private ClientStatus clientStatus = ClientStatus.ONLINE;

    private String clientUsername;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        while (!this.interrupted) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(this.clientSocket.getInputStream());
                this.onMessage((Message<?>) inputStream.readObject());
            } catch (Exception e) {
                e.printStackTrace();
                this.close();
            }
        }
    }

    private void close() {
        this.interrupted = true;
    }

    private void onMessage(Message<?> message) {
//        final var service = MessagingService.getInstance();
        switch (message.getType()) {
            case SUBSCRIBE -> this.subscribeClient((String) message.getPayload());
            case GO_ONLINE -> this.clientStatus = ClientStatus.ONLINE;
            case GO_OFFLINE -> this.clientStatus = ClientStatus.OFFLINE;
            case GET_USERS_REQUEST -> this.handleGetUsersRequest();
            case CHAT_REQUEST -> this.handleChatMessageRequest((ChatMessage) message.getPayload());
        }
    }

    private void subscribeClient(String username) {
        this.clientUsername = username;
        MessagingService.getInstance().addClient(username, this);
    }

    public void send(Message<?> message) {
        try {
            var outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
            outputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGetUsersRequest() {
        this.send(new Message<>(
            MessageType.GET_USERS_RESPONSE,
            MessagingService.getInstance().getAllUsernames()
                .stream().filter(username -> !this.clientUsername.equals(username))
                .toList()
        ));
    }

    public boolean online() {
        return ClientStatus.ONLINE.equals(this.clientStatus);
    }

    public boolean offline() {
        return ClientStatus.OFFLINE.equals(this.clientStatus);
    }

    private void handleChatMessageRequest(ChatMessage chatMessage) {
        final var message = new Message<>(
            MessageType.CHAT_RESPONSE,
            chatMessage
        );

        final var receiver = MessagingService.getInstance().getClient(chatMessage.to());
        if (receiver.online()) {
            receiver.send(message);
        } else {
            // TODO: enviar mensagem para o servidor de mensagens offline
        }
        this.send(message);
    }

    public ClientStatus getClientStatus() {
        return clientStatus;
    }
}

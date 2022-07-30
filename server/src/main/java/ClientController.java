import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientController implements Runnable, Writer {

    private final ClientService clientService;
    private final Socket clientSocket;
    private ObjectOutputStream writer;
    private String login;
    private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);

    public ClientController(ClientService clientService, Socket socket) {
        this.clientService = clientService;
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
            while (true) {
                ClientMessage lastMessage = (ClientMessage) reader.readObject();
                ClientMessage.ClientMessageType type = lastMessage.type();
                if (login == null){
                    if (type != ClientMessage.ClientMessageType.REGISTRATION) {
                        String errorMessage = "The server was waiting for the client to register but received a different message";
                        ServerMessage.ServerMessageType failedRegistrationType = ServerMessage.ServerMessageType.FAILED_REGISTRATION;
                        write(new ServerMessage(errorMessage, failedRegistrationType, null, null));
                        return;
                    }
                }
                else {
                    LOG.debug("User=" + login + " registration");
                }
                LOG.debug("Get message \""+ lastMessage.message() + "\"" + " Type=" + type + " From=" + login);
                receive(lastMessage);
                if (type == ClientMessage.ClientMessageType.EXIT) break;
            }
        }
        catch (IOException | ClassNotFoundException e){
            LOG.error("ClientService.Client " + login + " not available because of exception:", e);
        }
        finally {
            closeSocket();
        }
    }

    public void receive(ClientMessage message) {
        switch (message.type()) {
            case REGISTRATION ->{
                String clientName = message.message();
                String reason = getReasonIncorrectName(clientName);

                if (!clientService.register(message.message(), this)) {
                    reason = "Client with the same name already exists, try again: ";
                }

                if (reason != null) {
                    ServerMessage.ServerMessageType failedRegistrationType = ServerMessage.ServerMessageType.FAILED_REGISTRATION;
                    write(new ServerMessage(reason, failedRegistrationType, null, null));
                }
                else {
                    login = message.message();
                    ServerBroadMessage.ServerBroadMessageType newClientType = ServerBroadMessage.ServerBroadMessageType.NEW_CLIENT;
                    clientService.sendAll(new ServerBroadMessage(login + " successful registration", newClientType, null));
                }
            }
            case SEND_EVERYBODY -> {
                ServerBroadMessage.ServerBroadMessageType everybodyType = ServerBroadMessage.ServerBroadMessageType.MESSAGE;
                clientService.sendAll(new ServerBroadMessage(message.message(), everybodyType, login));
            }
            case SEND_USER -> {
                ServerClientMessage.ServerClientMessageType sendClientType = ServerClientMessage.ServerClientMessageType.MESSAGE;
                clientService.sendTo(new ServerClientMessage(message.message(), sendClientType, login, login));
                clientService.sendTo(new ServerClientMessage(message.message(), sendClientType, login, message.receiver()));
            }
            case SHOW_CLIENTS -> {
                String names = clientService.getClientNames().toString();
                ServerClientMessage.ServerClientMessageType showUserListType = ServerClientMessage.ServerClientMessageType.CLIENTS_LIST;
                clientService.sendTo(new ServerClientMessage(names, showUserListType, null, login));
            }
            case EXIT -> {
                clientService.sendTo(new ServerClientMessage("You left chat", ServerClientMessage.ServerClientMessageType.EXIT, login, login));
                clientService.sendAll(new ServerBroadMessage(login + " left chat", ServerBroadMessage.ServerBroadMessageType.MESSAGE, login));
                clientService.delete(login);
            }
        }
    }

    private String getReasonIncorrectName(String name) {
        if (name.charAt(0) == '@') return "login should not start with '@'";
        if (containsWhitespace(name)) return "Login must not contain white spaces, try again: ";
        if (name.equals("/exit") || name.equals("/list")) return "Login must not be a command, try again: ";
        return null;
    }

    private boolean containsWhitespace(String string){
        return string.indexOf(' ') != -1;
    }


    private void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(ServerMessage message) {
        try{
            writer.writeObject(message);
            writer.flush();
        }
        catch (IOException e) {
            LOG.error("Failed to send message to client because of exception: " + e.getMessage());
        }
    }
}

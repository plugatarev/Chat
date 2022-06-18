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
                Message lastMessage = (Message) reader.readObject();
                MessageType type = MessageUtils.getMessageType(lastMessage);
                if (login == null){
                    if (type != MessageType.REGISTRATION) {
                        String errorMessage = "The server was waiting for the client to register but received a different message";
                        write(new ClientMessage(errorMessage, ClientMessage.ClientMessageType.NOT_REGISTRATION, null, null));
                        return;
                    }
                }
                else {
                    LOG.debug("User=" + login + " registration");
                }
                LOG.debug("Get message \""+ lastMessage.message() + "\"" + " Type=" + type + " From=" + login);
                receive(lastMessage);
                if (type == MessageType.EXIT) break;
            }
        }
        catch (IOException | ClassNotFoundException e){
            LOG.error("ClientService.Client " + login + " not available because of exception:", e);
        }
        finally {
            closeSocket();
        }
    }

    public void receive(Message message) {
        // CR: i think it's better to handle message types separately, in some cases you won't need to create message again
        MessageType type = MessageUtils.getMessageType(message);
        switch (type) {
            case REGISTRATION ->{
                String clientName = message.message();
                String reason = getReasonIncorrectName(clientName);
                boolean isRegister = reason == null && clientService.register(message.message(), this);
                if (!isRegister) {
                    if (reason == null) reason = "Client with the same name already exists, try again: ";
                    write(new ClientMessage(reason, ClientMessage.ClientMessageType.NOT_REGISTRATION, null, null));
                }
                else {
                    login = message.message();
                    BroadMessage.BroadMessageType type1 = BroadMessage.BroadMessageType.REGISTRATION;
                    clientService.sendAll(new BroadMessage(login + " successful registration", type1, login));
                }
            }
            case SEND_EVERYBODY -> {
                BroadMessage.BroadMessageType everybody = BroadMessage.BroadMessageType.SEND_EVERYBODY;
                clientService.sendAll(new BroadMessage(message.message(), everybody, login));
            }
            case SEND_USER -> {
                ClientMessage clientMessage = MessageUtils.tryCast(message, ClientMessage.class);
                ClientMessage.ClientMessageType type1 = ClientMessage.ClientMessageType.SEND_USER;
                clientService.sendTo(new ClientMessage(message.message(), type1, login, login));
                clientService.sendTo(clientMessage);
            }
            case SHOW_USERS -> {
                String names = clientService.getClientNames().toString();
                clientService.sendTo(new ClientMessage(names, ClientMessage.ClientMessageType.SHOW_USERS, null, login));
            }
            case EXIT -> {
                clientService.sendTo(new ClientMessage("You left chat", ClientMessage.ClientMessageType.EXIT, login, login));
                clientService.sendAll(new BroadMessage(login + " left chat", BroadMessage.BroadMessageType.SEND_EVERYBODY, login));
                clientService.delete(login);
            }
            default -> {
                String errorMessage = "You send message with invalid message type";
                clientService.sendTo(new ClientMessage(errorMessage, ClientMessage.ClientMessageType.EXIT, login, login));
                throw new IllegalStateException("The server received a message with invalid message type");
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
    public void write(Message message) {
        try{
            writer.writeObject(message);
            writer.flush();
        }
        catch (IOException e) {
            LOG.error("Failed to send message to client because of exception: " + e.getMessage());
        }
    }
}

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
                Object o = reader.readObject();
                BroadMessage broadMessage = tryCast(o, BroadMessage.class);
                if (broadMessage == null) {
                    UserMessage userMessage = tryCast();

                }
                Message lastMessage = (Message) o;
                if (login == null){
                    if (lastMessage.type() != MessageType.REGISTRATION) {
                        write(new Message("The server was waiting for the client to register but received a different message", MessageType.NOT_REGISTRATION));
                        return;
                    }
                }
                else {
                    LOG.debug("User=" + login + " registration");
                }
                LOG.debug("Get message \""+ lastMessage.message() + "\"" + " Type=" + lastMessage.type() + " From=" + login);
                receive(lastMessage);
                if (lastMessage.type() == MessageType.EXIT) break;
            }
        }
        catch (IOException | ClassNotFoundException e){
            LOG.error("ClientService.Client " + login + " not available because of exception:", e);
        }
        finally {
            closeSocket();
        }
    }

    private static<T> T tryCast(Object obj, Class<T> clazz) {
        // TODO: reflection
        return null;
    }

    public void receive(Message message) {
        MessageType type = message.type();
        switch (type) {
            case REGISTRATION ->{
                String clientName = message.message();
                boolean isRegister = isValid(clientName) && clientService.register(message.message(), this);
                if (!isRegister) {
                    write(new Message(getReasonIncorrectName(clientName), MessageType.NOT_REGISTRATION));
                }
                else {
                    login = message.message();
                    clientService.sendAll(new BroadMessage(login + " successful registration", type, login));
                }
            }
            case SEND_EVERYBODY -> clientService.sendAll(new BroadMessage(message.message(), type, login));
            case SEND_USER -> {
                clientService.sendTo(new UserMessage(message.message(), type, login, login));
                clientService.sendTo(new UserMessage(message.message(), type, login, message.receiverName()));
            }
            case SHOW_USERS -> {
                String names = clientService.getClientNames().toString();
                clientService.sendTo(new UserMessage(names, type, null, login));
            }
            case EXIT -> {
                clientService.sendTo(new UserMessage("You left chat", type, login, login));
                clientService.sendAll(new BroadMessage(login + " left chat", MessageType.SEND_EVERYBODY, login));
                clientService.delete(login);
            }
            default -> {
                clientService.sendTo(new UserMessage("You send message with invalid message type", MessageType.EXIT, login, login));
                throw new IllegalStateException("The server received a message with invalid message type");
            }
        }
    }

    private String getReasonIncorrectName(String name) {
        if (containsWhitespace(name)) return "Login must not contain white spaces, try again: ";
        if (name.equals("/exit") || name.equals("/list") || name.charAt(0) == '@') return "Login must not be a command, try again: ";
        return "This login is busy, try again: ";
    }

    private boolean isValid(String name){
        return !name.equals("/exit") && !name.equals("/list") && !containsWhitespace(name) && name.charAt(0) != '@';
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
            e.printStackTrace();
        }
    }
}

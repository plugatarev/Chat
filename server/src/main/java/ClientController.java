import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientController implements Runnable{

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
        try (ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream())){
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
            while (true) {
                Message lastMessage = (Message) reader.readObject();
                if (login == null){
                    if (lastMessage.type() != MessageType.REGISTRATION) {
                        try{
                            writer.writeObject(new Message("The server was waiting for the client to register but received a different message", MessageType.NOT_REGISTRATION));
                            writer.flush();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
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
            LOG.error("Client " + login + " not available because of exception:", e);
        }
        finally {
            close();
        }
    }

    public void receive(Message message) {
        switch (message.type()) {
            case REGISTRATION ->{
                String clientName = message.message();
                boolean isRegister = !clientName.equals("/exit") && !clientName.equals("/list") && !containsWhitespace(clientName)
                        && clientName.charAt(0) != '@' && clientService.register(message.message(), writer);

                if (!isRegister) {
                    try{
                        writer.writeObject(new Message("This name is busy or incorrect, try again: ", MessageType.NOT_REGISTRATION));
                        writer.flush();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    login = message.message();
                    clientService.sendAll(login, new Message(login + " successful registration", MessageType.REGISTRATION));
                }
            }
            case SEND_EVERYBODY -> clientService.sendAll(login, message);
            case SEND_USER -> clientService.send(message.receiverName(), message);
            case SHOW_USERS -> {
                String names = clientService.getClientNames().toString();
                clientService.send(login, new Message(names, MessageType.SHOW_USERS, login));
            }
            case EXIT -> {
                clientService.send(login, new Message("You left chat", MessageType.EXIT, login));
                clientService.sendAll(login, new Message(login + " left chat", MessageType.SEND_EVERYBODY, login));
                clientService.delete(login);
            }
            default -> throw new IllegalStateException("The server received a message with invalid message type");
        }
    }

    private boolean containsWhitespace(String string){
        for (int i = 0; i < string.length(); i++){
            if (string.charAt(i) == ' ') return true;
        }
        return false;
    }

    private void close() {
        if (clientSocket != null) {
            try {
                writer.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

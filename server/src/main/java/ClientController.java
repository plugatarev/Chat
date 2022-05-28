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
        try {
            ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
            while (true) {
                Message lastMessage = (Message) reader.readObject();
                if (login == null) lastMessage.setType(MessageType.REGISTRATION);
                else LOG.debug("User=" + login + " registration");
                LOG.debug("Get message \""+ lastMessage.getMessage() + "\"" + " Type=" + lastMessage.getType()
                        + " From=" + login);
                receive(lastMessage);
                if (lastMessage.getType() == MessageType.EXIT) break;
            }
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            //TODO
        }
        finally {
            close();
        }
    }

    public void receive(Message message) {
        switch (message.type) {
            case SEND_EVERYBODY, SEND_USER -> clientService.send(new Message(message.getMessage(), message.getType(), login, message.getReceiverName()));
            case SHOW_USERS -> {
                String names = clientService.getOnlineUsers().toString();
                clientService.send(new Message(names, MessageType.SHOW_USERS, login, login));
            }
            case REGISTRATION ->{
                boolean isRegister = clientService.register(new Client(message.getMessage(), writer));
                if (!isRegister) {
                    clientService.write(new Message("This name is busy or incorrect, try again: ", MessageType.NOT_REGISTRATION), writer);
                }
                else {
                    login = message.getMessage();
                    clientService.send(new Message("Successful registration", MessageType.SEND_USER, null, login));
                    clientService.send(new Message("Successful registration", MessageType.SEND_EVERYBODY, login));
                }
            }
            case EXIT -> {
                clientService.send(new Message("You are left chat", MessageType.EXIT, null, login));
                clientService.send(new Message(login + "left chat", MessageType.SEND_EVERYBODY, login));
                clientService.delete(new Client(login, writer));
            }
        }
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

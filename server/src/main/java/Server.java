import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import org.slf4j.*;

public class Server implements Runnable{
    private static final int PORT = 8080;
    private ServerSocket socket;
    private final HashMap<User, ObjectOutputStream> clients = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public List<String> getOnlineUsersName(){
        return clients.keySet().stream().map(User::getName).toList();
    }

    public void removeOnlineUser(User user){
        clients.remove(user);
    }

    @Override
    public void run() {
        try {
            socket = new ServerSocket();
            socket.bind(new InetSocketAddress("localhost", PORT));
            LOG.debug("Server starting on port " + PORT);
            while (true) {
                Socket clientSocket = socket.accept();
                LOG.debug("Client started...");
                Thread t = new Thread(new Controller(clientSocket, this));
                t.start();
            }
        }
        catch (IOException ex){
            LOG.debug("The server has finished working");
        }
        finally {
            close();
        }
    }

    public void sendMessage(Message message, ObjectOutputStream writer){
        switch (message.getType()) {
            case REGISTRATION, SEND_EVERYBODY -> sendToEverybody(message);
            case SHOW_USERS ->{
                message.setMessage(getOnlineUsersName().toString());
                sendToUser(message, writer);
            }
            case NOT_REGISTRATION -> sendToUser(message, writer);
            case SEND_USER -> {
                User dst = new User(Message.getReceiverName(message.getMessage()));
                if (clients.containsKey(dst)){
                    sendToUser(new Message(Message.getMessageFromAddressedMessage(message.getMessage()),
                                    MessageType.SEND_USER, message.getSenderName()), clients.get(dst));
                }
                else{
                    message.setMessage("Such user doesn't exists");
                    sendToUser(message, writer);
                }
            }
            case EXIT -> {
                LOG.debug(message.getSenderName() + " left chat");
                sendToUser(new Message("You left chat", MessageType.EXIT), writer);
                sendToEverybodyExceptUser(new Message("left chat", MessageType.SEND_USER, message.getSenderName()), writer);
            }
        }
    }

    private void sendToUser(Message message, ObjectOutputStream writer){
        try{
            writer.writeObject(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(){
        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void sendToEverybody(Message message) {
        clients.entrySet()
               .stream()
               .filter(c -> !c.getKey().getName().equals(message.getSenderName()))
               .forEach(c -> sendToUser(message, c.getValue()));
    }

    void sendToEverybodyExceptUser(Message message, ObjectOutputStream user){
        clients.entrySet().stream().filter(c -> !c.getValue().equals(user)).forEach(c -> sendToUser(message, c.getValue()));
    }

    public boolean addUser(User user, ObjectOutputStream writer){
        if (clients.containsKey(user)){
            return false;
        }
        clients.put(user, writer);
        return true;
    }
}

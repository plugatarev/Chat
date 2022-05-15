import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Server implements Runnable{
    private ServerSocket socket;
    private final HashMap<User, ObjectOutputStream> clients = new HashMap<>();

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
            socket.bind(new InetSocketAddress("localhost", 8080));
            while (true) {
                Socket clientSocket = socket.accept();
                Thread t = new Thread(new Controller(clientSocket, this));
                t.start();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
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
                    sendToUser(message, writer);
                    sendToUser(new Message(Message.getMessageFromAddressedMessage(message.getMessage()), MessageType.SEND_USER),
                            clients.get(dst));
                }
                else{
                    message.setMessage("Such user doesn't exists");
                    sendToUser(message, writer);
                }
            }
            case EXIT -> {
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
        clients.forEach((key, value) -> sendToUser(message, value));
    }

    void sendToEverybodyExceptUser(Message message, ObjectOutputStream user){
        clients.entrySet().stream().filter(c -> !c.getValue().equals(user)).forEach(c -> sendToUser(message, c.getValue()));
    }

    public void addUser(User user, ObjectOutputStream writer) throws InvalidUserName {
        if (clients.containsKey(user)){
            throw new InvalidUserName("This name is busy: " + user.getName());
        }
        clients.put(user, writer);
    }

}

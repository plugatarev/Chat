import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Server implements Runnable{
    private ServerSocket socket;
    private final HashMap<User, Writer> clients = new HashMap<>();

    public HashMap<User, Writer> getUsers(){
        return clients;
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

    /**
     * type - optional parameter, if passed null it will be determined automatically
     **/
    public void sendMessage(String message, Writer writer, MessageType type){
        if (type == null) type = Message.getMessageType(message);
        switch (type){
            case REGISTRATION:
                sendToUser(message, writer);
            case SEND_EVERYBODY:
                sendToEverybody(message);
                break;
            case SEND_USER:
                sendToUser(message, writer);
                break;
        }
    }

    private void sendToUser(String message, Writer writer){
        try{
            writer.write(message + "\n");
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
    
    private void sendToEverybody(String message) {
        for (User user: clients.keySet()){
            sendToUser(message, clients.get(user));
        }
    }

    public void addUser(User user, Writer writer) throws InvalidUserName {
        if (clients.containsKey(user)){
            throw new InvalidUserName("This name is busy: " + user.getName());
        }
        clients.put(user, writer);
    }

    public void exitUserCommand(Message message) {

    }

    public void userListCommand(Message message) {
    }
}

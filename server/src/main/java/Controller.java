import java.io.*;
import java.net.Socket;

public class Controller implements Runnable {
    private final Socket socket;
    private final Server server;
    private ObjectOutputStream writer;

    Controller(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        User user = null;
        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
            Message loginMessage = (Message) reader.readObject();
            user = new User(loginMessage.getMessage());
            server.addUser(user, writer);
            server.sendMessage(new Message(user.getName() + " registration", MessageType.REGISTRATION), writer);
            while (true){
                Message receiveMessage = (Message) reader.readObject();
                if (receiveMessage == null) break;
                Message sendMessage = new Message(receiveMessage.getMessage(), receiveMessage.getType(), user.getName());
                if (receiveMessage.getType().equals(MessageType.EXIT)) server.removeOnlineUser(user);
                server.sendMessage(sendMessage, writer);
            }
        }
        catch (InvalidUserName ex){
            server.sendMessage(new Message("This name is busy", MessageType.NOT_REGISTRATION), writer);
        }
        catch (Exception e) {
//            if (user != null) {
//                server.removeOnlineUser(user);
//            }
        }
        finally {
            close();
        }
    }

    private void close() {
        if (socket != null) {
            try {
                socket.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

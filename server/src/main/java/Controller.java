import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class Controller implements Runnable {
    private final Socket socket;
    private final Server server;
    private ObjectOutputStream writer;
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    Controller(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        User user;
        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
            Message loginMessage = (Message) reader.readObject();
            user = new User(loginMessage.getMessage());
            while (true){
                boolean isAdd = server.addUser(user, writer);
                if (isAdd) break;
                server.sendMessage(new Message("This name is busy, try again: ", MessageType.NOT_REGISTRATION), writer);
                Message newLogin = (Message) reader.readObject();
                user = new User(newLogin.getMessage());
            }
            LOG.debug("User=" + user.getName() + " registration");
            server.sendMessage(new Message(user.getName() + " registration", MessageType.REGISTRATION), writer);
            while (true){
                Message receiveMessage = (Message) reader.readObject();
                if (receiveMessage == null) break;
                LOG.debug("Get message \""+ receiveMessage.getMessage()+"\"" + " Type=" + receiveMessage.getType()
                        + " From=" + user.getName());
                Message sendMessage = new Message(receiveMessage.getMessage(), receiveMessage.getType(), user.getName());
                if (receiveMessage.getType().equals(MessageType.EXIT)) server.removeOnlineUser(user);
                LOG.debug("Send message \""+ sendMessage.getMessage()+"\"" + " Type=" + sendMessage.getType()
                        + " TO=" + user.getName());
                server.sendMessage(sendMessage, writer);
            }
        }
        catch (Exception e) {
            //TODO
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

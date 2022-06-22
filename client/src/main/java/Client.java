import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client implements Sender{
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private boolean isActive = true;
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    public void start() {
        try (Socket socket = new Socket()) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            socket.connect(new InetSocketAddress("localhost", 8080));
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("""
                    Available commands:
                    @user - send message to user
                    /list - show online users
                    /exit - left chat
                    """);
            String login = register(consoleReader);
            Thread controller = new Thread(new Controller(login, this));
            controller.start();
            while (!socket.isClosed()) {
                ServerMessage lastMessage = (ServerMessage) objectInputStream.readObject();
                ConsoleUtils.showMessage(lastMessage, login);
                if (lastMessage.type() == ServerMessage.ServerMessageType.EXIT) break;
            }
            isActive = false;
            controller.join();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            LOG.error("Server is not available because of exception: " + e);
        }
    }

    @Override
    public void sendMessage(ClientMessage message){
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        }
        catch (IOException e) {
            LOG.error("Failed to send a message: ", e);
            throw new UncheckedIOException(e);
        }
    }

    public boolean isActive(){
        return isActive;
    }

    private String register(BufferedReader consoleReader) throws IOException, ClassNotFoundException {
        String login;
        System.out.print("Enter the desired login: ");
        while (true) {
            login = consoleReader.readLine();
            String reasonIncorrectName = getReasonIncorrectName(login);
            if (reasonIncorrectName != null) {
                System.err.println(reasonIncorrectName);
                continue;
            }
            sendMessage(new ClientMessage(login, ClientMessage.ClientMessageType.REGISTRATION, null, null));
            ServerMessage lastMessage = (ServerMessage) objectInputStream.readObject();
            ConsoleUtils.showMessage(lastMessage, null);
            if (lastMessage.type() == ServerMessage.ServerMessageType.NEW_CLIENT){
                break;
            }
        }
        return login;
    }

    private String getReasonIncorrectName(String name) {
        if (name.charAt(0) == '@') return "login should not start with '@', try again: ";
        if (name.contains(" ")) return "Login must not contain white spaces, try again: ";
        if (name.equals("/exit") || name.equals("/list")) return "Login must not be a command, try again: ";
        return null;
    }
}

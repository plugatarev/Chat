import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client{
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String login;
    private boolean isActive = true;
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    public void start() {
        try (Socket socket = new Socket();
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            socket.connect(new InetSocketAddress("localhost", 8080));
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Available commands:\n@user - send message to user\n/list - show online users\n/exit - left chat\n");
            login = validateLogin(consoleReader);
            Thread controller = new Thread(new Controller(this));
            controller.start();
            while (!socket.isClosed()) {
                Message lastMessage = (Message) objectInputStream.readObject();
                MessageType type = lastMessage.type();
                View.update(lastMessage, login);
                if (type == MessageType.EXIT) break;
            }
            isActive = false;
            controller.join();
        } catch (Exception e) {
            LOG.error("Server is not available because of exception: " + e);
        }
    }

    public void sendMessage(Message message){
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        }
        catch (IOException e) {
            LOG.error("Socket closed");
        }
    }
    public boolean isActive(){
        return isActive;
    }

    public String login(){
        return login;
    }

    private String validateLogin(BufferedReader consoleReader) throws IOException, ClassNotFoundException {
        String line;
        System.out.print("Enter the desired login: ");
        while (true) {
            line = consoleReader.readLine();
            if (!isValidLogin(line)) {
                System.out.print(getReasonIncorrectName(line));
                continue;
            }
            sendMessage(new Message(line, MessageType.REGISTRATION));
            Message lastMessage = (Message) objectInputStream.readObject();
            View.update(lastMessage, login);
            if (lastMessage.type() == MessageType.REGISTRATION){
                break;
            }
        }
        return line;
    }

    private boolean isValidLogin(String login){
        return !(login == null || login.equals("/exit") || login.equals("/list") || containsWhitespace(login) || login.charAt(0) == '@');
    }

    private String getReasonIncorrectName(String name) {
        if (containsWhitespace(name)) return "Login must not contain white spaces, try again: ";
        if (name.equals("/exit") || name.equals("/list") || name.charAt(0) == '@') return "Login must not be a command, try again: ";
        return "Login not set ";
    }

    private boolean containsWhitespace(String string){
        for (int i = 0; i < string.length(); i++){
            if (string.charAt(i) == ' ') return true;
        }
        return false;
    }
}

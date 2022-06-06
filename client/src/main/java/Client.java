import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client{
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private BufferedReader consoleReader;
    private String login;
    private boolean isActive = true;

    public void start() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 8080));
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            consoleReader = new BufferedReader(new InputStreamReader(System.in));
            login = validateLogin();
            Thread controller = new Thread(new Controller(this));
            controller.start();
            while (!socket.isClosed()) {
                Message lastMessage = (Message) objectInputStream.readObject();
                MessageType type = lastMessage.type();
                View.update(lastMessage);
                if (type == MessageType.EXIT) break;
            }
            isActive = false;
            controller.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Server is not available because of exception: " + e);
        }
    }

    public void sendMessage(Message message){
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        }
        catch (IOException e){
            System.out.println("Socket closed");
        }
    }
    public boolean isActive(){
        return isActive;
    }

    public String login(){
        return login;
    }

    private String validateLogin() throws IOException, ClassNotFoundException {
        String line;
        System.out.print("Enter the desired login: ");
        while (true) {
            line = consoleReader.readLine();
            if (!isValidLogin(line)) {
                System.out.print("Incorrect login, try again: ");
                continue;
            }
            objectOutputStream.writeObject(new Message(line, MessageType.REGISTRATION));
            objectOutputStream.flush();
            Message lastMessage = (Message) objectInputStream.readObject();
            View.update(lastMessage);
            if (lastMessage.type() == MessageType.REGISTRATION){
                break;
            }
        }
        return line;
    }

    private boolean isValidLogin(String login){
        return !(login == null ||login.equals("/exit") || login.equals("/list") || containsWhitespace(login) || login.charAt(0) == '@');
    }

    private boolean containsWhitespace(String string){
        for (int i = 0; i < string.length(); i++){
            if (string.charAt(i) == ' ') return true;
        }
        return false;
    }
}

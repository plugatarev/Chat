import java.io.*;
import java.net.Socket;

public class Controller implements Runnable {
    private ObjectOutputStream writer;
    private final Socket socket;
    private final Client client;
    
    public Controller(Client client, Socket socket){
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void run() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))){
            writer = new ObjectOutputStream(socket.getOutputStream());
            String login = validateLogin(consoleReader);
            String line;
            while (!socket.isClosed()) {
                // CR: use non-blocking channel (nio)
                line = consoleReader.readLine();
                Message message = createMessage(line, login);
                if (message == null) continue;
                sendMessage(message);
            }
        } catch (IOException e) {
            System.out.println("Socket closed");
        }
    }

    private String validateLogin(BufferedReader reader) throws IOException {
        String line;
        System.out.print("Enter the desired login: ");
        while(true){
            line = reader.readLine();
            if (!isValidLogin(line)){
                System.out.print("Incorrect login, try again: ");
                continue;
            }
            sendMessage(new Message(line, MessageType.REGISTRATION));
            if (client.isRegistration()) break;
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

    private void sendMessage(Message message){
        try {
            writer.writeObject(message);
            writer.flush();
        }
        catch (IOException e){
            System.out.println("Socket closed");
        }
    }

    private Message createMessage(String message, String sender) {
        if (message.length() == 0) return null;
        if (!client.isRegistration()){
            if (!isValidLogin(message)) return null;
            return new Message(message, MessageType.REGISTRATION);
        }
        if (message.equals("/list")) return new Message(message, MessageType.SHOW_USERS, sender);
        if (message.equals("/exit")) return new Message(message, MessageType.EXIT, sender);
        if (message.charAt(0) != '@') {
            return new Message(message, MessageType.SEND_EVERYBODY, sender);
        }
        int endIndex = message.indexOf(' ');
        if (endIndex == -1) {
            endIndex = message.length();
        }
        String receiver = message.substring(1, endIndex);
        message =  endIndex == message.length() ? "" : message.substring(++endIndex);
        return new Message(message, MessageType.SEND_USER, sender, receiver);
    }
}
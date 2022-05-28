import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Controller(Socket socket) implements Runnable {

    ObjectOutputStream writer;

    @Override
    public void run() {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        writer = new ObjectOutputStream(socket.getOutputStream());
        // CR: validate login
        System.out.print("Enter the desired login: ");
        try {
            String line;
            while (!socket.isClosed()) {
                // CR: use non-blocking channel (nio)
                line = consoleReader.readLine();
                Message message = createMessage(line);
                if (message == null) continue;
                sendMessage(message);
            }
        } catch (IOException e) {
            System.out.println("Socket closed");
        }
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

    static Message createMessage(String message) {
        if (message.length() == 0) return null;
        if (message.equals("/list")) return new Message(message, MessageType.SHOW_USERS);
        if (message.equals("/exit")) return new Message(message, MessageType.EXIT);
        if (message.charAt(0) != '@') {
            return new Message(message, MessageType.SEND_EVERYBODY);
        }
        int start = 1;
        int endIndex = message.indexOf(' ');
        if (endIndex == -1) {
            endIndex = message.length();
        }
        String receiver = message.substring(1, endIndex);
        message =  endIndex == message.length() ? "" : message.substring(endIndex);
        return new Message(message, MessageType.SEND_USER, null, receiver);
    }
}
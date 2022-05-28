import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public record Controller(Client client) implements Runnable {

    @Override
    public void run() {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter the desired login: ");
        try {
            String line;
            while (client.isConnected()) {
                line = consoleReader.readLine();
                if (line.equals("")) continue;
                client.sendMessage(createMessage(line));
            }
        } catch (IOException e) {
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
        while (start < message.length() && message.charAt(start) != ' ') {
            start++;
        }
        String receiver = message.substring(1, start);
        String msg = " ";
        if (start < message.length()) msg = message.substring(++start);
        return new Message(msg, MessageType.SEND_USER, null, receiver);
    }
}
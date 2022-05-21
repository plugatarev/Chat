import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller implements Runnable {
    private final Client client;

    public Controller(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter the desired login: ");
        try {
            while (client.isConnected()) {
                String line = consoleReader.readLine();
                if (line == null) break;

                client.sendMessage(new Message(line, Message.getMessageType(line)));
            }
        } catch (IOException e) {
            System.out.println("Socket closed");
        }
    }

    static Message createMessage(String message) {
        if (message.length() == 0) return null;
        if (message.charAt(0) != '@') return new Message(....);
        String receiver = message.indexOf(' ');
    }
}
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
                client.sendMessage(line);
            }
            System.out.println("Client not connect");
        } catch (IOException e) {
            System.out.println("Socket closed");
        }
    }
}
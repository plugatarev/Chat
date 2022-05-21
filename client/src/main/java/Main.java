import java.net.Inet4Address;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Main {

    BlockingQueue queue;

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        // CR: remove thread
        Thread clientThread = new Thread(client);
        clientThread.start();
        clientThread.join();

        try (Socket socket = new Socket()) {
            // init socket
            new Client()

        }
    }
}

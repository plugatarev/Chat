import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);
    private static final int PORT = 8080;

    public static void main(String[] args){
        ClientService service = new ClientService();
        try (ServerSocket socket = new ServerSocket()){
            socket.bind(new InetSocketAddress("localhost", PORT));
            LOG.debug("Server starting on port " + PORT);
            while (true) {
                Socket clientSocket = socket.accept();
                LOG.debug("ClientService.Client started...");
                Thread t = new Thread(new ClientController(service, clientSocket));
                t.start();
            }
        }
        catch (IOException exception){
            LOG.error("The server stopped working because of exception: ", exception);
        }
    }
}

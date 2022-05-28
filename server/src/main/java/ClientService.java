import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ClientService implements Runnable{

    private static final int PORT = 8080;
    private ServerSocket socket;
    private final ClientList clients = new ClientList();
    private static final Logger LOG = LoggerFactory.getLogger(ClientService.class);

    @Override
    public void run() {
        try {
            socket = new ServerSocket();
            socket.bind(new InetSocketAddress("localhost", PORT));
            LOG.debug("Server starting on port " + PORT);
            while (true) {
                Socket clientSocket = socket.accept();
                LOG.debug("Client started...");
                Thread t = new Thread(new ClientController( this, clientSocket));
                t.start();
            }
        }
        catch (IOException ex){
            LOG.debug("The server has finished working");
        }
        finally {
            close();
        }
    }

    private synchronized void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void send(Message message) {
        if (message.getType() == MessageType.SEND_EVERYBODY){
            for (Client c : clients.getClients()){
                if (!message.senderName.equals(c.name())) send(new Message(message.getMessage(), MessageType.SEND_USER,
                        message.senderName, c.name()));
            }
            return;
        }
        if (message.getReceiverName() == null) throw new IllegalStateException("Receiver name not set");
        ObjectOutputStream writer = clients.getWriter(message.receiverName);
        if (writer == null) {
            send(new Message("Such user doesn't exists", MessageType.SEND_USER,null, message.senderName));
            return;
        }
        write(message, writer);
    }

    public synchronized void write(Message message, ObjectOutputStream writer){
        try{
            writer.writeObject(message);
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean containsSpace(String string){
        for (int i = 0; i < string.length(); i++){
            if (string.charAt(i) == ' ') return true;
        }
        return false;
    }


    public synchronized boolean register(Client client) {
        if (clients.getWriter(client.name()) != null) return false;
        String name = client.name();
        if (name.equals("/exit") || name.equals("/list") || containsSpace(name) || name.charAt(0) == '@') return false;
        clients.add(client);
        return true;
    }

    public synchronized List<String> getOnlineUsers(){
        return clients.getClients().stream().map(Client::name).toList();
    }

    public synchronized void delete(Client client) {
        clients.removed(client);
    }
}

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientService{

    private final List<Client> clients = new ArrayList<>();

    public synchronized void sendAll(String sender, Message message) {
        if (message.type() != MessageType.SEND_EVERYBODY && message.type() != MessageType.REGISTRATION){
            throw new IllegalStateException("Invalid message type to send to all clients");
        }
        for (Client c : clients){
            if (message.senderName() != null && sender.equals(c.name())) continue;
            message.setReceiverName(c.name());
            if (message.type() == MessageType.SEND_EVERYBODY) message.setMessageType(MessageType.SEND_USER);
            ObjectOutputStream writer = c.writer();
            try{
                writer.writeObject(new Message(message.message(), message.type(), sender, c.name()));
                writer.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void sendTo(String receiver, Message message) {
        if (message.type() == MessageType.SEND_EVERYBODY || message.type() == MessageType.REGISTRATION){
            throw new IllegalStateException("Invalid message type to send to client");
        }
        ObjectOutputStream writer;
        if (!containsClient(receiver)){
            writer = getWriter(message.senderName());
            message = new Message("Such user doesn't exists", MessageType.SEND_USER,null, message.senderName());
        }
        else{
            writer = getWriter(receiver);
        }

        try{
            writer.writeObject(message);
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean register(String clientName, ObjectOutputStream outputStream) {
        if (containsClient(clientName)) return false;
        clients.add(new Client(clientName, outputStream));
        return true;
    }

    public synchronized List<String> getClientNames(){
        return clients.stream().map(Client::name).toList();
    }

    public synchronized void delete(String clientName) {
        Client client = null;
        for (Client c : clients){
            if (c.name().equals(clientName)){
                client = c;
                break;
            }
        }
        if (client != null) clients.remove(client);
    }

    private boolean containsClient(String name){
        return clients.stream().map(Client::name).anyMatch(c -> c.equals(name));
    }


    private ObjectOutputStream getWriter(String client){
        for (Client c : clients){
            if (c.name().equals(client)) return c.writer();
        }
        return null;
    }

    // CR: override equals and hashcode
    private record Client(String name, ObjectOutputStream writer) {}
}

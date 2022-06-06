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

    public synchronized void send(String receiver, Message message) {
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

//    public synchronized void send(Message message) {
//        if (message.getType() == MessageType.SEND_EVERYBODY || message.getType() == MessageType.REGISTRATION){
//            String sender = message.senderName;
//            for (Client c : clients){
//                if (sender != null && sender.equals(c.name())) continue;
//                message.setReceiverName(c.name());
//                ObjectOutputStream writer = c.writer();
//                try{
//                    writer.writeObject(new Message(message.getMessage(), message.type, sender, c.name()));
//                    writer.flush();
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return;
//        }
//        if (message.getReceiverName() == null) throw new IllegalStateException("Receiver name not set");
//        if (!containsClient(message.receiverName)){
//            ObjectOutputStream writer = getWriter(message.senderName);
//            try{
//                writer.writeObject(new Message("Such user doesn't exists", MessageType.SEND_USER,null, message.senderName));
//                writer.flush();
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        else{
//            ObjectOutputStream writer = getWriter(message.receiverName);
//            try{
//                writer.writeObject(message);
//                writer.flush();
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private boolean containsClient(String name){
        return clients.stream().map(Client::name).anyMatch(c -> c.equals(name));
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

    private ObjectOutputStream getWriter(String client){
        for (Client c : clients){
            if (c.name().equals(client)) return c.writer();
        }
        return null;
    }
}

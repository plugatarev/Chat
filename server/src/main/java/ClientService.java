import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientService{

    private final List<Client> clients = new ArrayList<>();

    public synchronized void sendAll(String sender, Message message) {
        if (message.getType() != MessageType.SEND_EVERYBODY || message.getType() != MessageType.REGISTRATION){
            // TODO
            throw new IllegalStateException("");
        }
        for (Client c : clients){
            if (sender != null && sender.equals(c.name())) continue;
            message.setReceiverName(c.name());
            ObjectOutputStream writer = c.writer();
            try{
                writer.writeObject(new Message(message.getMessage(), message.type, sender, c.name()));
                writer.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void send(String receiver, Message message) {
        if (message.getType() == MessageType.SEND_EVERYBODY || message.getType() == MessageType.REGISTRATION){
            throw new IllegalStateException("");
        }
        if (!containsClient(message.receiverName)){
            ObjectOutputStream writer = getWriter(message.senderName);
            try{
                writer.writeObject(new Message("Such user doesn't exists", MessageType.SEND_USER,null, message.senderName));
                writer.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

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

    private boolean containsWhitespace(String string){
        for (int i = 0; i < string.length(); i++){
            if (string.charAt(i) == ' ') return true;
        }
        return false;
    }


    public synchronized boolean register(String clientName, ObjectOutputStream oos) {
        if (containsClient(client.name())) return false;
        String name = client.name();
        if (name.equals("/exit") || name.equals("/list") || containsWhitespace(name) || name.charAt(0) == '@') return false;
        clients.add(client);
        return true;
    }

    public synchronized List<String> getClientNames(){
        return clients.stream().map(Client::name).toList();
    }

    public synchronized void delete(String clientName) {
        clients.remove(client);
    }

    private ObjectOutputStream getWriter(String client){
        for (Client c : clients){
            if (c.name().equals(client)) return c.writer();
        }
        return null;
    }
}

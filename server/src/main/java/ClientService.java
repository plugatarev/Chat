import java.util.*;

public class ClientService{

    private final HashMap<String, Writer> clients = new HashMap<>();

    public synchronized void sendAll(BroadMessage message) {
        for (Map.Entry<String, Writer> c : clients.entrySet()){
            message.setReceiverName(c.getKey());
            if (message.type() == MessageType.SEND_EVERYBODY) message.setMessageType(MessageType.SEND_USER);
            Writer writer = c.getValue();
            writer.write(new Message(message.message(), message.type(), message.senderName(), c.getKey()));
        }
    }

    public synchronized void sendTo(ClientMessage message) {
        String receiver = message.receiverName();
        Writer writer;
        if (!clients.containsKey(receiver)){
            writer = clients.get(message.senderName());
            message = new ClientMessage("Such client doesn't exists", MessageType.SEND_USER, null, message.senderName());
        }
        else{
            writer = clients.get(receiver);
        }
        writer.write(message);
    }

    public synchronized boolean register(String clientName, Writer outputStream) {
        if (clients.containsKey(clientName)) return false;
        clients.put(clientName, outputStream);
        return true;
    }

    public synchronized List<String> getClientNames(){
        return clients.keySet().stream().toList();
    }

    public synchronized void delete(String clientName) {
        if (!clients.containsKey(clientName)) throw new IllegalStateException(clientName + " doesn't exists in clients list");
        clients.remove(clientName);
    }
}

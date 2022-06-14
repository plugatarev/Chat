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

    public synchronized void sendTo(UserMessage message) {
        String receiver = message.receiverName();
        Writer writer = clients.get(receiver);
        if (writer == null) {
            writer = clients.get(message.senderName());
            // CR: illegal state if writer == null
            message = new UserMessage("Such user doesn't exists", MessageType.SEND_USER, null, message.senderName());
        }
        else{
            // CR: merge with containsKey
            writer = clients.get(receiver);
        }
        writer.write(message);
    }

    public synchronized boolean register(String clientName, Writer outputStream) {
        if (clients.containsKey(clientName)) return false;
        // CR: putIfAbsent
        clients.put(clientName, outputStream);
        return true;
    }

    public synchronized Collection<String> getClientNames(){
        return clients.keySet();
    }

    public synchronized void delete(String clientName) {
        Writer removed = clients.remove(clientName);
        if (removed == null) throw new IllegalStateException(clientName + " doesn't exists in clients list");
    }
}

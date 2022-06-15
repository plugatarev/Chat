import java.util.*;

public class ClientService{

    private final HashMap<String, Writer> clients = new HashMap<>();

    public synchronized void sendAll(BroadMessage message) {
        for (Map.Entry<String, Writer> c : clients.entrySet()){
            Writer writer = c.getValue();
            if (message.type() == BroadMessage.BroadMessageType.SEND_EVERYBODY) {
                ClientMessage.ClientMessageType type = ClientMessage.ClientMessageType.SEND_USER;
                writer.write(new ClientMessage(message.message(), type, message.senderName(), c.getKey()));
            }
            else{
                ClientMessage.ClientMessageType type = ClientMessage.ClientMessageType.REGISTRATION;
                writer.write(new ClientMessage(message.message(), type, message.senderName(), c.getKey()));
            }
        }
    }

    public synchronized void sendTo(ClientMessage message) {
        String receiver = message.receiverName();
        Writer writer = clients.get(receiver);
        if (writer == null) {
            writer = clients.get(message.senderName());
            if (writer == null) throw new IllegalStateException("Client " + message.senderName() + " not available to send a message");
            message = new ClientMessage("Such client doesn't exists", ClientMessage.ClientMessageType.SEND_USER, null, message.senderName());
        }
        writer.write(message);
    }

    public synchronized boolean register(String clientName, Writer outputStream) {
        return clients.putIfAbsent(clientName, outputStream) == null;
    }

    public synchronized Collection<String> getClientNames(){
        return clients.keySet();
    }

    public synchronized void delete(String clientName) {
        Writer removed = clients.remove(clientName);
        if (removed == null) throw new IllegalStateException(clientName + " doesn't exists in clients list");
    }
}

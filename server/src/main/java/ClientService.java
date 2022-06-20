import java.util.*;

public class ClientService{

    private final Map<String, Writer> clients = new HashMap<>();

    public synchronized void sendAll(ServerBroadMessage message) {
        for (Map.Entry<String, Writer> c : clients.entrySet()){
            Writer writer = c.getValue();
            writer.write(message.serverMessage());
        }
    }

    public synchronized void sendTo(ServerClientMessage message) {
        ServerMessage clientMessage = message.serverMessage();
        String receiver = message.receiver();
        Writer writer = clients.get(receiver);
        if (writer == null) {
            writer = clients.get(message.sender());
            if (writer == null) throw new IllegalStateException("Client " + message.sender() + " not available to send a message");
            ServerMessage.ServerMessageType sendClient = ServerMessage.ServerMessageType.MESSAGE;
            clientMessage = new ServerMessage("Such client doesn't exists", sendClient, null, message.sender());
        }
        writer.write(clientMessage);
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

import java.util.*;

public class ClientService{

    private final HashSet<Client> clients = new HashSet<>();

    public synchronized void sendAll(BroadMessage message) {
        for (Client c : clients){
            message.setReceiverName(c.name());
            if (message.type() == MessageType.SEND_EVERYBODY) message.setMessageType(MessageType.SEND_USER);
            Writer writer = c.writer();
            writer.write(new Message(message.message(), message.type(), message.senderName(), c.name()));
        }
    }

    public synchronized void sendTo(UserMessage message) {
        String receiver = message.receiverName();
        Writer writer;
        if (!clients.contains(new Client(receiver, null))){
            writer = getWriter(message.senderName());
            message = new UserMessage("Such user doesn't exists", MessageType.SEND_USER, null, message.senderName());
        }
        else{
            writer = getWriter(receiver);
        }
        writer.write(message);
    }

    public synchronized boolean register(String clientName, Writer outputStream) {
        if (clients.contains(new Client(clientName, null))) return false;
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

    private Writer getWriter(String client){
        for (Client c : clients){
            if (c.name().equals(client)) return c.writer();
        }
        return null;
    }

    // CR: override equals and hashcode
    private record Client(String name, Writer writer) {
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Client) obj;
            return this.name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}

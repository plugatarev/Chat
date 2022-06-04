import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientService{

    private final List<Client> clients = new ArrayList<>();

    public synchronized void send(Message message) {
        if (message.getType() == MessageType.SEND_EVERYBODY || message.getType() == MessageType.REGISTRATION){
            String sender = message.senderName;
            for (Client c : clients){
                if (sender != null && sender.equals(c.name())) continue;
                message.setReceiverName(c.name());
                write(new Message(message.getMessage(), message.type, sender, c.name()), c.writer());
            }
            return;
        }
        if (message.getReceiverName() == null) throw new IllegalStateException("Receiver name not set");
        if (!containsClient(message.receiverName)){
            write(new Message("Such user doesn't exists", MessageType.SEND_USER,null, message.senderName),
                    getWriter(message.senderName));

        }
        else{
            write(message,getWriter(message.receiverName));
        }
    }

    private boolean containsClient(String name){
        return clients.stream().map(Client::name).anyMatch(c -> c.equals(name));
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

    private boolean containsWhitespace(String string){
        for (int i = 0; i < string.length(); i++){
            if (string.charAt(i) == ' ') return true;
        }
        return false;
    }


    public synchronized boolean register(Client client) {
        if (containsClient(client.name())) return false;
        String name = client.name();
        if (name.equals("/exit") || name.equals("/list") || containsWhitespace(name) || name.charAt(0) == '@') return false;
        add(client);
        return true;
    }

    public synchronized List<String> getOnlineUsers(){
        return clients.stream().map(Client::name).toList();
    }

    public synchronized void delete(Client client) {
        removed(client);
    }

    private synchronized void add(Client client){
        clients.add(client);
    }

    private synchronized void removed(Client client){
        clients.remove(client);
    }

    private synchronized ObjectOutputStream getWriter(String client){
        for (Client c : clients){
            if (c.name().equals(client)) return c.writer();
        }
        return null;
    }
}

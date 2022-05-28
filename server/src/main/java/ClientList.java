import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientList {
    private final List<Client> clients;

    public ClientList(){
        clients = new ArrayList<>();
    }

    public synchronized void add(Client client){
        clients.add(client);
    }

    public synchronized void removed(Client client){
        clients.remove(client);
    }

    public synchronized List<Client> getClients(){
        return clients;
    }

    public synchronized ObjectOutputStream getWriter(String client){
        for (Client c : clients){
            if (c.name().equals(client)) return c.writer();
        }
        return null;
    }
}

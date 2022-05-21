import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientService {

    private final List<Client> clients = new ArrayList<>();

    boolean send(String receiver, String message) {
        if (reciever !in clients) return false;
        client.send(message);
    }

    public boolean register(Client client) {

    }

    public void delete(String client) {

    }

    public List<Client> getClients() {
        return clients;
    }
}

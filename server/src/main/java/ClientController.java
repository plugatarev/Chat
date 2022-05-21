import java.util.List;

public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    public void receive(Message message) {
        switch (message.type) {
            case SHOW_USERS -> {
                List<Client> clients = clientService.getClients();
                clientService.send(message.getSenderName(), clients.toString());
            }
        }
        clientService.send(message.getReceiverName(), message.getMessage());
    }

    public void add(Client client) {
        clientService.register(client);
    }

}

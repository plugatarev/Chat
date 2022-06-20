import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class ServiceTest {
    private static final ServerBroadMessage.ServerBroadMessageType SEND_EVERYBODY = ServerBroadMessage.ServerBroadMessageType.MESSAGE;
    private static final ServerClientMessage.ServerClientMessageType SEND_USER = ServerClientMessage.ServerClientMessageType.MESSAGE;

    static class TestWriter implements Writer{
        final List<ServerMessage> messages = new ArrayList<>();

        @Override
        public void write(ServerMessage message) {
            messages.add(message);
        }

        public List<String> getListStringMessages(){
            return messages.stream().map(ServerMessage::message).toList();
        }
    }

    @Test
    public void registration(){
        ClientService service = new ClientService();
        service.register("abracadabra", new TestWriter());
        Assert.assertEquals(service.getClientNames().toString(), "[abracadabra]");
    }

    @Test
    public void severalRegistrations(){
        ClientService service = new ClientService();
        service.register("abracadabra", new TestWriter());
        service.register("dgre", new TestWriter());
        service.register("lire", new TestWriter());
        Assert.assertEquals(service.getClientNames().stream().sorted().toList().toString(), "[abracadabra, dgre, lire]");
    }

    @Test
    public void registrationAlreadyExistsClient(){
        ClientService service = new ClientService();
        service.register("abracadabra", new TestWriter());
        Assert.assertFalse(service.register("abracadabra", new TestWriter()));
        Assert.assertEquals(service.getClientNames().toString(), "[abracadabra]");
    }

    @Test
    public void sendMessageToAllClients(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        TestWriter hello = new TestWriter();
        TestWriter world = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.register("hello", hello);
        service.register("world", world);
        ServerBroadMessage message = new ServerBroadMessage("hellll@lll#o", SEND_EVERYBODY, "abracadabra");
        service.sendAll(message);
        Assert.assertEquals(abracadabra.messages.get(0).message(), message.message());
        Assert.assertEquals(abracadabra.messages.get(0).sender(), message.sender());
        Assert.assertEquals(hello.messages.get(0).message(), message.message());
        Assert.assertEquals(hello.messages.get(0).sender(), message.sender());
        Assert.assertEquals(world.messages.get(0).message(), message.message());
        Assert.assertEquals(world.messages.get(0).sender(), message.sender());
    }

    @Test
    public void severalMessageSendsToAllClients(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        TestWriter hello = new TestWriter();
        TestWriter world = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.register("hello", hello);
        service.register("world", world);
        ServerBroadMessage message1 = new ServerBroadMessage("hello", SEND_EVERYBODY, "abracadabra");
        ServerBroadMessage message2 = new ServerBroadMessage("world", SEND_EVERYBODY, "hello");
        ServerBroadMessage message3 = new ServerBroadMessage("!!!", SEND_EVERYBODY, "!!!");
        service.sendAll(message1);
        service.sendAll(message2);
        service.sendAll(message3);
        Assert.assertEquals(abracadabra.getListStringMessages(), List.of("hello", "world", "!!!"));
        Assert.assertEquals(hello.getListStringMessages(), List.of("hello", "world", "!!!"));
        Assert.assertEquals(world.getListStringMessages(), List.of("hello", "world", "!!!"));
    }

    @Test
    public void sendMessageToClient(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        TestWriter hello = new TestWriter();
        TestWriter world = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.register("hello", hello);
        service.register("world", world);
        ServerClientMessage message = new ServerClientMessage("hello", SEND_USER, "abracadabra", "hello");
        service.sendTo(message);
        Assert.assertEquals(hello.messages.get(0).message(), message.message());
        Assert.assertTrue(world.messages.isEmpty());
    }

    @Test
    public void sendToDoesntExistsClient(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        TestWriter hello = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.register("hello", hello);
        ServerClientMessage message = new ServerClientMessage("hello", SEND_USER, "abracadabra", "hello1");
        service.sendTo(message);
        Assert.assertEquals(abracadabra.messages.get(0).message(), "Such client doesn't exists");
        Assert.assertTrue(hello.messages.isEmpty());
    }

    @Test
    public void deleteOnlineClient(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.delete("abracadabra");
        Assert.assertTrue(service.getClientNames().isEmpty());
    }

    @Test
    public void deleteDoesntOnlineClient(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        service.register("abracadabra", abracadabra);
        boolean isException = false;
        try{
            service.delete("abracadabra1");
        }catch (Exception e){
            isException = true;
        }
        Assert.assertTrue(isException);
    }
}

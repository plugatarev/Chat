import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class ServiceTest {
    static class TestWriter implements Writer{
        final List<Message> messages = new ArrayList<>();

        @Override
        public void write(Message message) {
            messages.add(message);
        }

        public List<String> getListStringMessages(){
            return messages.stream().map(Message::message).toList();
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
    public void registrationAlreadyExistsUser(){
        ClientService service = new ClientService();
        service.register("abracadabra", new TestWriter());
        Assert.assertFalse(service.register("abracadabra", new TestWriter()));
        Assert.assertEquals(service.getClientNames().toString(), "[abracadabra]");
    }

    @Test
    public void sendMessageToAllUsers(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        TestWriter hello = new TestWriter();
        TestWriter world = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.register("hello", hello);
        service.register("world", world);
        BroadMessage message = new BroadMessage("hellll@lll#o", MessageType.SEND_EVERYBODY, "abracadabra");
        service.sendAll(message);
        Assert.assertEquals(abracadabra.messages.get(0).message(), message.message());
        Assert.assertEquals(abracadabra.messages.get(0).senderName(), message.senderName());
        Assert.assertEquals(hello.messages.get(0).message(), message.message());
        Assert.assertEquals(hello.messages.get(0).senderName(), message.senderName());
        Assert.assertEquals(world.messages.get(0).message(), message.message());
        Assert.assertEquals(world.messages.get(0).senderName(), message.senderName());
    }

    @Test
    public void severalMessageSendsToAllUsers(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        TestWriter hello = new TestWriter();
        TestWriter world = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.register("hello", hello);
        service.register("world", world);
        BroadMessage message1 = new BroadMessage("hello", MessageType.SEND_EVERYBODY, "abracadabra");
        BroadMessage message2 = new BroadMessage("world", MessageType.SEND_EVERYBODY, "hello");
        BroadMessage message3 = new BroadMessage("!!!", MessageType.SEND_EVERYBODY, "!!!");
        service.sendAll(message1);
        service.sendAll(message2);
        service.sendAll(message3);
        Assert.assertEquals(abracadabra.getListStringMessages(), List.of("hello", "world", "!!!"));
        Assert.assertEquals(hello.getListStringMessages(), List.of("hello", "world", "!!!"));
        Assert.assertEquals(world.getListStringMessages(), List.of("hello", "world", "!!!"));
    }

    @Test
    public void sendMessageToUser(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        TestWriter hello = new TestWriter();
        TestWriter world = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.register("hello", hello);
        service.register("world", world);
        UserMessage message = new UserMessage("hello", MessageType.SEND_USER, "abracadabra", "hello");
        service.sendTo(message);
        Assert.assertEquals(hello.messages.get(0).message(), message.message());
        Assert.assertTrue(world.messages.isEmpty());
    }

    @Test
    public void sendToDoesntExistsUser(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        TestWriter hello = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.register("hello", hello);
        UserMessage message = new UserMessage("hello", MessageType.SEND_USER, "abracadabra", "hello1");
        service.sendTo(message);
        Assert.assertEquals(abracadabra.messages.get(0).message(), "Such user doesn't exists");
        Assert.assertTrue(hello.messages.isEmpty());
    }

    @Test
    public void deleteOnlineUser(){
        ClientService service = new ClientService();
        TestWriter abracadabra = new TestWriter();
        service.register("abracadabra", abracadabra);
        service.delete("abracadabra");
        Assert.assertTrue(service.getClientNames().isEmpty());
    }

    @Test
    public void deleteDoesntOnlineUser(){
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

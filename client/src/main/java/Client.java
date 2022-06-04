import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client{
    private final AtomicBoolean isRegistration = new AtomicBoolean(false);
    public void start() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 8080));
            Thread controller = new Thread(new Controller(this, socket));
            controller.start();
            ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
            Message lastMessage;
            MessageType type;
            while (!socket.isClosed()) {
                if (!isRegistration.get()) {
                    synchronized (isRegistration){
                        lastMessage = (Message) reader.readObject();
                        type = lastMessage.getType();
                        if (type == MessageType.REGISTRATION) isRegistration.set(true);
                    }
                }
                else{
                    lastMessage = (Message) reader.readObject();
                    type = lastMessage.getType();
                }
                View.update(lastMessage);
                if (type == MessageType.EXIT) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Server is not available because of exception: " + e);
        }
    }

    public boolean isRegistration(){
        synchronized (isRegistration) {
            return isRegistration.get();
        }
    }
}

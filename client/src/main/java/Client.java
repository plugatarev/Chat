import jdk.swing.interop.SwingInterOpUtils;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.GregorianCalendar;

public class Client implements Runnable{
    private Socket socket;
    private Message lastMessage;
    private ObjectOutputStream writer;
    private final View viewer = new View(this);

    @Override
    public void run() {
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 8080));
            writer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
            Thread controller = new Thread(new Controller(this));
            controller.start();
            while (isConnected()){
                lastMessage = (Message) reader.readObject();
                viewer.update();
                MessageType type = lastMessage.getType();
                if (type.equals(MessageType.EXIT) || type.equals(MessageType.NOT_REGISTRATION)) break;
            }
        } catch (Exception e) {
            System.out.println("Server is not available");
        }
        finally {
            close();
        }
    }

    public void sendMessage(Message message){
        try {
            writer.writeObject(message);
            writer.flush();
        }
        catch (IOException e){
            System.out.println("Socket closed");
        }
    }

    private void close() {
        try {
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message getLastMessage(){
        return lastMessage;
    }

    public boolean isConnected() {
        return !socket.isClosed();
    }
}

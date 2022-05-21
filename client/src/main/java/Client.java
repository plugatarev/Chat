import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client implements Runnable{
    private Socket socket;
    private ObjectOutputStream writer;

    @Override
    public void run() {
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 8080));
            writer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
            Thread controller = new Thread(new Controller(this));
            controller.start();
            Message lastMessage;
            while (isConnected()){
                lastMessage = (Message) reader.readObject();
                View.update(lastMessage);
                MessageType type = lastMessage.getType();
                if (type.equals(MessageType.EXIT)) break;
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

    public boolean isConnected() {
        return !socket.isClosed();
    }
}

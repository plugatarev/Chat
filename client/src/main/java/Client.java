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
    private Writer writer;
    private final View viewer = new View(this);

    @Override
    public void run() {
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 8080));
            writer = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread controller = new Thread(new Controller(this));
            controller.start();
            while (isConnected()){
                String line = reader.readLine();
                lastMessage = new Message(line, Message.getMessageType(line));
                viewer.update();
            }
            controller.join();
        } catch (Exception e) {
            System.out.println("Server is not available");
        }
        finally {
            close();
        }
    }

    public void sendMessage(String message){
        try {
            writer.write(message + "\n");
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

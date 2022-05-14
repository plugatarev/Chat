import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.GregorianCalendar;

public class Controller implements Runnable {
    private final Socket socket;
    private final Server server;
    private OutputStreamWriter writer;

    Controller(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            writer = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String loginMessage = reader.readLine();
            User newUser = new User(loginMessage);
            server.addUser(newUser, writer);
            server.sendMessage("User=" + newUser.getName() + " registration", writer, MessageType.REGISTRATION);
            while (true){
                String msg = reader.readLine();
                if (msg == null) break;
                server.sendMessage(msg, writer, null);
            }
        }
        catch (InvalidUserName ex){
            server.sendMessage("This name is busy", writer, MessageType.SEND_USER);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            close();
        }
    }

//    private void send(){
//        try{
//            writer.writeObject(message + "\n");
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void close() {
        if (socket != null) {
            try {
                socket.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

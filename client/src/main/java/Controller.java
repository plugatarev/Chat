import java.io.*;

public record Controller(Client client) implements Runnable {

    @Override
    public void run() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while (client.isActive()){
                if ((consoleReader.ready())) line = consoleReader.readLine();
                else continue;
                Message message = createMessage(line, client.login());
                if (message == null) continue;
                client.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String message, String sender) {
        if (message.length() == 0) return null;
        if (message.equals("/list")) return new Message(message, MessageType.SHOW_USERS, sender);
        if (message.equals("/exit")) return new Message(message, MessageType.EXIT, sender);
        if (message.charAt(0) != '@') {
            return new Message(message, MessageType.SEND_EVERYBODY, sender);
        }
        int endIndex = message.indexOf(' ');
        if (endIndex == -1) {
            endIndex = message.length();
        }
        String receiver = message.substring(1, endIndex);
        message = endIndex == message.length() ? "" : message.substring(++endIndex);
        return new Message(message, MessageType.SEND_USER, sender, receiver);
    }
}
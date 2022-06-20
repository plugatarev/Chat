import java.io.*;

public record Controller(String login, Client client) implements Runnable {

    @Override
    public void run() {
        try{
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (client.isActive()){
                if ((!consoleReader.ready())) {
                    continue;
                }
                line = consoleReader.readLine();
                ClientMessage message = createMessage(line, login);
                if (message == null) continue;
                client.sendMessage(message);
            }
        } catch (IOException e) {
            client.logError("Failed to read data from user because of exception: " + e.getMessage());
        }
    }

    private ClientMessage createMessage(String message, String sender) {
        if (message.isBlank()) return null;
        if (message.equals("/list")) return new ClientMessage(message, ClientMessage.ClientMessageType.SHOW_CLIENTS, sender, sender);
        if (message.equals("/exit")) return new ClientMessage(message, ClientMessage.ClientMessageType.EXIT, sender, sender);
        if (message.charAt(0) != '@') {
            return new ClientMessage(message, ClientMessage.ClientMessageType.SEND_EVERYBODY, sender, null);
        }
        int endIndex = message.indexOf(' ');
        if (endIndex == -1) {
            endIndex = message.length();
        }
        String receiver = message.substring(1, endIndex);
        message = endIndex == message.length() ? "" : message.substring(++endIndex);
        return new ClientMessage(message, ClientMessage.ClientMessageType.SEND_USER, sender, receiver);
    }
}
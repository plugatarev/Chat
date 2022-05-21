import java.net.Socket;

public record Client(String name, Socket socket) {}

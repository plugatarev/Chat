import java.io.Serializable;

public record ClientMessage(String message, ClientMessage.ClientMessageType type, String sender,
                            String receiver) implements Serializable {
    enum ClientMessageType {
        SEND_EVERYBODY,
        SEND_USER,
        REGISTRATION,
        SHOW_CLIENTS,
        EXIT
    }
}
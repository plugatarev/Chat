import java.io.Serializable;

public record ServerMessage(String message, ServerMessage.ServerMessageType type, String sender,
                            String receiver) implements Serializable {
    enum ServerMessageType {
        MESSAGE,
        NEW_CLIENT,
        CLIENTS_LIST,
        FAILED_REGISTRATION,
        EXIT
    }
}

public class ClientMessage extends Message{

    enum ClientMessageType {
        SHOW_USERS,
        SEND_USER,
        EXIT,
        REGISTRATION,
        NOT_REGISTRATION
    }

    private final ClientMessageType type;
    private final String receiverName;

    public ClientMessage(String message, ClientMessageType type, String senderName, String receiverName) {
        super(message, senderName);
        this.receiverName = receiverName;
        this.type = type;
    }

    public String receiverName(){
        return receiverName;
    }

    public String senderName() {
        return super.senderName();
    }

    public ClientMessageType type(){
        return type;
    }
}
public class BroadMessage extends Message {

    enum BroadMessageType {
        SEND_EVERYBODY,
        REGISTRATION
    }

    private final BroadMessageType messageType;

    public BroadMessage(String message, BroadMessageType messageType, String senderName) {
        super(message, senderName);
        this.messageType = messageType;
    }
}

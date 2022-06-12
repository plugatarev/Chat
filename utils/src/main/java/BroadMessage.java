public class BroadMessage extends Message{
    public BroadMessage(String message, MessageType type, String senderName) {
        super(message, type, senderName);
        if (type != MessageType.SEND_EVERYBODY && type != MessageType.REGISTRATION){
            throw new IllegalStateException("Invalid message type to send to all clients");
        }
    }
}

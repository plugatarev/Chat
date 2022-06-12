public class UserMessage extends Message{
    public UserMessage(String message, MessageType type, String senderName, String receiverName) {
        super(message, type, senderName, receiverName);
        if (type == MessageType.SEND_EVERYBODY || type == MessageType.REGISTRATION){
            throw new IllegalStateException("Invalid message type to send to client");
        }
    }
}
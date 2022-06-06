import java.io.Serializable;

public class Message implements Serializable {
    private final String message;
    private MessageType type;
    private String senderName;
    private String receiverName;

    public Message(String message, MessageType type){
        this.message = message;
        this.type = type;
    }

    public Message(String message, MessageType type, String senderName){
        this.message = message;
        this.type = type;
        this.senderName = senderName;
    }

    public Message(String message, MessageType type, String senderName, String receiverName){
        this.message = message;
        this.type = type;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

    public String message(){
        return message;
    }

    public MessageType type() {
        return type;
    }

    public String senderName(){
        return senderName;
    }

    public String receiverName() { return receiverName; }

    public void setReceiverName(String name) { this.receiverName = name; }

    public void setMessageType(MessageType type) { this.type = type; }

    public void setSenderName(String name) { this.senderName = name; }
}

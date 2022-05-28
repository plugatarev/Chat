import java.io.Serializable;

public class Message implements Serializable {
    String message;
    MessageType type;
    String senderName;
    String receiverName;

    public String getReceiverName() {
        return receiverName;
    }

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

    public String getMessage(){
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public String getSenderName(){
        return senderName;
    }

    public void setType(MessageType type){
        this.type = type;
    }
}

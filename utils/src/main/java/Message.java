import java.io.Serializable;

public abstract class Message implements Serializable {
    private final String message;
    private final String senderName;

    public Message(String message, String senderName){
        this.message = message;
        this.senderName = senderName;
    }

    public String message(){
        return message;
    }

    public String senderName(){
        return senderName;
    }
}

import java.io.Serializable;

public class Message implements Serializable {
    private final String message;
    private String senderName;

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

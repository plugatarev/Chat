import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Message implements Serializable {
    final String message;
    final MessageType type;
    String senderName;
    private final Calendar date;

    public Message(String message, MessageType type){
        this.message = message;
        this.type = type;
        this.date = new GregorianCalendar();
    }

    public Message(String message, MessageType type, String senderName){
        this.message = message;
        this.type = type;
        this.senderName = senderName;
        this.date = new GregorianCalendar();
    }

    public String getMessage(){
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public String getSenderName(){
        return  senderName;
    }

    public static MessageType getMessageType(String message){
        if (message.equals("/exit")) return MessageType.EXIT;
        if (message.charAt(0) == '@') {
            int i = 1;
            while (i < message.length() && message.charAt(i) == ' ') {
                i++;
            }
            if (message.substring(1, i).equals("all")) return MessageType.SEND_EVERYBODY;
            return MessageType.SEND_USER;
        }
        if (message.equals("/list")) return MessageType.SHOW_USERS;
        return MessageType.SEND_EVERYBODY;
    }
}

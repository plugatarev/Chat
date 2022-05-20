import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Message implements Serializable {
    String message;
    final MessageType type;
    String senderName;

    public Message(String message, MessageType type){
        this.message = message;
        this.type = type;
    }

    public Message(String message, MessageType type, String senderName){
        this.message = message;
        this.type = type;
        this.senderName = senderName;
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

    public static String getReceiverName(String message){
        if (message.charAt(0) != '@') return null;
        int i = 1;
        while (i < message.length() && message.charAt(i) != ' '){
            i++;
        }
        return message.substring(1, i);
    }

    public static String getMessageFromAddressedMessage(String message){
        if (message.charAt(0) != '@') return null;
        int start = 1;
        while (start < message.length() && message.charAt(start) != ' '){
            start++;
        }
        return message.substring(++start);
    }

    public static MessageType getMessageType(String message){
        if (message.equals("/exit")) return MessageType.EXIT;
        if (message.charAt(0) == '@') return MessageType.SEND_USER;
        if (message.equals("/list")) return MessageType.SHOW_USERS;
        return MessageType.SEND_EVERYBODY;
    }

    public void setMessage(String message){
        this.message = message;
    }
}

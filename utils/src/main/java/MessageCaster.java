public class MessageCaster {
    public static<T> T tryCast(Object obj, Class<T> tClass){
        // TODO: reflection
        if (tClass.isInstance(obj)) return (T) obj;
        return null;
    }

    public static MessageType getMessageType(Message message){
        BroadMessage broadMessage = tryCast(message, BroadMessage.class);
        if (broadMessage != null){
            return switch (broadMessage.type()){
                case REGISTRATION -> MessageType.REGISTRATION;
                case SEND_EVERYBODY -> MessageType.SEND_EVERYBODY;
            };
        }
        else{
            ClientMessage clientMessage = tryCast(message, ClientMessage.class);
            return switch (clientMessage.type()){
                case SHOW_USERS -> MessageType.SHOW_USERS;
                case SEND_USER -> MessageType.SEND_USER;
                case EXIT -> MessageType.EXIT;
                case REGISTRATION -> MessageType.REGISTRATION;
                case NOT_REGISTRATION -> MessageType.NOT_REGISTRATION;
            };
        }
    }
}

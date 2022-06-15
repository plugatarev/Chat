public class MessageCaster {

    public static<T> T tryCast(Object obj, Class<T> tClass){
        return tClass.isInstance(obj) ? (T) obj : null;
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
            if (clientMessage == null) throw new IllegalStateException("The message is not ClientMessage or BroadMessage");
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

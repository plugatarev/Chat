public record ServerClientMessage(String message, ServerClientMessage.ServerClientMessageType type,
                                  String sender, String receiver) {
    enum ServerClientMessageType {
        MESSAGE,
        FAILED_REGISTRATION,
        CLIENTS_LIST,
        EXIT
    }

    public ServerMessage serverMessage() {
        return new ServerMessage(message, serverMessageType(), sender, receiver);
    }

    private ServerMessage.ServerMessageType serverMessageType() {
        return switch (type) {
            case MESSAGE -> ServerMessage.ServerMessageType.MESSAGE;
            case FAILED_REGISTRATION -> ServerMessage.ServerMessageType.FAILED_REGISTRATION;
            case CLIENTS_LIST -> ServerMessage.ServerMessageType.CLIENTS_LIST;
            case EXIT -> ServerMessage.ServerMessageType.EXIT;
        };
    }
}

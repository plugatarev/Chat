public record ServerClientMessage(String message, ServerClientMessage.ServerClientMessageType type,
                                  String sender, String receiver) {
    enum ServerClientMessageType {
        MESSAGE,
        FAILED_REGISTRATION,
        CLIENTS_LIST,
        EXIT
    }

    public ServerMessage serverMessage() {
        ServerMessage.ServerMessageType serverMessageType;
        switch (type) {
            case MESSAGE -> serverMessageType = ServerMessage.ServerMessageType.MESSAGE;
            case FAILED_REGISTRATION -> serverMessageType = ServerMessage.ServerMessageType.FAILED_REGISTRATION;
            case CLIENTS_LIST -> serverMessageType = ServerMessage.ServerMessageType.CLIENTS_LIST;
            case EXIT -> serverMessageType = ServerMessage.ServerMessageType.EXIT;
            default -> throw new IllegalStateException("Invalid server message type: " + type);
        }
        return new ServerMessage(message, serverMessageType, sender, receiver);
    }
}

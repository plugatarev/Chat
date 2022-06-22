public record ServerBroadMessage(String message, ServerBroadMessage.ServerBroadMessageType type,
                                 String sender) {
    enum ServerBroadMessageType {
        NEW_CLIENT,
        MESSAGE
    }

    public ServerMessage serverMessage() {
        return new ServerMessage(message, serverMessageType(), sender, null);
    }

    private ServerMessage.ServerMessageType serverMessageType() {
        return switch (type) {
            case MESSAGE -> ServerMessage.ServerMessageType.MESSAGE;
            case NEW_CLIENT -> ServerMessage.ServerMessageType.NEW_CLIENT;
        };
    }
}

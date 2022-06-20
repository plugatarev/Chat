public record ServerBroadMessage(String message, ServerBroadMessage.ServerBroadMessageType type,
                                 String sender) {
    enum ServerBroadMessageType {
        NEW_CLIENT,
        MESSAGE
    }

    public ServerMessage serverMessage() {
        ServerMessage.ServerMessageType type1;
        if (type == ServerBroadMessageType.MESSAGE) {
            type1 = ServerMessage.ServerMessageType.MESSAGE;
        } else {
            type1 = ServerMessage.ServerMessageType.NEW_CLIENT;
        }
        return new ServerMessage(message, type1, sender, null);
    }
}

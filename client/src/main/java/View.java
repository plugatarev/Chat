public record View() {
    public static void update(ServerMessage m, String clientLogin) {
        switch (m.type()) {
            case NEW_CLIENT -> System.out.print("New user: ");
            case CLIENTS_LIST -> System.out.println("Users online:");
            case MESSAGE -> {
                if (clientLogin.equals(m.sender())) System.out.print("> ");
                else if (m.sender() != null) System.out.print(m.sender() + ": ");
            }
        }
        System.out.println(m.message());
    }
}

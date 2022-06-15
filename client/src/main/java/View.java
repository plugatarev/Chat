public record View() {
    public static void update(Message m, MessageType type, String clientLogin) {
        switch (type) {
            case REGISTRATION -> System.out.print("New user: ");
            case SHOW_USERS -> System.out.println("Users online:");
            case SEND_USER -> {
                if (clientLogin.equals(m.senderName())) System.out.print("> ");
                else if (m.senderName() != null) System.out.print(m.senderName() + ": ");
            }
        }
        System.out.println(m.message());
    }
}

public record View() {
    public static void update(Message m) {
        switch (m.type()) {
            case REGISTRATION -> System.out.print("New user: ");
            case SHOW_USERS -> System.out.println("Users online:");
            case SEND_USER, SEND_EVERYBODY, EXIT -> {
                if (m.senderName() != null) System.out.print(m.senderName() + ": ");
            }
        }
        System.out.println(m.message());
    }
}

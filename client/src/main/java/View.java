public record View(Client client) {

    public void update() {
        Message m = client.getLastMessage();
        switch (m.getType()) {
            case REGISTRATION -> System.out.print("New user: ");
            case SHOW_USERS -> System.out.println("Users online:");
            case SEND_USER, SEND_EVERYBODY, EXIT -> {
                if (m.getSenderName() != null) System.out.print(m.getSenderName() + ": ");
            }
        }
        System.out.println(m.getMessage());
    }
}

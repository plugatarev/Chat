public record View(Client client) {

    public void update() {
        Message m = client.getLastMessage();
        switch (m.getType()) {
            case REGISTRATION -> System.out.print("New user: ");
            case SHOW_USERS -> System.out.println("Users online: \n" + m.getMessage());
            case EXIT -> System.out.println("You left the chat");
            case SEND_USER, SEND_EVERYBODY -> System.out.print(m.getSenderName() + ": ");
        }
        System.out.println(m.getMessage());
    }
}

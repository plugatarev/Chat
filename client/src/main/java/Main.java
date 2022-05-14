public class Main {
    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        Thread clientThread = new Thread(client);
        clientThread.start();
        clientThread.join();
    }
}

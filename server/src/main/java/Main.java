public class Main {
    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
        serverThread.join();
    }
}

package task1.client;

public class Main {
    public static void main(String[] args) {
        EchoClient.connectTo("127.0.0.1", 8788).run();
    }
}
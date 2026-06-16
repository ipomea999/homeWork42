package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {

    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    public static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (!server.isClosed()) {
                Socket clientSocket = server.accept();
                pool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            String formatMsg = "Вероятнее всего порт %s занят.%n";
            System.out.printf(formatMsg, port);
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
package task2.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoServer {

    private final int port;

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            try (Socket clientSocket = server.accept()) {
                handle(clientSocket);
            }
        } catch (IOException e) {
            String formatMsg = "Вероятнее всего порт %s занят.%n";
            System.out.printf(formatMsg, port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException {
        try (Scanner scanner = new Scanner(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner serverConsole = new Scanner(System.in, "UTF-8")) {

            while (true) {
                String message = scanner.nextLine().strip();
                System.out.printf("Got: %s%n", message);

                if ("bye".equalsIgnoreCase(message)) {
                    System.out.println("Bye bye");
                    return;
                }

                String response = serverConsole.nextLine();
                writer.println(response);
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Client dropped connection");
        }
    }
}
package task2.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoClient {

    private final String host;
    private final int port;

    private EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static EchoClient connectTo(String host, int port) {
        return new EchoClient(host, port);
    }

    public void run() {
        System.out.printf("напиши 'bye' чтобы выйти%n%n%n");
        try (Socket socket = new Socket(host, port);
             Scanner scanner = new Scanner(System.in, "UTF-8");
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner serverScanner = new Scanner(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {

            while (true) {
                String message = scanner.nextLine();
                writer.println(message);

                if ("bye".equalsIgnoreCase(message)) {
                    return;
                }

                if (serverScanner.hasNextLine()) {
                    String response = serverScanner.nextLine();
                    System.out.printf("Server: %s%n", response);
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.printf("Connection dropped!%n");
        } catch (IOException e) {
            String msg = "Can't connect to %s:%s!%n";
            System.out.printf(msg, host, port);
            e.printStackTrace();
        }
    }
}
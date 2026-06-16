package server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.printf("Подключен клиент: %s%n", socket);
        try (Socket s = socket;
             Scanner reader = getReader(s);
             PrintWriter writer = getWriter(s)) {
            sendResponse("Привет " + socket, writer);
            while (true) {
                String message = reader.nextLine();
                if (isEmptyMsg(message) || isQuitMsg(message)) {
                    break;
                }
                sendResponse(message.toUpperCase(), writer);
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Клиент закрыл соединение!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Клиент отключен: %s%n", socket);
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream());
    }

    private Scanner getReader(Socket socket) throws IOException {
        return new Scanner(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    }

    private boolean isQuitMsg(String message) {
        return "bye".equals(message.toLowerCase());
    }

    private boolean isEmptyMsg(String message) {
        return message == null || message.isBlank();
    }

    private void sendResponse(String response, PrintWriter writer) {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }
}
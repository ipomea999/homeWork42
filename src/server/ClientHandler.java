package server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final String name;
    private PrintWriter writer;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.name = "Guest_" + (100 + new Random().nextInt(900));
    }

    @Override
    public void run() {
        System.out.printf("Подключен клиент: %s%n", socket);
        try (Socket s = socket;
             Scanner reader = getReader(s);
             PrintWriter w = getWriter(s)) {

            this.writer = w;
            EchoServer.clients.add(this);

            sendResponse("Привет, " + name, w);

            while (true) {
                String message = reader.nextLine();
                if (isEmptyMsg(message) || isQuitMsg(message)) {
                    break;
                }
                broadcast(message);
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Клиент закрыл соединение!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            EchoServer.clients.remove(this);
            System.out.printf("Клиент отключен: %s%n", socket);
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : EchoServer.clients) {
            if (client != this) {
                client.sendMessage(name + ": " + message);
            }
        }
    }

    public void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
            writer.flush();
        }
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
        writer.println(response);
        writer.flush();
    }
}
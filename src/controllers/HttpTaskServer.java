package controllers;

import com.sun.net.httpserver.HttpServer;
import handlers.*;
import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private static final int PORT = 8080;
    HttpServer server;
    TaskManager taskManager;

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        try {
            HttpTaskServer server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.taskManager = manager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен. Порт: " + PORT);
    }

    public void stop() {
        server.stop(1);
        System.out.println("Сервер остановлен. Порт: " + PORT);
    }

}

package controllers;

import adapter.DurationAdapter;
import adapter.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    HttpServer server;
    private HttpExchange httpExchange;
    private int responseCode = 404;
    private byte[] response = new byte[0];
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    TaskManager taskManager;

    public static void main(String[] args) {
        TaskManager manager = FileBackedTaskManager.loadFromFile(new File("save/load file/file.csv"));
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
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/subtasks", new TasksHandler());
        server.createContext("/epics", new TasksHandler());
        server.createContext("/history", new TasksHandler());
        server.createContext("/prioritized", new TasksHandler());
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен. Порт: " + PORT);
    }

    public void stop() {
        server.stop(1);
        System.out.println("Сервер остановлен. Порт: " + PORT);
    }

    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {
            String path = exchange.getRequestURI().getPath();
            String[] pathElements = path.split("/");
            int id = 0;
            if (pathElements.length > 2) {
                id = Integer.parseInt(pathElements[2]);
            }
            httpExchange = exchange;
            String method = exchange.getRequestMethod();
            try {
                switch (method) {
                    case "GET":
                        if (path.contains("/tasks/")) {
                            getTaskById(id);
                        } else if (path.endsWith("/tasks")) {
                            getTasks();
                        } else if (path.contains("/epics/") && path.endsWith("/subtasks")) {
                            getEpicSubtasks(id);
                        } else if (path.contains("/epics/")) {
                            getEpicByID(id);
                        } else if (path.endsWith("/epics")) {
                            getEpics();
                        } else if (path.contains("/subtasks/")) {
                            getSubtaskById(id);
                        } else if (path.endsWith("/subtasks")) {
                            getSubtasks();
                        } else if (path.endsWith("/history")) {
                            getHistory();
                        } else if (path.endsWith("/prioritized")) {
                            getPrioritizedTasks();
                        }
                        break;
                    case "POST":
                        if (path.contains("/tasks/")) {
                            updateTask();
                        } else if (path.endsWith("/tasks")) {
                            createTask();
                        } else if (path.endsWith("/epics")) {
                            createEpic();
                        } else if (path.contains("/subtasks/")) {
                            updateSubtask();
                        } else if (path.endsWith("/subtasks")) {
                            createSubtask();
                        }
                        break;
                    case "DELETE":
                        if (path.contains("/tasks/")) {
                            deleteTask(id);
                        } else  if (path.contains("/epics/") && !path.contains("/subtasks")) {
                            deleteEpic(id);
                        } else if (path.contains("/subtasks/")) {
                            deleteSubtask(id);
                        }
                        break;
                    default:
                        throw new IOException();
                }
                exchange.sendResponseHeaders(responseCode, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                    response = new byte[0];
                }
            } catch (IOException e) {
                System.out.println("Ошибка выполнения запроса: " + e.getMessage());
            } finally {
                exchange.close();
            }
        }

        public void getTaskById(int id) {
            if (taskManager.getTaskById(id) != null) {
                String taskJson = gson.toJson(taskManager.getTaskById(id));
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                response = taskJson.getBytes(DEFAULT_CHARSET);
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        public void getTasks() {
            if (!taskManager.getTasks().isEmpty()) {
                String tasksJson = gson.toJson(taskManager.getTasks());
                response = tasksJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        public void getEpicSubtasks(int id) {
            if (taskManager.getEpicById(id).getSubtasks() != null) {
                String epicsSubtasksJson = gson.toJson(taskManager.getEpicById(id).getSubtasks());
                response = epicsSubtasksJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        public void getEpicByID(int id) {
            if (taskManager.getEpicById(id) != null) {
                String epicJson = gson.toJson(taskManager.getEpicById(id));
                response = epicJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        public void getEpics() {
            if (!taskManager.getEpics().isEmpty()) {
                String epicsJson = gson.toJson(taskManager.getEpics());
                response = epicsJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        public void getSubtaskById(int id) {
            if (taskManager.getSubtaskById(id) != null) {
                String subtaskJson = gson.toJson(taskManager.getSubtaskById(id));
                response = subtaskJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        public void getSubtasks() {
            if (!taskManager.getSubtasks().isEmpty()) {
                String subtasksJson = gson.toJson(taskManager.getSubtasks());
                response = subtasksJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        public void getHistory() {
            if (!taskManager.getHistory().isEmpty()) {
                String historyJson = gson.toJson(taskManager.getHistory());
                response = historyJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        public void getPrioritizedTasks() {
            if (!taskManager.getPrioritizedTasks().isEmpty()) {
                String priorTasksJson = gson.toJson(taskManager.getPrioritizedTasks());
                response = priorTasksJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        public void updateTask() throws IOException {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            var task = gson.fromJson(body, Task.class);
            if (taskManager.updateTask(task) == -1) {
                responseCode  = 406;
            } else if (taskManager.updateTask(task) > 0) {
                responseCode = 201;
            }
        }

        public void createTask() throws IOException {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);
            if (taskManager.addTask(task) == 0) {
                responseCode  = 406;
            } else {
                responseCode = 201;
            }
        }

        public void createEpic() throws IOException {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
            if (taskManager.addEpic(epic) == 0) {
                responseCode  = 406;
            } else {
                responseCode = 201;
            }
        }

        public void updateSubtask() throws IOException {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (taskManager.updateTask(subtask) == -1) {
                responseCode  = 406;
            } else if (taskManager.updateSubtask(subtask) > 0) {
                responseCode = 201;
            }
        }

        public void createSubtask() throws IOException {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (taskManager.addSubtask(subtask) == 0) {
                responseCode  = 406;
            } else {
                responseCode = 201;
            }
        }

        public void deleteTask(int id) throws IOException {
            taskManager.deleteTaskById(id);
            responseCode = 200;
        }

        public void deleteEpic(int id) throws IOException {
            taskManager.deleteEpicById(id);
            responseCode = 200;
        }

        public void deleteSubtask(int id) throws IOException {
            taskManager.deleteSubtaskById(id);
            responseCode = 200;
        }

    }
}

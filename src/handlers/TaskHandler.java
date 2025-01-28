package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Task;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    Gson gson;
    TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = getGson();
    }


    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String path = getPath(exchange);
        int id = getIdFromPath(exchange);
        if (path.contains("/tasks/")) {
            getTaskById(id, exchange);
        } else if (path.endsWith("/tasks")) {
            getTasks(exchange);
        }
    }
    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String path = getPath(exchange);
        if (path.contains("/tasks/")) {
            updateTask(exchange);
        } else if (path.endsWith("/tasks")) {
            createTask(exchange);
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);
        String path = getPath(exchange);
        if (path.contains("/tasks/")) {
            deleteTask(id, exchange);
        }
    }

    public void getTaskById(int id, HttpExchange exchange) throws IOException {
        if (taskManager.getTaskById(id) != null) {
            String taskJson = gson.toJson(taskManager.getTaskById(id));
            sendText200(exchange,taskJson);
        } else {
            sendNotFound(exchange, "Task not found");
        }
    }

    public void getTasks(HttpExchange exchange) throws  IOException {
        if (!taskManager.getTasks().isEmpty()) {
            String tasksJson = gson.toJson(taskManager.getTasks());
            sendText200(exchange,tasksJson);
        } else {
            sendNotFound(exchange, "Tasks not found");
        }
    }

    public void updateTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(body, Task.class);
        if (taskManager.updateTask(task) == -1) {
            sendHasInteractions(exchange, "Task date interactions");
        } else if (taskManager.updateTask(task) > 0) {
            sendText201(exchange, "Task updated");
        }
    }

    public void createTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(body, Task.class);
        if (taskManager.addTask(task) == 0) {
            sendHasInteractions(exchange, "Task date interactions");
        } else {
            sendText201(exchange, "Task created");
        }
    }

    public void deleteTask(int id, HttpExchange exchange) throws IOException {
        taskManager.deleteTaskById(id);
        sendText200(exchange, "Task deleted");
    }
}

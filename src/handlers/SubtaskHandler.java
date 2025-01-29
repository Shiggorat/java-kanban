package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Subtask;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    Gson gson;
    TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = getGson();
    }


    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String path = getPath(exchange);
        int id = getIdFromPath(exchange);
        if (path.contains("/subtasks/")) {
            getSubtaskById(id, exchange);
        } else if (path.endsWith("/subtasks")) {
            getSubtasks(exchange);
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String path = getPath(exchange);
        if (path.contains("/subtasks/")) {
            updateSubtask(exchange);
        } else if (path.endsWith("/subtasks")) {
            createSubtask(exchange);
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);
        String path = getPath(exchange);
        if (path.contains("/subtasks/")) {
            deleteSubtask(id, exchange);
        }
    }

    public void getSubtaskById(int id, HttpExchange exchange) throws IOException {
        if (taskManager.getSubtaskById(id) != null) {
            String subtaskJson = gson.toJson(taskManager.getSubtaskById(id));
            sendText200(exchange,subtaskJson);
        } else {
            sendNotFound(exchange, "Subtask not found");
        }
    }

    public void getSubtasks(HttpExchange exchange) throws  IOException {
        if (!taskManager.getSubtasks().isEmpty()) {
            String subtasksJson = gson.toJson(taskManager.getSubtasks());
            sendText200(exchange,subtasksJson);
        } else {
            sendNotFound(exchange, "Subtasks not found");
        }
    }

    public void updateSubtask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        if (taskManager.updateSubtask(subtask) == -1) {
            sendHasInteractions(exchange, "Subtask date interactions");
        } else if (taskManager.updateSubtask(subtask) > 0) {
            sendText201(exchange, "Subtask updated");
        }
    }

    public void createSubtask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        if (taskManager.addSubtask(subtask) == 0) {
            sendHasInteractions(exchange, "Subtask date interactions");
        } else {
            sendText201(exchange, "Subtask created");
        }
    }

    public void deleteSubtask(int id, HttpExchange exchange) throws IOException {
        taskManager.deleteSubtaskById(id);
        sendText200(exchange, "Subtask deleted");
    }
}
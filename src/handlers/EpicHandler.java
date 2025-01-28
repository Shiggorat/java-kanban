package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Epic;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    Gson gson;
    TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = getGson();
    }


    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String path = getPath(exchange);
        int id = getIdFromPath(exchange);
        if (path.contains("/epics/") && path.endsWith("/subtasks")) {
            getEpicSubtasks(id, exchange);
        } else if (path.contains("/epics/")) {
            getEpicByID(id, exchange);
        } else if (path.endsWith("/epics")) {
            getEpics(exchange);
        }
    }
    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String path = getPath(exchange);
        if (path.endsWith("/epics")) {
            createEpic(exchange);
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);
        String path = getPath(exchange);
        if (path.contains("/epics/") && !path.contains("/subtasks")) {
            deleteEpic(id, exchange);
        }
    }

    public void getEpicByID(int id, HttpExchange exchange) throws IOException {
        if (taskManager.getEpicById(id) != null) {
            String epicJson = gson.toJson(taskManager.getEpicById(id));
            sendText200(exchange,epicJson);
        } else {
            sendNotFound(exchange, "Epic not found");
        }
    }

    public void getEpics(HttpExchange exchange) throws  IOException {
        if (!taskManager.getEpics().isEmpty()) {
            String epicsJson = gson.toJson(taskManager.getEpics());
            sendText200(exchange,epicsJson);
        } else {
            sendNotFound(exchange, "Epics not found");
        }
    }

    public void getEpicSubtasks(int id, HttpExchange exchange) throws  IOException {
        if (taskManager.getEpicById(id).getSubtasks() != null) {
            String epicsSubtasksJson = gson.toJson(taskManager.getEpicById(id).getSubtasks());
            sendText200(exchange,epicsSubtasksJson);
        } else {
            sendNotFound(exchange, "Epic`s subtasks not found");
        }
    }

    public void createEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Epic epic = gson.fromJson(body, Epic.class);
        if (taskManager.addEpic(epic) == 0) {
            sendHasInteractions(exchange, "Epic date interactions");
        } else {
            sendText201(exchange, "Epic created");
        }
    }

    public void deleteEpic(int id, HttpExchange exchange) throws IOException {
        taskManager.deleteEpicById(id);
        sendText200(exchange, "Epic deleted");
    }
}


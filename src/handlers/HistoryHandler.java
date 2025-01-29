package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import java.io.IOException;


public class HistoryHandler extends BaseHttpHandler {

    Gson gson;
    TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = getGson();
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String path = getPath(exchange);
        if (path.endsWith("/history")) {
            getHistory(exchange);
        }
    }

    public void getHistory(HttpExchange exchange) throws  IOException {
        if (!taskManager.getHistory().isEmpty()) {
            String historyJson = gson.toJson(taskManager.getHistory());
            sendText200(exchange,historyJson);
        } else {
            sendNotFound(exchange, "History not found");
        }
    }
}
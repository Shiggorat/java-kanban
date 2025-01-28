package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PrioritizedHandler extends BaseHttpHandler {

    Gson gson;
    TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = getGson();
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String path = getPath(exchange);
        if (path.endsWith("/prioritized")) {
            getPrioritizedTasks(exchange);
        }
    }

    public void getPrioritizedTasks(HttpExchange exchange) throws  IOException {
        if (!taskManager.getPrioritizedTasks().isEmpty()) {
            String priorTasksJson = gson.toJson(taskManager.getPrioritizedTasks());
            sendText200(exchange,priorTasksJson);
        } else {
            sendNotFound(exchange, "Prioritized Tasks not found");
        }
    }
}

package handlers;

import adapter.DurationAdapter;
import adapter.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


public  class BaseHttpHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime .class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration .class, new DurationAdapter())
            .create();

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    processGet(exchange);
                    break;
                case "POST":
                    processPost(exchange);
                    break;
                case "DELETE":
                    processDelete(exchange);
                    break;
                default:
                    writeToUser(exchange, "Данный метод не предусмотрен");
            }
        } catch(IOException e) {
            System.out.println("Ошибка выполнения запроса: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    protected void processGet(HttpExchange exchange) throws IOException {}

    protected void processPost(HttpExchange exchange) throws IOException {}

    protected void processDelete(HttpExchange exchange) throws IOException {}

    protected void writeToUser(HttpExchange exchange, String message) throws  IOException {
        sendNotFound(exchange, message);
    }

    protected Gson getGson() {
        return this.gson;
    }

    public int getIdFromPath(HttpExchange exchange) {
        String path = getPath(exchange);
        String[] pathElements = path.split("/");
        int id = 0;
        if (pathElements.length > 2) {
            id = Integer.parseInt(pathElements[2]);
        }
        return id;
    }

    public String getPath(HttpExchange exchange) {
        return  exchange.getRequestURI().getPath();
    }

    protected void sendText200(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendText201(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}



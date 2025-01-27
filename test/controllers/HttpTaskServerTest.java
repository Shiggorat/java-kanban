package controllers;

import adapter.DurationAdapter;
import adapter.LocalDateTimeTypeAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer;
    {
        try {
            taskServer = new HttpTaskServer(taskManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void setUp() {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    protected Task createTask1() {
        return new Task(1, "TASK1", "TaskDescr1", Status.NEW,
                LocalDateTime.of(2025, 1, 10, 14, 33), 600);
    }

    protected Task createTask2() {
        return new Task(1, "TASK2", "TaskDescr2", Status.NEW,
                LocalDateTime.of(2025, 1, 20, 14, 33), 600);
    }

    protected Epic createEpic1() {
        return new Epic(1, "EPIC1", "EpicDescr1", Status.NEW,
                LocalDateTime.of(2025, 1, 10, 14, 33), 100,
                LocalDateTime.of(2025, 1, 12, 11, 15));
    }

    protected Epic createEpic2() {
        return new Epic(1, "EPIC2", "EpicDescr2", Status.NEW,
                LocalDateTime.of(2025, 3, 20, 14, 33), 100,
                LocalDateTime.of(2025, 3, 21, 11, 15));
    }

    protected Subtask createSubtask1(Epic epic) {
        return new Subtask(1, "subtask1", "SubtaskDescr1", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 1, 1, 14, 33), 5);
    }

    protected Subtask createSubtask2(Epic epic) {
        return new Subtask(2,"subtask2", "SubtaskDescr2", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 1, 5, 14, 33), 5);
    }


    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = createTask1();
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");
        assertEquals("TASK1", taskManager.getTasks().getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = createEpic1();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask1(epic);
        String subtJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertNotNull(taskManager.getSubtasks(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество задач");
        assertEquals("subtask1", taskManager.getSubtasks().getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = createEpic1();
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertNotNull(taskManager.getEpics(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество задач");
        assertEquals("EPIC1", taskManager.getEpics().getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = createTask1();
        taskManager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");
        assertEquals(Status.IN_PROGRESS, taskManager.getTasks().getFirst().getStatus(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtaskAndEpicStatus() throws IOException, InterruptedException {
        Epic epic = createEpic1();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask1(epic);
        taskManager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        String subtJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertNotNull(taskManager.getSubtasks(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество задач");
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtasks().getFirst().getStatus(), "Некорректное имя задачи");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpics().getFirst().getStatus(), "Некорректное имя задачи");

    }

    @Test
    public void testDeleteTaskByID() throws IOException, InterruptedException {
        Task task = createTask1();
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");

        url = URI.create("http://localhost:8080/tasks/1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasks().size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteSubtaskByID() throws IOException, InterruptedException {
        Epic epic = createEpic1();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask1(epic);
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество задач");

        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getSubtasks().size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteEpicByID() throws IOException, InterruptedException {
        Epic epic = createEpic1();
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество задач");

        url = URI.create("http://localhost:8080/epics/1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getEpics().size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = createTask1();
        Task task2 = createTask2();
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertEquals(2, arrayTasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = createEpic1();
        Epic epic2 = createEpic2();
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray arrayEpics = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertEquals(2, arrayEpics.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic1 = createEpic1();
        taskManager.addEpic(epic1);
        Subtask subtask1 = createSubtask1(epic1);
        Subtask subtask2 = createSubtask2(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray arraySubtasks = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertEquals(2, arraySubtasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTaskByID() throws IOException, InterruptedException {
        Task task1 = createTask1();
        Task task2 = createTask2();
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task responseTask = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals("TASK2", responseTask.getName(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpicByID() throws IOException, InterruptedException {
        Epic epic1 = createEpic1();
        Epic epic2 = createEpic2();
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals("EPIC2", responseEpic.getName(), "Некорректное количество задач");
    }

    @Test
    public void testGetSubtaskByID() throws IOException, InterruptedException {
        Epic epic1 = createEpic1();
        taskManager.addEpic(epic1);
        Subtask subtask1 = createSubtask1(epic1);
        Subtask subtask2 = createSubtask2(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals("subtask2", responseSubtask.getName(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = createEpic1();
        taskManager.addEpic(epic1);
        Subtask subtask1 = createSubtask1(epic1);
        Subtask subtask2 = createSubtask2(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray arraySubtasks = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertEquals(2, arraySubtasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = createTask1();
        Task task2 = createTask2();
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray arrayHistory = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertEquals(2, arrayHistory.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = createTask1();
        Task task2 = createTask2();
        taskManager.addTask(task1);
        taskManager.addTask(task2);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray arrayPrioritized = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertEquals(2, arrayPrioritized.size(), "Некорректное количество задач");
    }






}

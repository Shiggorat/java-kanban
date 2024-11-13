package controllers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }


    @Test
    void shouldAddNewTaskAndGetItById() {
        Task task1 = new Task("Сходить в магазин", "Купить бананы");
        taskManager.addTask(task1);

        Task savedTask = taskManager.getTaskById(task1.getId());
        assertNotNull(savedTask);
        assertEquals(task1, savedTask);

        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(task1, tasks.getFirst());
    }

    @Test
    void shouldAddNewEpicAndSubtaskAndGetItById() {
        Epic epic1 = new Epic("Поездка на природу", "Нужно выехать в лес");
        taskManager.addEpic(epic1);
        Subtask subtask1ep1 = new Subtask("Заправить машину", "Заехать на заправку", epic1.getId());
        Subtask subtask2ep1 = new Subtask("Купить еду", "Заехать на рынок за едой", epic1.getId());
        taskManager.addSubtask(subtask1ep1);
        taskManager.addSubtask(subtask2ep1);

        Epic savedEpic = taskManager.getEpicById(epic1.getId());
        Subtask savedSubtask1 = taskManager.getSubtaskById(subtask1ep1.getId());
        Subtask savedSubtask2 = taskManager.getSubtaskById(subtask2ep1.getId());

        assertNotNull(savedEpic);
        assertNotNull(savedSubtask2);
        assertEquals(epic1, savedEpic);
        assertEquals(subtask1ep1, savedSubtask1);
        assertEquals(subtask2ep1, savedSubtask2);

        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals(epic1, epics.getFirst());

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertEquals(savedSubtask1, subtasks.getFirst());
    }

    @Test
    public void epicShouldChangeStatusWhenSubtasksChangeStatus() {
        Epic epic1 = new Epic("Поездка на природу", "Нужно выехать в лес");
        taskManager.addEpic(epic1);

        Subtask subtask1ep1 = new Subtask("Заправить машину", "Заехать на заправку", epic1.getId());
        Subtask subtask2ep1 = new Subtask("Купить еду", "Заехать на рынок за едой", epic1.getId());
        taskManager.addSubtask(subtask1ep1);
        taskManager.addSubtask(subtask2ep1);
        subtask1ep1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1ep1);
        assertTrue(Status.IN_PROGRESS == epic1.getStatus());
        subtask1ep1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1ep1);
        subtask2ep1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2ep1);
        assertTrue(Status.DONE == epic1.getStatus());


    }

    @Test
    public void shouldDeleteTasks() {
        Task task1 = new Task("Сходить в магазин", "Купить бананы");
        taskManager.addTask(task1);
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    public void shouldDeleteEpicAndSubtasks() {
        Epic epic1 = new Epic("Поездка на природу", "Нужно выехать в лес");
        taskManager.addEpic(epic1);
        Subtask subtask1ep1 = new Subtask("Заправить машину", "Заехать на заправку", epic1.getId());
        Subtask subtask2ep1 = new Subtask("Купить еду", "Заехать на рынок за едой", epic1.getId());
        taskManager.addSubtask(subtask1ep1);
        taskManager.addSubtask(subtask2ep1);
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());

    }

    @Test
    public void shouldDeleteTasksById() {
        Task task1 = new Task("Сходить в магазин", "Купить бананы");
        taskManager.addTask(task1);
        taskManager.deleteTaskById(1);
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    public void shouldDeleteEpicAndSubtasksById() {
        Epic epic1 = new Epic("Поездка на природу", "Нужно выехать в лес");
        taskManager.addEpic(epic1);
        Subtask subtask1ep1 = new Subtask("Заправить машину", "Заехать на заправку", epic1.getId());
        Subtask subtask2ep1 = new Subtask("Купить еду", "Заехать на рынок за едой", epic1.getId());
        taskManager.addSubtask(subtask1ep1);
        taskManager.addSubtask(subtask2ep1);
        taskManager.deleteSubtaskById(2);
        assertTrue(taskManager.getSubtasks().size() == 1);

        Epic epic2 = new Epic("Сходить в зал", "Потренить спину");
        taskManager.addEpic(epic2);
        Subtask subtask1ep2 = new Subtask("Заправить кровать", "Поменять наволочку", epic2.getId());
        taskManager.addSubtask(subtask1ep2);
        taskManager.deleteEpicById(4);
        assertTrue(taskManager.getEpics().size() == 1);

        assertTrue(taskManager.getSubtasks().size() == 1);

    }
}
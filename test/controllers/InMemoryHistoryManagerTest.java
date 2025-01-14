package controllers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    protected Task createTask1() {
        return new Task("TASK1", "TaskDescr1", LocalDateTime.of(2025, 1, 10, 14, 33), 600);
    }

    protected Task createTask2() {
        return new Task("TASK2", "TaskDescr2", LocalDateTime.of(2026, 1, 10, 14, 33), 600);
    }

    protected Epic createEpic1() {

        return new Epic("EPIC1", "EpicDescr1",
                LocalDateTime.of(2025, 1, 10, 14, 33), 100,
                LocalDateTime.of(2025, 1, 12, 11, 15));
    }

    protected Epic createEpic2() {

        return new Epic("EPIC2", "EpicDescr2",
                LocalDateTime.of(2026, 1, 10, 14, 33), 100,
                LocalDateTime.of(2026, 1, 12, 11, 15));
    }

    protected Subtask createSubtask1(Epic epic) {
        return new Subtask("subtask1", "SubtaskDescr1", epic.getId(),
                LocalDateTime.of(2025, 1, 1, 14, 33), 5);
    }

    protected Subtask createSubtask2(Epic epic) {
        return new Subtask("subtask2", "SubtaskDescr2", epic.getId(),
                LocalDateTime.of(2025, 1, 5, 14, 33), 5);
    }



    @Test
    public void historyShouldReturnNotUpdatedTask() {
        Task task1 = createTask1();
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.updateTask(createTask2());

        assertEquals(task1.getName(), taskManager.getHistory().get(0).getName()
                , "Изначальный таск не сохранился");
        assertEquals(task1.getDescription(), taskManager.getHistory().getFirst().getDescription()
                , "Изначальный таск не сохранился");
    }

    @Test
    public void historyShouldReturnNotUpdatedEpic() {
        Epic epic1 = createEpic1();
        taskManager.addEpic(epic1);
        taskManager.getEpicById(epic1.getId());
        taskManager.updateEpic(createEpic2());

        assertEquals(epic1.getName(), taskManager.getHistory().getFirst().getName()
                , "Изначальный эпик не сохранился");
        assertEquals(epic1.getDescription(), taskManager.getHistory().getFirst().getDescription()
                , "Изначальный эпик не сохранился");
    }

    @Test
    public void historyShouldReturnNotUpdatedSubtask() {
        Epic epic1 = createEpic1();
        taskManager.addEpic(epic1);
        Subtask sub1 = createSubtask1(epic1);
        taskManager.addSubtask(sub1);
        taskManager.getSubtaskById(sub1.getId());
        taskManager.updateSubtask(new Subtask("Ходить туда-сюда", "Ходить долго", epic1.getId()));

        assertEquals(sub1.getName(), taskManager.getHistory().getFirst().getName()
                , "Изначальный сабтаск не сохранился");
        assertEquals(sub1.getDescription(), taskManager.getHistory().getFirst().getDescription()
                , "Изначальный сабтаск не сохранился");
    }

    @Test
    public void historyShouldNotIncreaseSizeIfAddedTheSameTask() {
        Task task1 = createTask1();
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        assertEquals(1, taskManager.getHistory().size()
                , "Размер истории больше 1");
    }

    @Test
    public void historyShouldNotIncreaseSizeIfAddedTheSameEpic() {
        Epic epic1 = createEpic1();
        taskManager.addEpic(epic1);
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        assertEquals(1, taskManager.getHistory().size()
                , "Размер истории больше 1");
    }

    @Test
    public void historyShouldNotIncreaseSizeIfAddedTheSameSubtask() {
        Epic epic1 = createEpic1();
        taskManager.addEpic(epic1);
        Subtask sub1 = createSubtask1(epic1);
        taskManager.addSubtask(sub1);
        taskManager.getSubtaskById(sub1.getId());
        taskManager.getSubtaskById(sub1.getId());

        assertEquals(1, taskManager.getHistory().size()
                , "Размер истории больше 1");
    }

    @Test
    public void historyShouldRemoveTask() {
        Task task1 = createTask1();
        Task task2 = createTask2();
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.deleteTaskById(task1.getId());
        assertEquals(1, taskManager.getHistory().size()
                , "Размер истории больше 1, таск не был удален");

        taskManager.deleteTaskById(task2.getId());
        assertEquals(0, taskManager.getHistory().size()
                , "Размер истории больше 0, таск не был удален");
    }

    @Test
    public void historyShouldAddTaskAtTheEndOfTheList() {
        Task task1 = createTask1();
        Task task2 = createTask2();
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        assertEquals(task1.getName(), taskManager.getHistory().getLast().getName()
                , "Таск не был добавлен в конец истории");
    }









}
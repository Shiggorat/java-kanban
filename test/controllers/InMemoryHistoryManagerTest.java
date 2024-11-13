package controllers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void historyListShouldNotBeMoreThan10() {
        for(int i = 0; i < 13; i++) {
            taskManager.addTask(new Task("пырыры", "парара"));
        }
        for(Task task : taskManager.getTasks()) {
            taskManager.getTaskById(task.getId());
        }
        assertEquals(10, taskManager.getHistory().size(), "Размер истории не должен быть больше 10");
    }

    @Test
    public void historyShouldReturnNotUpdatedTask() {
        Task task1 = new Task("пырыры", "парара");
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.updateTask(new Task(task1.getId(), "Погулять", "Гулять долго", Status.IN_PROGRESS));

        assertEquals(task1.getName(), taskManager.getHistory().getFirst().getName()
                , "Изначальный таск не сохранился");
        assertEquals(task1.getDescription(), taskManager.getHistory().getFirst().getDescription()
                , "Изначальный таск не сохранился");
    }

    @Test
    public void historyShouldReturnNotUpdatedEpic() {
        Epic epic1 = new Epic("пырыры", "парара");
        taskManager.addEpic(epic1);
        taskManager.getEpicById(epic1.getId());
        taskManager.updateEpic(new Epic(epic1.getId(), "Погулять", "Гулять долго"));

        assertEquals(epic1.getName(), taskManager.getHistory().getFirst().getName()
                , "Изначальный эпик не сохранился");
        assertEquals(epic1.getDescription(), taskManager.getHistory().getFirst().getDescription()
                , "Изначальный эпик не сохранился");
    }

    @Test
    public void historyShouldReturnNotUpdatedSubtask() {
        Epic epic1 = new Epic("Пойти туда", "Пойти сюда");
        taskManager.addEpic(epic1);
        Subtask sub1 = new Subtask("Сюда иду", "Туда иду", epic1.getId());
        taskManager.addSubtask(sub1);
        taskManager.getSubtaskById(sub1.getId());
        taskManager.updateSubtask(new Subtask("Ходить туда-сюда", "Ходить долго", epic1.getId()));

        assertEquals(sub1.getName(), taskManager.getHistory().getFirst().getName()
                , "Изначальный сабтаск не сохранился");
        assertEquals(sub1.getDescription(), taskManager.getHistory().getFirst().getDescription()
                , "Изначальный сабтаск не сохранился");
    }



}
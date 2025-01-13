package controllers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    protected abstract T getNewTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = getNewTaskManager();
    }




    protected Task createTask() {
        return new Task("TASK1", "TaskDescr1", LocalDateTime.of(2025, 1, 10, 14, 33), 600);
    }

    protected Epic createEpic() {

        return new Epic("EPIC1", "EpicDescr1",
                LocalDateTime.of(2025, 1, 10, 14, 33), 100,
                LocalDateTime.of(2025, 1, 12, 11, 15));
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask("subtask1", "SubtaskDescr1", epic.getId(),
                LocalDateTime.of(2025, 1, 1, 14, 33), 5);
    }

    @Test
    public void shouldMakeEpicStartTimeAndDurationAsSubtaskAfterUpdate() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);
        assertTrue(epic.getStartTime() != subtask.getStartTime());
        assertTrue(epic.getDuration() != subtask.getDuration());

        taskManager.updateEpic(epic);
        assertEquals(epic.getStartTime(), subtask.getStartTime());
        assertEquals(epic.getDuration(), subtask.getDuration());
    }

    @Test
    public void shouldNotAddToPrioritizedTasksIfTimeCrossed() {
        Task task1 = createTask();
        Task task2 = createTask();
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addPrioritizedTasks(task1);
        taskManager.addPrioritizedTasks(task2);
        assertTrue(taskManager.getPrioritizedTasks().size() == 1);
    }

    @Test
    public void shouldAddToPrioritizedTasksIfTimeIsNotCrossed() {
        Task task1 = new Task("TASK1", "TaskDescr1", LocalDateTime.of(2025, 1, 10, 14, 33), 6);
        Task task2 = new Task("TASK2", "TaskDescr2", LocalDateTime.of(2025, 1, 10, 16, 33), 15);
        Task task3 = new Task("TASK3", "TaskDescr3", LocalDateTime.of(2024, 10, 22, 10, 25), 60);

        Task task4 = new Task("TASK4", "TaskDescr4", LocalDateTime.of(2024, 10, 22, 10, 25), 60);
        task4.setStartTime(task3.getStartTime());
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addPrioritizedTasks(task1);
        taskManager.addPrioritizedTasks(task2);
        taskManager.addPrioritizedTasks(task3);
        assertTrue(taskManager.getPrioritizedTasks().size() == 3);
    }



        @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = createTask();
        taskManager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToInDone() {
        Task task = createTask();
        taskManager.addTask(task);
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        assertEquals(Status.DONE, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInDone() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToDone() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        assertEquals(Status.DONE, taskManager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }


    @Test
    public void shouldReturnHistoryWithTasks() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        List<Task> list = taskManager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }

    @Test
    void shouldAddNewTaskAndGetItById() {
        Task task1 = createTask();
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
        Epic epic1 = createEpic();
        taskManager.addEpic(epic1);
        Subtask subtask1ep1 = createSubtask(epic1);
        Subtask subtask2ep1 = createSubtask(epic1);
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
        Epic epic1 = createEpic();
        taskManager.addEpic(epic1);

        Subtask subtask1ep1 = createSubtask(epic1);
        Subtask subtask2ep1 = createSubtask(epic1);
        taskManager.addSubtask(subtask1ep1);
        taskManager.addSubtask(subtask2ep1);
        assertTrue(Status.NEW == epic1.getStatus());

        subtask1ep1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1ep1);
        assertTrue(Status.IN_PROGRESS == epic1.getStatus());

        subtask1ep1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1ep1);
        subtask2ep1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2ep1);
        assertTrue(Status.IN_PROGRESS == epic1.getStatus());

        subtask1ep1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1ep1);
        subtask2ep1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2ep1);
        assertTrue(Status.DONE == epic1.getStatus());


    }

    @Test
    public void shouldDeleteTasks() {
        Task task1 = createTask();
        taskManager.addTask(task1);
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    public void shouldDeleteEpicAndSubtasks() {
        Epic epic1 = createEpic();
        taskManager.addEpic(epic1);
        Subtask subtask1ep1 = createSubtask(epic1);
        Subtask subtask2ep1 = createSubtask(epic1);
        taskManager.addSubtask(subtask1ep1);
        taskManager.addSubtask(subtask2ep1);
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty());

    }

    @Test
    public void shouldDeleteTasksById() {
        Task task1 = createTask();
        taskManager.addTask(task1);
        taskManager.deleteTaskById(1);
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void shouldDeleteEpicAndSubtasksById() {
        Epic epic1 = createEpic();
        taskManager.addEpic(epic1);
        Subtask subtask1ep1 = createSubtask(epic1);
        Subtask subtask2ep1 = createSubtask(epic1);
        taskManager.addSubtask(subtask1ep1);
        taskManager.addSubtask(subtask2ep1);
        taskManager.deleteSubtaskById(2);
        assertTrue(taskManager.getSubtasks().size() == 1);

        Epic epic2 = createEpic();
        taskManager.addEpic(epic2);
        Subtask subtask1ep2 = createSubtask(epic2);
        taskManager.addSubtask(subtask1ep2);
        taskManager.deleteEpicById(4);
        assertTrue(taskManager.getEpics().size() == 1);

        assertTrue(taskManager.getSubtasks().size() == 1);

    }

}

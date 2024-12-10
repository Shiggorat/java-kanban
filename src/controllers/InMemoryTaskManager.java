package controllers;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> taskList;
    protected final HashMap<Integer, Epic> epicList;
    protected final HashMap<Integer, Subtask> subtaskList;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();


    private int taskId = 0;

    public InMemoryTaskManager() {
        this.taskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.subtaskList = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public int idGenerator() {
        return ++taskId;
    }

    @Override
    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epicList.containsKey(epic.getId())) {
            return;
        }
        Epic oldEpic = epicList.get(epic.getId());
        epicList.put(epic.getId(), epic);
        epic.setSubtasks(oldEpic.getSubtasks());
        epicCheckStatus(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtaskList.containsKey(subtask.getId())) {
            return;
        }
        subtaskList.put(subtask.getId(), subtask);
        Epic epic = epicList.get(subtask.getEpicId());
        if (!epic.getSubtasks().contains(subtask)) {
            epic.getSubtasks().add(subtask);
        } else {
            epic.getSubtasks().remove(subtask);
            epic.getSubtasks().add(subtask);
        }
        epicCheckStatus(epic);
    }


    @Override
    public int addTask(Task task) {
        task.setId(idGenerator());
        taskList.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(idGenerator());
        epicList.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        subtask.setId(idGenerator());
        subtaskList.put(subtask.getId(), subtask);
        Epic epic = epicList.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        epicCheckStatus(epic);
        return subtask.getId();
    }

    @Override
    public void epicCheckStatus(Epic epic) {
        int newStat = 0;
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (subtask.getStatus() == Status.NEW) {
                newStat++;
            }
        }
        if (newStat == epic.getSubtasks().size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.DONE);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task taskClone = taskList.get(id);
        if (!taskList.isEmpty() && taskList.containsKey(id)) {
            historyManager.add(taskClone);
        }
        return taskClone;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epicClone = epicList.get(id);
        if (!epicList.isEmpty() && epicList.containsKey(id)) {
            historyManager.add(epicClone);
        }
        return epicClone;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtClone = subtaskList.get(id);
        if (!subtaskList.isEmpty() && subtaskList.containsKey(id)) {
            historyManager.add(subtClone);
        }
        return subtClone;
    }

    @Override
    public void deleteEpics() {
        for (Epic value : epicList.values()) {
            if (historyManager.getHistory().contains(value)) {
                historyManager.remove(value.getId());
            }
        }
        for (Subtask value : subtaskList.values()) {
            if (historyManager.getHistory().contains(value)) {
                historyManager.remove(value.getId());
            }
        }
        epicList.clear();
        subtaskList.clear();
    }

    @Override
    public void deleteTasks() {
        for (Task value : taskList.values()) {
            if (historyManager.getHistory().contains(value)) {
                historyManager.remove(value.getId());
            }
        }
        taskList.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask value : subtaskList.values()) {
            if (historyManager.getHistory().contains(value)) {
                historyManager.remove(value.getId());
            }
        }
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.clearEpicsSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        taskList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Subtask> epicSubtasks = epicList.get(id).getSubtasks();
        epicList.remove(id);
        for (Subtask subtask : epicSubtasks) {
            subtaskList.remove(subtask.getId());
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtaskList.get(id);
        int epicID = subtask.getEpicId();
        subtaskList.remove(id);
        Epic epic = epicList.get(epicID);
        ArrayList<Subtask> subtaskList = epic.getSubtasks();
        subtaskList.remove(subtask);
        epic.setSubtasks(subtaskList);
        updateEpic(epic);
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicList.values());
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskList.values());
    }

}

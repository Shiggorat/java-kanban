package controllers;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, Epic> epicList;
    private final HashMap<Integer, Subtask> subtaskList;
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    private int taskId = 0;

    public InMemoryTaskManager() {
        this.taskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.subtaskList = new HashMap<>();
    }

    @Override
    public List<Task> getHistory(){
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
        if(!taskList.isEmpty()) {
            historyManager.add(taskList.get(id));
        }
        return taskList.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if(!epicList.isEmpty()) {
            historyManager.add(epicList.get(id));
        }
        return epicList.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if(!subtaskList.isEmpty()) {
            historyManager.add(subtaskList.get(id));
        }
        return subtaskList.get(id);
    }

    @Override
    public void deleteEpics() {
        epicList.clear();
        subtaskList.clear();
    }

    @Override
    public void deleteTasks() {
        taskList.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.clearEpicsSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        taskList.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Subtask> epicSubtasks = epicList.get(id).getSubtasks();
        epicList.remove(id);
        for (Subtask subtask : epicSubtasks) {
            subtaskList.remove(subtask.getId());
        }
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
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList(epicList.values());
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList(taskList.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList(subtaskList.values());
    }

}

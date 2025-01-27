package controllers;

import java.time.LocalDateTime;
import java.util.*;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> taskList;
    protected final HashMap<Integer, Epic> epicList;
    protected final HashMap<Integer, Subtask> subtaskList;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Comparator<Task> compareStartTime = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(compareStartTime);


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
    public int updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            if (task.getStartTime().toString().equals(taskList.get(task.getId()).getStartTime().toString())) {
                taskList.put(task.getId(), task);
                return task.getId();
            } else if (!checkCrossingTime(task)) {
                taskList.put(task.getId(), task);
                return task.getId();
            } else return -1;
        }
        return 0;
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
        if (!epic.getSubtasks().isEmpty() || epic.getSubtasks().stream().anyMatch(sub -> sub.getStartTime() != null)) {
            updateEpicTime(epic);
        }
    }

    public void updateEpicTime(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasks();
        LocalDateTime startTime = subtasks.getFirst().getStartTime();
        LocalDateTime endTime = subtasks.getLast().getEndTime();

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        if (subtaskList.containsKey(subtask.getId())) {
            if (subtask.getStartTime().toString().equals(subtaskList.get(subtask.getId()).getStartTime().toString()) || !checkCrossingTime(subtask)) {
                subtaskList.put(subtask.getId(), subtask);
                Epic epic = epicList.get(subtask.getEpicId());
                if (!epic.getSubtasks().contains(subtask)) {
                    epic.getSubtasks().add(subtask);
                } else {
                    epic.getSubtasks().remove(subtask);
                    epic.getSubtasks().add(subtask);
                }
                epicCheckStatus(epic);
                return subtask.getId();
            } else {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public int addTask(Task task) {
        if (!checkCrossingTime(task)) {
            task.setId(idGenerator());
            taskList.put(task.getId(), task);
            addPrioritizedTasks(task);
            return task.getId();
        }
        return 0;
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(idGenerator());
        epicList.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (!checkCrossingTime(subtask)) {
            subtask.setId(idGenerator());
            subtaskList.put(subtask.getId(), subtask);
            Epic epic = epicList.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            epicCheckStatus(epic);
            addPrioritizedTasks(subtask);
            return subtask.getId();
        }
        return 0;
    }

    @Override
    public void epicCheckStatus(Epic epic) {
        int newStat = 0;
        int doneStat = 0;
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (subtask.getStatus() == Status.NEW) {
                newStat++;
            }
            if (subtask.getStatus() == Status.DONE) {
                doneStat++;
            }
        }
        if (newStat == epic.getSubtasks().size()) {
            epic.setStatus(Status.NEW);
        } else if (doneStat == epic.getSubtasks().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
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
        epicList.values().forEach(value -> {
            if (historyManager.getHistory().contains(value)) {
                historyManager.remove(value.getId());
            }
            prioritizedTasks.removeIf(epicTime -> epicTime.getId() == value.getId());
        });
        subtaskList.values().forEach(subtask -> {
            if (historyManager.getHistory().contains(subtask)) {
                historyManager.remove(subtask.getId());
            }
            prioritizedTasks.removeIf(subTime -> subTime.getId() == subtask.getId());
        });
        epicList.clear();
        subtaskList.clear();
    }

    @Override
    public void deleteTasks() {
        taskList.values().forEach(value -> {
            if (historyManager.getHistory().contains(value)) {
                historyManager.remove(value.getId());
            }
            prioritizedTasks.removeIf(taskTime -> taskTime.getId() == value.getId());
        });
        taskList.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtaskList.values().forEach(value -> {
            if (historyManager.getHistory().contains(value)) {
                historyManager.remove(value.getId());
            }
            prioritizedTasks.removeIf(subtaskTime -> subtaskTime.getId() == value.getId());
        });
        subtaskList.clear();
        epicList.values().forEach(epicVal -> {
            epicVal.clearEpicsSubtasks();
            epicVal.setStatus(Status.NEW);
        });
    }

    @Override
    public void deleteTaskById(int id) {
        taskList.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Subtask> epicSubtasks = epicList.get(id).getSubtasks();
        epicList.remove(id);
        for (Subtask subtask : epicSubtasks) {
            subtaskList.remove(subtask.getId());
        }
        historyManager.remove(id);
        prioritizedTasks.removeIf(epic -> epic.getId() == id);
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
        prioritizedTasks.removeIf(subtaskTime -> subtaskTime.getId() == id);
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

    public boolean checkCrossingTime(Task task) {
        if (this.prioritizedTasks.isEmpty() && task.getStartTime() != null) {
            return false;
        } else if (task.getStartTime() != null) {
            int count = 1;
            List<Task> tasks = getPrioritizedTasks();
                for (Task element : tasks) {
                    if (task.getStartTime().isBefore(element.getStartTime()) && task.getEndTime().isBefore(element.getStartTime())
                            || task.getStartTime().isAfter(element.getEndTime()) && task.getEndTime().isAfter(element.getEndTime())) {
                        count++;
                    }
                }
            return count == tasks.size();
        }
        return true;
    }

    @Override
    public void addPrioritizedTasks(Task task) {
        this.prioritizedTasks.add(task);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return this.prioritizedTasks.stream().toList();
    }

}

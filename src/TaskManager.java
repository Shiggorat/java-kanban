import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {

    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, Epic> epicList;
    private final HashMap<Integer, Subtask> subtaskList;

    private int taskId = 0;

    public TaskManager() {
        this.taskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.subtaskList = new HashMap<>();
    }

    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (!epicList.containsKey(epic.getId())) {
            return;
        }
        Epic oldEpic = epicList.get(epic.getId());
        epicList.put(epic.getId(), epic);
        epic.setSubtasks(oldEpic.getSubtasks());
        epicCheckStatus(epic);
    }

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


    public void addTask(Task task) {
        task.setId(idGenerator());
        taskList.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(idGenerator());
        epicList.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(idGenerator());
        subtaskList.put(subtask.getId(), subtask);
        Epic epic = epicList.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        epicCheckStatus(epic);
    }

    public void epicCheckStatus(Epic epic) {
        int doneStat = 0;
        int newStat = 0;
        for (Subtask subtask : epic.getSubtasks()) {
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

    public Task getTaskById(int id) {
        return taskList.get(id);
    }

    public Epic getEpicById(int id) {
        return epicList.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtaskList.get(id);
    }

    public void deleteEpics() {
        epicList.clear();
        subtaskList.clear();
    }

    public void deleteTasks() {
        taskList.clear();
    }

    public void deleteSubtasks() {
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.clearEpicsSubtasks();
            epic.setStatus(Status.NEW);
        }
    }


    public ArrayList<Epic> getEpics() {
        return new ArrayList(epicList.values());
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList(taskList.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList(subtaskList.values());
    }

    private int idGenerator() {
        return ++taskId;
    }

}

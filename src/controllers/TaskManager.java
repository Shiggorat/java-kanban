package controllers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    void epicCheckStatus(Epic epic);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void deleteEpics();

    void deleteTasks();

    void deleteSubtasks();

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    ArrayList<Epic> getEpics();

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    int idGenerator();

    List<Task> getHistory();
}

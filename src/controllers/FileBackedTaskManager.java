package controllers;

import exceptions.ManagerSaveException;
import model.*;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {


    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("save/load file/file.csv", StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : super.getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : super.getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : super.getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        InMemoryTaskManager fileBacked = new FileBackedTaskManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> readedLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                readedLines.add(line);
            }
            for (int i = 1; i < readedLines.size(); i++) {
                Task task = ((FileBackedTaskManager) fileBacked).fromString(readedLines.get(i));
                switch (task.getType()) {
                    case TASK:
                        fileBacked.addTask(task);
                        break;
                    case EPIC:
                        fileBacked.addEpic((Epic) task);
                        break;
                    case SUBTASK:
                        fileBacked.addSubtask((Subtask) task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ((FileBackedTaskManager) fileBacked);
    }


    public String toString(Task task) {
        String line = null;
        line = task.getId() + ", " + task.getType() + ", " + task.getName() +
                        ", " + task.getStatus() + ", " + task.getDescription();
        return line;
    }

    public String toString(Epic epic) {
        String line = null;
        line = epic.getId() + ", " + epic.getType() + ", " + epic.getName() +
                ", " + epic.getStatus() + ", " + epic.getDescription();
        return line;
    }

    public String toString(Subtask subtask) {
        String line = null;
        line = subtask.getId() + ", " + subtask.getType() + ", " + subtask.getName() +
                ", " + subtask.getStatus() + ", " + subtask.getDescription() + ", " + subtask.getEpicId();
        return line;
    }

    public Task fromString(String value) {
        String[] values = value.split(",");
        int id = Integer.parseInt(values[0].trim());
        TaskType type = TaskType.valueOf(values[1].trim());
        String name = values[2];
        Status status = Status.valueOf(values[3].trim());
        String description = values[4];

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(values[5].trim());;
                return new Subtask(id, name, description, status, epicId);

        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }


    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public void epicCheckStatus(Epic epic) {
        super.epicCheckStatus(epic);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        save();
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        save();
        return super.getEpicById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        save();
        return super.getSubtaskById(id);
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return super.getSubtasks();
    }
}

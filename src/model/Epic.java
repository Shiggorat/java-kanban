package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks = new ArrayList<>();
    private final TaskType type = TaskType.EPIC;


    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return this.subtasks;
    }

    public void clearEpicsSubtasks() {
        this.subtasks.clear();
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public TaskType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        String info = "Epic={" +
                "name= " + getName() +
                ", description= " + getDescription() +
                ", status= " + getStatus() +
                ", id= " + getId();
        if (!subtasks.isEmpty()) {
            info += ", subtasks= " + subtasks.size() + "}";
        } else {
            info += "}";
        }
        return info;
    }


}

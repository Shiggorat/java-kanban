package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description, LocalDateTime startTime, long duration, LocalDateTime endTime) {
        super(name, description, startTime, duration);
        this.endTime = endTime;
    }

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, long duration, LocalDateTime endTime) {
        super(id, name, description, status, startTime, duration);
        this.endTime = endTime;
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

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtasks.isEmpty()) {
            return super.getStartTime();
        } else {
            return getSubtasks().getFirst().getStartTime();
        }
    }

    @Override
    public Duration getDuration() {
        if (subtasks.isEmpty()) {
            return super.getDuration();
        }
        List<Subtask> subtaskList = getSubtasks();
        Duration sum = subtaskList.getFirst().getDuration();
        if (subtaskList.size() == 1) {
            return sum;
        } else {
            for (int i = 1; i < subtaskList.size(); i++) {
                sum = sum.plus(subtaskList.get(i).getDuration());
            }
        }
        return sum;
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

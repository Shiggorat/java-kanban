package model;


import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;



    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId, LocalDateTime startTime, long duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, LocalDateTime startTime, long duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }


    public int getEpicId() {
        return this.epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask={" +
                "name= " + getName() +
                ", description= " + getDescription() +
                ", status= " + getStatus() +
                ", id= " + getId() +
                ", epicId= " + getEpicId() + "}";
    }

}

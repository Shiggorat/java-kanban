package model;

public class Subtask extends Task {

    private final int epicId;
    private final TaskType type = TaskType.SUBTASK;

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return this.epicId;
    }

    public TaskType getType() {
        return this.type;
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

package model;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    private String name;
    private String description;
    private int id;
    private Status status;
    private final TaskType type = TaskType.TASK;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }


    public Task(String name, String description, LocalDateTime startTime, long duration) {
        this.description = description;
        this.name = name;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
        this.status = Status.NEW;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return this.type;
    }

    public LocalDateTime getEndTime() {
        return this.startTime.plus(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }

        Task otherTask = (Task) object;
        if (this.id == ((Task) object).id) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.id * 31;
    }

    @Override
    public String toString() {
        return "Task={" +
                "name= " + getName() +
                ", description= " + getDescription() +
                ", status= " + getStatus() +
                ", id= " + getId() +
                "}";
    }

}

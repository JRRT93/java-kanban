package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String taskName;
    protected String description;
    protected TaskStatus status;
    protected int identifier;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm");


    public Task(String taskName, String description, int identifier, long minutes, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.identifier = identifier;
        this.duration = Duration.ofMinutes(minutes);
        this.startTime = startTime;
    }

    public Task(String taskName, String description, int identifier) { //todo попробовать выбросить здесь исключение, обработать его
        //в конструкторе эпике
        this.taskName = taskName;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.identifier = identifier;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", identifier=" + identifier +
                ", startTime=" + startTime.format(formatter) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (identifier != task.identifier) return false;
        if (!Objects.equals(taskName, task.taskName)) return false;
        if (!Objects.equals(description, task.description)) return false;
        return status == task.status;
    }

    @Override
    public int hashCode() {
        int result = taskName != null ? taskName.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + identifier;
        return result;
    }
}

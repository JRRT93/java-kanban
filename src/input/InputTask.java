package input;

import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class InputTask {
    protected String taskName;
    protected String description;
    protected TaskStatus status;
    protected int identifier;
    protected Duration duration;
    protected LocalDateTime startTime;

    public InputTask(String taskName, String description, long minutes, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.duration = Duration.ofMinutes(minutes);
        this.startTime = startTime;
    }

    public InputTask(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
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

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "taskManager.Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}


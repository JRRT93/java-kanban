package input;

import tasks.TaskStatus;
import util.DefaultFormatter;

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
        return "InputTask{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", identifier=" + identifier +
                ", duration=" + duration +
                ", startTime=" + startTime.format(DefaultFormatter.FORMATTER) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputTask inputTask = (InputTask) o;

        if (identifier != inputTask.identifier) return false;
        if (!taskName.equals(inputTask.taskName)) return false;
        if (!description.equals(inputTask.description)) return false;
        if (status != inputTask.status) return false;
        if (duration != null ? !duration.equals(inputTask.duration) : inputTask.duration != null) return false;
        return startTime != null ? startTime.equals(inputTask.startTime) : inputTask.startTime == null;
    }

    @Override
    public int hashCode() {
        int result = taskName.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + identifier;
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        return result;
    }
}


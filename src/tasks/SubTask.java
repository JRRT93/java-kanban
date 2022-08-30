package tasks;

import com.google.gson.annotations.Expose;
import util.DefaultFormatter;

import java.time.LocalDateTime;

public class SubTask extends Task {
    @Expose
    private EpicTask relatedEpicTask;

    public SubTask(String taskName, String description, int identifier, EpicTask relatedEpicTask, long minutes,
                   LocalDateTime startTime) {
        super(taskName, description, identifier, minutes, startTime);
        this.relatedEpicTask = relatedEpicTask;
    }

    public EpicTask getRelatedEpicTask() {
        return relatedEpicTask;
    }

    public void setRelatedEpicTask(EpicTask relatedEpicTask) {
        this.relatedEpicTask = relatedEpicTask;
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
        if (relatedEpicTask != null) {
            relatedEpicTask.updateEpicTaskStatus();
        }
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "relatedEpicTask='" + relatedEpicTask.getIdentifier() + '\'' +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", identifier=" + identifier +
                ", startTime=" + startTime.format(DefaultFormatter.FORMATTER) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SubTask subTask = (SubTask) o;

        return  relatedEpicTask.getIdentifier() == subTask.relatedEpicTask.getIdentifier() &&
                relatedEpicTask.getTaskName().equals(subTask.relatedEpicTask.getTaskName()) &&
                relatedEpicTask.getDescription().equals(subTask.relatedEpicTask.getDescription()) &&
                relatedEpicTask.getStatus().equals(subTask.relatedEpicTask.getStatus());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (relatedEpicTask != null ? relatedEpicTask.hashCode() : 0);
        return result;
    }


}

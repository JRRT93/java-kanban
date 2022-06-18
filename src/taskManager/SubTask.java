package taskManager;

import java.util.Objects;

public class SubTask extends Task {
    private EpicTask relatedEpicTask;

    SubTask(String taskName, String description, int identifier, EpicTask relatedEpicTask) {
        super(taskName, description, identifier);
        this.relatedEpicTask = relatedEpicTask;
    }

    public EpicTask getRelatedEpicTask() {
        return relatedEpicTask;
    }

    public void setRelatedEpicTask(EpicTask relatedEpicTask) {
        this.relatedEpicTask = relatedEpicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "relatedEpicTask='" + relatedEpicTask.getTaskName() + '\'' +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", identifier=" + identifier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SubTask subTask = (SubTask) o;

        return Objects.equals(relatedEpicTask, subTask.relatedEpicTask);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (relatedEpicTask != null ? relatedEpicTask.hashCode() : 0);
        return result;
    }
}

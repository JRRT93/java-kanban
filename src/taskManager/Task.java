package taskManager;

public class Task {
    protected String taskName;
    protected String description;
    protected String status;
    protected int identifier;

    Task(String taskName, String description, int identifier) {
        this.taskName = taskName;
        this.description = description;
        this.status = "NEW";
        this.identifier = identifier;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "taskManager.Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", identifier=" + identifier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (identifier != task.identifier) return false;
        if (!taskName.equals(task.taskName)) return false;
        if (!description.equals(task.description)) return false;
        return status.equals(task.status);
    }

    @Override
    public int hashCode() {
        int result = taskName.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + identifier;
        return result;
    }
}

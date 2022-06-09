package taskManager;

import java.util.HashMap;
import java.util.Objects;

public class EpicTask extends Task {
    protected HashMap<String, SubTask> listOfRelatedSubTasks;

    EpicTask(String taskName, String description, int identifier) {
        super(taskName, description, identifier);
        this.listOfRelatedSubTasks = new HashMap<>();
    }

    public HashMap<String, SubTask> getListOfRelatedSubTasks() {
        return listOfRelatedSubTasks;
    }

    public void setListOfRelatedSubTasks(HashMap<String, SubTask> listOfRelatedSubTasks) {
        this.listOfRelatedSubTasks = listOfRelatedSubTasks;
    }

    @Override
    public String toString() {
        String epicString = "taskManager.EpicTask{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", identifier='" + identifier + '\'' +
                '}' + "\nПеречень подзадач: ";
        for (HashMap.Entry<String, SubTask> entry : listOfRelatedSubTasks.entrySet()) {
            epicString = epicString + "\n" + entry.getValue().toString();
        }
        return epicString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EpicTask epicTask = (EpicTask) o;

        return Objects.equals(listOfRelatedSubTasks, epicTask.listOfRelatedSubTasks);
    }

    @Override
    public int hashCode() {
        int result = taskName.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + identifier;
        result = 31 * result + listOfRelatedSubTasks.hashCode();
        return result;
    }
}

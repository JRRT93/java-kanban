package taskManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EpicTask extends Task {
    protected Map<String, SubTask> listOfRelatedSubTasks;

    EpicTask(String taskName, String description, int identifier) {
        super(taskName, description, identifier);
        this.listOfRelatedSubTasks = new HashMap<>();
    }

    public Map<String, SubTask> getListOfRelatedSubTasks() {
        return listOfRelatedSubTasks;
    }

    public void setListOfRelatedSubTasks(Map<String, SubTask> listOfRelatedSubTasks) {
        this.listOfRelatedSubTasks = listOfRelatedSubTasks;
    }

    @Override
    public String toString() {
        return  "EpicTask{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", identifier='" + identifier + '\'' +
                ", Количество подзадач='" + listOfRelatedSubTasks.size() + "'}";
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
        int result = super.hashCode();
        result = 31 * result + (listOfRelatedSubTasks != null ? listOfRelatedSubTasks.hashCode() : 0);
        return result;
    }
}

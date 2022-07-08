package input;

import java.util.HashMap;
import java.util.Map;

public class InputTaskEpic extends InputTask {
    HashMap<String, InputSubTask> listOfRelatedSubTasks;

    public InputTaskEpic(String taskName, String description) {
        super(taskName, description);
        this.listOfRelatedSubTasks = new HashMap<>();
    }

    public Map<String, InputSubTask> getListOfRelatedSubTasks() {
        return listOfRelatedSubTasks;
    }

    @Override
    public String toString() {
        String epicString = "taskManager.EpicTask{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}' + "\nПеречень подзадач: ";
        for (HashMap.Entry<String, InputSubTask> entry : listOfRelatedSubTasks.entrySet()) {
            epicString = epicString + "\n" + entry.getValue().toString();
        }
        return epicString;
    }
}

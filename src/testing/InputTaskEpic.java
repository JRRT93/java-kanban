package testing;

import java.util.HashMap;

public class InputTaskEpic extends InputTask {
    HashMap<String, InputSubTask> listOfSubTasks;

    InputTaskEpic(String taskName, String description) {
        super(taskName, description);
        this.listOfSubTasks = new HashMap<>();
    }

    public HashMap<String, InputSubTask> getListOfSubTasks() {
        return listOfSubTasks;
    }

    @Override
    public String toString() {
        String epicString = "taskManager.EpicTask{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}' + "\nПеречень подзадач: ";
        for (HashMap.Entry<String, InputSubTask> entry : listOfSubTasks.entrySet()) {
            epicString = epicString + "\n" + entry.getValue().toString();
        }
        return epicString;
    }
}

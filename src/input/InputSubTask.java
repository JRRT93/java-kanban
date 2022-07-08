package input;

public class InputSubTask extends InputTask {
    InputTaskEpic relatedInputEpicTask;

    public InputSubTask(String taskName, String description, InputTaskEpic relatedInputEpicTask) {
        super(taskName, description);
        this.relatedInputEpicTask = relatedInputEpicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "relatedEpicTask='" + relatedInputEpicTask.getTaskName() + '\'' +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", identifier=" + identifier +
                '}';
    }
}

package input;

import java.time.LocalDateTime;

public class InputSubTask extends InputTask {

    public InputSubTask(String taskName, String description, long minutes, LocalDateTime startTime) {
        super(taskName, description, minutes, startTime);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                //"relatedEpicTask='" + relatedInputEpicTask.getTaskName() + '\'' +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", identifier=" + identifier +
                '}';
    }
}

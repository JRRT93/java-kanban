package input;

import tasks.Task;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

public class InputTaskCreator {

    public InputTask createInputTask(String taskName, String description, int minutes, LocalDateTime startTime) {
        return new InputTask(taskName, description, minutes, startTime);
    }

    public InputTaskEpic createEpicTask(String taskName, String description) {
        return new InputTaskEpic(taskName, description);
    }
    public InputSubTask createInputSubTask(String taskName, String description, long minutes, LocalDateTime startTime) {
        return new InputSubTask(taskName, description, minutes, startTime);
    }

    public void putSubTaskInEpic(InputSubTask inputSubTask, InputTaskEpic inputTaskEpic) {
        inputTaskEpic.listOfRelatedSubTasks.put(inputSubTask.getTaskName(), inputSubTask);
    }

    public InputTask updateTask(Task backendTask) {
        Random random = new Random();
        InputTask inputTask = new InputTask(backendTask.getTaskName(), backendTask.getDescription());
        inputTask.setIdentifier(backendTask.getIdentifier());
        inputTask.setTaskName("UPDATED! " + backendTask.getTaskName());
        inputTask.setDescription("UPDATED! " + backendTask.getDescription());
        int randomUpdate = random.nextInt(2);
        if (randomUpdate == 0) {
            inputTask.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            inputTask.setStatus(TaskStatus.DONE);
        }
        return inputTask;
    }

    public InputTaskEpic updateEpicTask(EpicTask backendEpicTask) {
        InputTaskEpic inputTaskEpic = new InputTaskEpic(backendEpicTask.getTaskName(), backendEpicTask.getDescription());
        inputTaskEpic.setIdentifier(backendEpicTask.getIdentifier());
        inputTaskEpic.setTaskName("UPDATED! " + backendEpicTask.getTaskName());
        inputTaskEpic.setDescription("UPDATED! " + backendEpicTask.getDescription());
        for (Map.Entry<String, SubTask> entry : backendEpicTask.getListOfRelatedSubTasks().entrySet()) {
            inputTaskEpic.listOfRelatedSubTasks.put(entry.getKey(), updateSubTask(entry.getValue()));
        }
        return inputTaskEpic;
    }

    public InputSubTask updateSubTask(SubTask backendSubTask) {
        Random random = new Random();
        InputSubTask inputSubTask = new InputSubTask(backendSubTask.getTaskName(), backendSubTask.getDescription(),
                backendSubTask.getDuration().toMinutes(), backendSubTask.getStartTime());
        inputSubTask.setIdentifier(backendSubTask.getIdentifier());
        inputSubTask.setTaskName("UPDATED! " + backendSubTask.getTaskName());
        inputSubTask.setDescription("UPDATED! " + backendSubTask.getDescription());
        int randomUpdate = random.nextInt(2);
        if (randomUpdate == 0) {
            inputSubTask.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            inputSubTask.setStatus(TaskStatus.DONE);
        }
        return inputSubTask;
    }
}

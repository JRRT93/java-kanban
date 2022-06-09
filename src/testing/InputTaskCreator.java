package testing;

import taskManager.Task;
import taskManager.EpicTask;
import taskManager.SubTask;

import java.util.Map;
import java.util.Random;

public class InputTaskCreator {

    public InputTask createInputTask(String taskName, String description) {
        return new InputTask(taskName, description);
    }

    public InputTaskEpic createEpicTask(String taskName, String description) {
        return new InputTaskEpic(taskName, description);
    }
    public InputSubTask createInputSubTask(String taskName, String description, InputTaskEpic relatedInputTaskEpic) {
        return new InputSubTask(taskName, description, relatedInputTaskEpic);
    }

    public void putSubTaskInEpic(InputSubTask inputSubTask, InputTaskEpic inputTaskEpic) {
        inputTaskEpic.listOfSubTasks.put(inputSubTask.getTaskName(), inputSubTask);
    }

    public InputTask updateTask(Task backendTask) {
        Random random = new Random();
        InputTask inputTask = new InputTask(backendTask.getTaskName(), backendTask.getDescription());
        inputTask.setIdentifier(backendTask.getIdentifier());
        inputTask.setTaskName("UPDATED! " + backendTask.getTaskName());
        inputTask.setDescription("UPDATED! " + backendTask.getDescription());
        int randomUpdate = random.nextInt(2);
        if (randomUpdate == 0) {
            inputTask.setStatus("IN_PROGRESS");
        } else {
            inputTask.setStatus("DONE");
        }
        return inputTask;
    }

    public InputTaskEpic updateEpicTask(EpicTask backendEpicTask) {
        InputTaskEpic inputTaskEpic = new InputTaskEpic(backendEpicTask.getTaskName(), backendEpicTask.getDescription());
        inputTaskEpic.setIdentifier(backendEpicTask.getIdentifier());
        inputTaskEpic.setTaskName("UPDATED! " + backendEpicTask.getTaskName());
        inputTaskEpic.setDescription("UPDATED! " + backendEpicTask.getDescription());
        for (Map.Entry<String, SubTask> entry : backendEpicTask.getListOfRelatedSubTasks().entrySet()) {
            inputTaskEpic.listOfSubTasks.put(entry.getKey(), updateSubTask(entry.getValue(), inputTaskEpic));
        }
        return inputTaskEpic;
    }

    public InputSubTask updateSubTask(SubTask backendSubTask, InputTaskEpic relatedInputTaskEpic) {
        Random random = new Random();
        InputSubTask inputSubTask = new InputSubTask(backendSubTask.getTaskName(), backendSubTask.getDescription(),
                relatedInputTaskEpic);
        inputSubTask.setIdentifier(backendSubTask.getIdentifier());
        inputSubTask.setTaskName("UPDATED! " + backendSubTask.getTaskName());
        inputSubTask.setDescription("UPDATED! " + backendSubTask.getDescription());
        int randomUpdate = random.nextInt(2);
        if (randomUpdate == 0) {
            inputSubTask.setStatus("IN_PROGRESS");
        } else {
            inputSubTask.setStatus("DONE");
        }
        return inputSubTask;
    }
}

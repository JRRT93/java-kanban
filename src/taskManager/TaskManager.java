package taskManager;

import testing.InputSubTask;
import testing.InputTask;
import testing.InputTaskEpic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private HashMap<Integer, Task> listOfTasks;
    private HashMap<Integer, EpicTask> listOfEpics;
    private HashMap<Integer, SubTask> listOfSubTasks;
    private int identifier;

    TaskManager() {
        this.listOfTasks = new HashMap<>();
        this.listOfEpics = new HashMap<>();
        this.listOfSubTasks = new HashMap<>();
    }

    public HashMap<Integer, Task> getListOfTasks() {
        return listOfTasks;
    }

    public HashMap<Integer, EpicTask> getListOfEpics() {
        return listOfEpics;
    }

    public HashMap<Integer, SubTask> getListOfSubTasks() {
        return listOfSubTasks;
    }

    public int getIdentifier() {
        return identifier;
    }

    public Task createTask(InputTask frontendInputTask) {
        Task task = new Task(frontendInputTask.getTaskName(), frontendInputTask.getDescription(), identifier);
        listOfTasks.put(task.identifier, task);
        identifier++;
        return task;
    }

    public EpicTask createEpicTask(InputTaskEpic frontendTaskEpic) {
        EpicTask epicTask = new EpicTask(frontendTaskEpic.getTaskName(), frontendTaskEpic.getDescription(), identifier);
        listOfEpics.put(identifier, epicTask);
        identifier++;
        for (Map.Entry<String, InputSubTask> entry : frontendTaskEpic.getListOfSubTasks().entrySet()) {
            epicTask.listOfRelatedSubTasks.put(entry.getKey(), createSubTask(entry.getValue(), epicTask));
        }
        return epicTask;
    }

    public SubTask createSubTask(InputSubTask frontendInputSubTask, EpicTask parentEpicTask) {
        SubTask subTask = new SubTask(frontendInputSubTask.getTaskName(), frontendInputSubTask.getDescription(), identifier,
                parentEpicTask);
        listOfSubTasks.put(identifier, subTask);
        identifier++;
        return subTask;
    }

    public Task getTaskByID(int identifier) {
        return listOfTasks.get(identifier);
    }

    public EpicTask getEpicTaskByID(int identifier) {
        return listOfEpics.get(identifier);
    }

    public SubTask getSubTaskByID(int identifier) {
        return listOfSubTasks.get(identifier);
    }

    public HashMap<String, SubTask> getListOfEpicsSubTasks(EpicTask epicTask) {
        return epicTask.getListOfRelatedSubTasks();
    }

    public Task updateTask(InputTask updatedInputTask) {
        Task updatedTask = listOfTasks.get(updatedInputTask.getIdentifier());
        updatedTask.setTaskName(updatedInputTask.getTaskName());
        updatedTask.setDescription(updatedInputTask.getDescription());
        updatedTask.setStatus(updatedInputTask.getStatus());
        return updatedTask;
    }

    public EpicTask updateEpicTask(InputTaskEpic updatedInputTaskEpic) {
        EpicTask updatedEpicTask = listOfEpics.get(updatedInputTaskEpic.getIdentifier());
        updatedEpicTask.setTaskName(updatedInputTaskEpic.getTaskName());
        updatedEpicTask.setDescription(updatedInputTaskEpic.getDescription());
        for (Map.Entry<String, InputSubTask> entry : updatedInputTaskEpic.getListOfSubTasks().entrySet()) {
            HashMap<String, SubTask> epicTaskSetList = updatedEpicTask.getListOfRelatedSubTasks();
            epicTaskSetList.clear();
            epicTaskSetList.put(entry.getValue().getTaskName(), updateSubTask(entry.getValue()));
            updatedEpicTask.setListOfRelatedSubTasks(epicTaskSetList);
        }
        ArrayList<String> currentSubTasksStatus = new ArrayList<>();
        for (Map.Entry<String, SubTask> entry : updatedEpicTask.getListOfRelatedSubTasks().entrySet()) {
            currentSubTasksStatus.add(entry.getValue().getStatus());
        }
        int sarcasticNumber = 0;
        for (int i = 0; i < currentSubTasksStatus.size(); i++) {
            if (currentSubTasksStatus.get(i).equals("IN_PROGRESS")) {
                sarcasticNumber++;
            } else if (currentSubTasksStatus.get(i).equals("DONE")) {
                sarcasticNumber += 2;
            }
        }
        if (sarcasticNumber == 0) {
            updatedEpicTask.setStatus("NEW");
        } else if (sarcasticNumber == currentSubTasksStatus.size() * 2) {
            updatedEpicTask.setStatus("DONE");
        } else {
            updatedEpicTask.setStatus("IN_PROGRESS");
        }
        return updatedEpicTask;
    }

    public SubTask updateSubTask(InputSubTask updatedInputSubTask) {
        SubTask updatedSubTask = listOfSubTasks.get(updatedInputSubTask.getIdentifier());
        updatedSubTask.setTaskName(updatedInputSubTask.getTaskName());
        updatedSubTask.setDescription(updatedInputSubTask.getDescription());
        updatedSubTask.setStatus(updatedInputSubTask.getStatus());
        return updatedSubTask;
    }

    public void removeAllTask() {
        listOfTasks.clear();
    }

    public void removeAllEpicTask() {
        listOfEpics.clear();
        listOfSubTasks.clear();
    }

    public void removeAllSubTask() {
        listOfEpics.clear();
        listOfSubTasks.clear();
    }

    public void removeTaskByID(int identifier) {
        listOfTasks.remove(identifier);
    }

    public void removeEpicTaskByID(int identifier) {
        EpicTask epicTask = listOfEpics.get(identifier);
        for (Map.Entry<String, SubTask> entry : epicTask.getListOfRelatedSubTasks().entrySet()) {
            SubTask subTask = entry.getValue();
            listOfSubTasks.remove(subTask.getIdentifier());
        }
        HashMap<String, SubTask> epicTaskSetList = epicTask.getListOfRelatedSubTasks();
        epicTaskSetList.clear();
        epicTask.setListOfRelatedSubTasks(epicTaskSetList);
        listOfEpics.remove(identifier);
    }

    public void removeSubTaskByID(int identifier) {
        SubTask subTask = listOfSubTasks.get(identifier);
        EpicTask epicTask = subTask.getRelatedEpicTask();
        HashMap<String, SubTask> subTasksSetList = epicTask.getListOfRelatedSubTasks();
        subTasksSetList.remove(subTask.getTaskName());
        epicTask.setListOfRelatedSubTasks(subTasksSetList);
        if (epicTask.getListOfRelatedSubTasks().size() == 0) {
            listOfEpics.remove(epicTask.getIdentifier());
        }
        listOfSubTasks.remove(identifier);
    }
}

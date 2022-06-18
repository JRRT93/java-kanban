package taskManager;

import testing.InputSubTask;
import testing.InputTask;
import testing.InputTaskEpic;
import utilityClasses.HistoryManager;
import utilityClasses.TaskManager;
import utilityClasses.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> listOfTasks;
    private final Map<Integer, EpicTask> listOfEpics;
    private final Map<Integer, SubTask> listOfSubTasks;
    private int identifier;

    public InMemoryTaskManager() {
        this.listOfTasks = new HashMap<>();
        this.listOfEpics = new HashMap<>();
        this.listOfSubTasks = new HashMap<>();
    }

    @Override
    public Map<Integer, Task> getListOfTasks() {
        return listOfTasks;
    }

    @Override
    public Map<Integer, EpicTask> getListOfEpics() {
        return listOfEpics;
    }

    @Override
    public Map<Integer, SubTask> getListOfSubTasks() {
        return listOfSubTasks;
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }

    @Override
    public Task createTask(InputTask frontendInputTask) {
        Task task = new Task(frontendInputTask.getTaskName(), frontendInputTask.getDescription(), identifier);
        listOfTasks.put(task.identifier, task);
        identifier++;
        return task;
    }

    @Override
    public EpicTask createEpicTask(InputTaskEpic frontendTaskEpic) {
        EpicTask epicTask = new EpicTask(frontendTaskEpic.getTaskName(), frontendTaskEpic.getDescription(), identifier);
        listOfEpics.put(identifier, epicTask);
        identifier++;
        for (Map.Entry<String, InputSubTask> entry : frontendTaskEpic.getListOfRelatedSubTasks().entrySet()) {
            epicTask.listOfRelatedSubTasks.put(entry.getKey(), createSubTask(entry.getValue(), epicTask));
        }
        return epicTask;
    }

    @Override
    public SubTask createSubTask(InputSubTask frontendInputSubTask, EpicTask relatedEpicTask) {
        SubTask subTask = new SubTask(frontendInputSubTask.getTaskName(), frontendInputSubTask.getDescription(),
                identifier, relatedEpicTask);
        listOfSubTasks.put(identifier, subTask);
        identifier++;
        return subTask;
    }

    @Override
    public Task getTaskByID(int identifier, HistoryManager historyManager) {
        historyManager.add(listOfTasks.get(identifier));
        return listOfTasks.get(identifier);
    }

    @Override
    public EpicTask getEpicTaskByID(int identifier, HistoryManager historyManager) {
        historyManager.add(listOfEpics.get(identifier));
        return listOfEpics.get(identifier);
    }

    @Override
    public SubTask getSubTaskByID(int identifier, HistoryManager historyManager) {
        historyManager.add(listOfSubTasks.get(identifier));
        return listOfSubTasks.get(identifier);
    }

    @Override
    public Map<String, SubTask> getListOfEpicsSubTasks(EpicTask epicTask) {
        return epicTask.getListOfRelatedSubTasks();
    }

    @Override
    public Task updateTask(InputTask updatedInputTask) {
        Task updatedTask = listOfTasks.get(updatedInputTask.getIdentifier());
        updatedTask.setTaskName(updatedInputTask.getTaskName());
        updatedTask.setDescription(updatedInputTask.getDescription());
        updatedTask.setStatus(updatedInputTask.getStatus());
        return updatedTask;
    }

    @Override
    public EpicTask updateEpicTask(InputTaskEpic updatedInputTaskEpic) {
        EpicTask updatedEpicTask = listOfEpics.get(updatedInputTaskEpic.getIdentifier());
        updatedEpicTask.setTaskName(updatedInputTaskEpic.getTaskName());
        updatedEpicTask.setDescription(updatedInputTaskEpic.getDescription());
        Map<String, SubTask> epicTaskSetList = updatedEpicTask.getListOfRelatedSubTasks();
        epicTaskSetList.clear();
        for (Map.Entry<String, InputSubTask> entry : updatedInputTaskEpic.getListOfRelatedSubTasks().entrySet()) {
            epicTaskSetList.put(entry.getValue().getTaskName(), updateSubTask(entry.getValue()));
        }
        updatedEpicTask.setListOfRelatedSubTasks(epicTaskSetList);
        updatedEpicTask.setStatus(defineEpicTaskStatus(updatedEpicTask));
        return updatedEpicTask;
    }

    @Override
    public SubTask updateSubTask(InputSubTask updatedInputSubTask) {
        SubTask updatedSubTask = listOfSubTasks.get(updatedInputSubTask.getIdentifier());
        updatedSubTask.setTaskName(updatedInputSubTask.getTaskName());
        updatedSubTask.setDescription(updatedInputSubTask.getDescription());
        updatedSubTask.setStatus(updatedInputSubTask.getStatus());
        return updatedSubTask;
    }

    private TaskStatus defineEpicTaskStatus(EpicTask epicTask) {
        List<TaskStatus> subTasksStatus = new ArrayList<>();
        for (Map.Entry<String, SubTask> entry : epicTask.getListOfRelatedSubTasks().entrySet()) {
            subTasksStatus.add(entry.getValue().getStatus());
        }
        if (!subTasksStatus.contains(TaskStatus.DONE) && !subTasksStatus.contains(TaskStatus.IN_PROGRESS)) {
            return TaskStatus.NEW;
        } else if (!subTasksStatus.contains(TaskStatus.NEW) && !subTasksStatus.contains(TaskStatus.IN_PROGRESS)) {
            return TaskStatus.DONE;
        }
        return TaskStatus.IN_PROGRESS;
    }

    @Override
    public void removeAllTask() {
        listOfTasks.clear();
    }

    @Override
    public void removeAllEpicTask() {
        listOfEpics.clear();
        listOfSubTasks.clear();
    }

    @Override
    public void removeAllSubTask() {
        listOfEpics.clear();
        listOfSubTasks.clear();
    }

    @Override
    public void removeTaskByID(int identifier) {
        listOfTasks.remove(identifier);
    }

    @Override
    public void removeEpicTaskByID(int identifier) {
        EpicTask epicTask = listOfEpics.get(identifier);
        for (Map.Entry<String, SubTask> entry : epicTask.getListOfRelatedSubTasks().entrySet()) {
            SubTask subTask = entry.getValue();
            listOfSubTasks.remove(subTask.getIdentifier());
        }
        Map<String, SubTask> epicTaskSetList = epicTask.getListOfRelatedSubTasks();
        epicTaskSetList.clear();
        epicTask.setListOfRelatedSubTasks(epicTaskSetList);
        listOfEpics.remove(identifier);
    }

    @Override
    public void removeSubTaskByID(int identifier) {
        SubTask subTask = listOfSubTasks.get(identifier);
        EpicTask epicTask = subTask.getRelatedEpicTask();
        if (epicTask.listOfRelatedSubTasks.size() == 1) {
            epicTask.listOfRelatedSubTasks.clear();
            listOfSubTasks.remove(identifier);
            listOfEpics.remove(epicTask.getIdentifier());
        } else {
            epicTask.listOfRelatedSubTasks.remove(subTask.getTaskName());
            listOfSubTasks.remove(identifier);
        }
    }
}

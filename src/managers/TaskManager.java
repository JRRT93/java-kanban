package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import input.InputSubTask;
import input.InputTask;
import input.InputTaskEpic;

import java.util.Map;
import java.util.Set;

public interface TaskManager {
    Map<Integer, Task> getListOfTasks();
    Map<Integer, EpicTask> getListOfEpics();
    Map<Integer, SubTask> getListOfSubTasks();
    int getIdentifier();
    Task createTask(InputTask frontendInputTask);
    EpicTask createEpicTask(InputTaskEpic frontendTaskEpic);
    SubTask createSubTask(InputSubTask frontendInputSubTask, EpicTask relatedEpicTask);
    Task getTaskByID(int identifier);
    EpicTask getEpicTaskByID(int identifier);
    SubTask getSubTaskByID(int identifier);
    Task updateTask(InputTask updatedInputTask);
    EpicTask updateEpicTask(InputTaskEpic updatedInputTaskEpic);
    SubTask updateSubTask(InputSubTask updatedInputSubTask);
    void removeAllTask();
    void removeAllEpicTask();
    void removeAllSubTask();
    void removeTaskByID(int identifier);
    void removeEpicTaskByID(int identifier);
    void removeSubTaskByID(int identifier);
    Set<Task> getPrioritizedTasks();
    HistoryManager getHistoryManager();
}

package utilityClasses;

import taskManager.EpicTask;
import taskManager.SubTask;
import taskManager.Task;
import testing.InputSubTask;
import testing.InputTask;
import testing.InputTaskEpic;

import java.util.Map;

public interface TaskManager {
    Map<Integer, Task> getListOfTasks();
    Map<Integer, EpicTask> getListOfEpics();
    Map<Integer, SubTask> getListOfSubTasks();
    int getIdentifier();
    Task createTask(InputTask frontendInputTask);
    EpicTask createEpicTask(InputTaskEpic frontendTaskEpic);
    SubTask createSubTask(InputSubTask frontendInputSubTask, EpicTask relatedEpicTask);
    Task getTaskByID(int identifier, HistoryManager historyManager);
    EpicTask getEpicTaskByID(int identifier, HistoryManager historyManager);
    SubTask getSubTaskByID(int identifier, HistoryManager historyManager);
    Map<String, SubTask> getListOfEpicsSubTasks(EpicTask epicTask);
    Task updateTask(InputTask updatedInputTask);
    EpicTask updateEpicTask(InputTaskEpic updatedInputTaskEpic);
    SubTask updateSubTask(InputSubTask updatedInputSubTask);
    void removeAllTask();
    void removeAllEpicTask();
    void removeAllSubTask();
    void removeTaskByID(int identifier);
    void removeEpicTaskByID(int identifier);
    void removeSubTaskByID(int identifier);
}

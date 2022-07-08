package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import input.InputSubTask;
import input.InputTask;
import input.InputTaskEpic;

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
    void removeAllTask(HistoryManager historyManager);
    void removeAllEpicTask(HistoryManager historyManager);
    void removeAllSubTask(HistoryManager historyManager);
    void removeTaskByID(int identifier, HistoryManager historyManager);
    void removeEpicTaskByID(int identifier, HistoryManager historyManager);
    void removeSubTaskByID(int identifier, HistoryManager historyManager);
}

package managers;

import input.InputSubTask;
import input.InputTask;
import input.InputTaskEpic;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import util.Managers;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> listOfTasks;
    protected final Map<Integer, EpicTask> listOfEpics;
    protected final Map<Integer, SubTask> listOfSubTasks;
    protected final Set<Task> prioritizedTasks;
    protected final HistoryManager historyManager;
    protected int identifier;

    public InMemoryTaskManager() {
        this.listOfTasks = new HashMap<>();
        this.listOfEpics = new HashMap<>();
        this.listOfSubTasks = new HashMap<>();
        this.prioritizedTasks = initialTree();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return this.historyManager;
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
    public Set<Task> getPrioritizedTasks() {
        prioritizedTasks.addAll(listOfTasks.values());
        prioritizedTasks.addAll(listOfSubTasks.values());
        return prioritizedTasks;
    }

    private void validateTask(Task task) {
        for (int i = 1; i < getPrioritizedTasks().size(); i++) {
            LocalDateTime prevFinishTime = getPrioritizedTasks().toArray(new Task[0])[i - 1].getEndTime();
            LocalDateTime thisStartTime = getPrioritizedTasks().toArray(new Task[0])[i].getStartTime();
            if (thisStartTime.isBefore(prevFinishTime)) throw new IllegalArgumentException("Выявлено пересечение " +
                    "дат выполнения задачи с уже существующими задачами");
        }
    }

    @Override
    public Task createTask(InputTask frontendInputTask) {
        Task task = new Task(frontendInputTask.getTaskName(), frontendInputTask.getDescription(), identifier,
                frontendInputTask.getDuration().toMinutes(), frontendInputTask.getStartTime());
        listOfTasks.put(task.getIdentifier(), task);
        validateTask(task);
        identifier++;
        return task;
    }

    @Override
    public EpicTask createEpicTask(InputTaskEpic frontendTaskEpic) {
        EpicTask epicTask = new EpicTask(frontendTaskEpic.getTaskName(), frontendTaskEpic.getDescription(), identifier);
        listOfEpics.put(identifier, epicTask);
        identifier++;
        for (Map.Entry<String, InputSubTask> entry : frontendTaskEpic.getListOfRelatedSubTasks().entrySet()) {
            epicTask.addRelatedSubTasks(createSubTask(entry.getValue(), epicTask));
        }
        return epicTask;
    }

    @Override
    public SubTask createSubTask(InputSubTask frontendInputSubTask, EpicTask relatedEpicTask) {
        SubTask subTask = new SubTask(frontendInputSubTask.getTaskName(), frontendInputSubTask.getDescription(),
                identifier, relatedEpicTask, frontendInputSubTask.getDuration().toMinutes(),
                frontendInputSubTask.getStartTime());
        listOfSubTasks.put(identifier, subTask);
        validateTask(subTask);
        identifier++;
        return subTask;
    }

    @Override
    public Task getTaskByID(int identifier) {
        historyManager.add(listOfTasks.get(identifier));
        return listOfTasks.get(identifier);
    }

    @Override
    public EpicTask getEpicTaskByID(int identifier) {
        historyManager.add(listOfEpics.get(identifier));
        for (Map.Entry<String, SubTask> entry :
                listOfEpics.get(identifier).getListOfRelatedSubTasks().entrySet()) {
            getSubTaskByID(entry.getValue().getIdentifier());
        }
        return listOfEpics.get(identifier);
    }

    @Override
    public SubTask getSubTaskByID(int identifier) {
        historyManager.add(listOfSubTasks.get(identifier));
        return listOfSubTasks.get(identifier);
    }

    @Override
    public Task updateTask(InputTask updatedInputTask) {
        Task updatedTask = listOfTasks.get(updatedInputTask.getIdentifier());
        updatedTask.setTaskName(updatedInputTask.getTaskName());
        updatedTask.setDescription(updatedInputTask.getDescription());
        updatedTask.setStatus(updatedInputTask.getStatus());
        validateTask(updatedTask);
        return updatedTask;
    }

    @Override
    public EpicTask updateEpicTask(InputTaskEpic updatedInputTaskEpic) {
        EpicTask updatedEpicTask = listOfEpics.get(updatedInputTaskEpic.getIdentifier());
        updatedEpicTask.setTaskName(updatedInputTaskEpic.getTaskName());
        updatedEpicTask.setDescription(updatedInputTaskEpic.getDescription());

        for (Map.Entry<String, InputSubTask> entry : updatedInputTaskEpic.getListOfRelatedSubTasks().entrySet()) {
            updateSubTask(entry.getValue());
        }
        return updatedEpicTask;
    }

    @Override
    public SubTask updateSubTask(InputSubTask updatedInputSubTask) {
        SubTask updatedSubTask = listOfSubTasks.get(updatedInputSubTask.getIdentifier());
        String keyForSubTask = updatedSubTask.getTaskName();
        EpicTask relatedEpicTask = updatedSubTask.getRelatedEpicTask();

        updatedSubTask.setTaskName(updatedInputSubTask.getTaskName());
        updatedSubTask.setDescription(updatedInputSubTask.getDescription());
        updatedSubTask.setStatus(updatedInputSubTask.getStatus());

        Map<String, SubTask> listOfRelatedSubTask = relatedEpicTask.getListOfRelatedSubTasks();
        listOfRelatedSubTask.remove(keyForSubTask);
        listOfRelatedSubTask.put(updatedSubTask.getTaskName(), updatedSubTask);
        relatedEpicTask.setListOfRelatedSubTasks(listOfRelatedSubTask);
        validateTask(updatedSubTask);
        return updatedSubTask;
    }

    @Override
    public void removeAllTask() {
        if (!listOfTasks.isEmpty())
            for (Map.Entry<Integer, Task> entry : listOfTasks.entrySet()) {
                historyManager.remove(entry.getKey());
            }
        listOfTasks.clear();
    }

    @Override
    public void removeAllEpicTask() {
        if (!listOfEpics.isEmpty()) {
            List<Integer> destroy = new ArrayList<>();
            for (Integer key: listOfEpics.keySet()) {
                destroy.add(key);
            }
            for (int i = 0; i < destroy.size(); i++) {
                removeEpicTaskByID(destroy.get(i));
            }
        }
    }

    @Override
    public void removeAllSubTask() {
        if (!listOfSubTasks.isEmpty()) {
            List<Integer> destroy = new ArrayList<>();
            for (Integer key: listOfSubTasks.keySet()) {
                destroy.add(key);
            }
            for (int i = 0; i < destroy.size(); i++) {
                removeSubTaskByID(destroy.get(i));
            }
        }
    }

    @Override
    public void removeTaskByID(int identifier) {
        historyManager.remove(identifier);
        listOfTasks.remove(identifier);
    }

    @Override
    public void removeEpicTaskByID(int identifier) {
        historyManager.remove(identifier);
        EpicTask epicTask = listOfEpics.get(identifier);
        for (Map.Entry<String, SubTask> entry : epicTask.getListOfRelatedSubTasks().entrySet()) {
            SubTask subTask = entry.getValue();
            historyManager.remove(subTask.getIdentifier());
            listOfSubTasks.remove(subTask.getIdentifier());
        }
        Map<String, SubTask> epicTaskSetList = epicTask.getListOfRelatedSubTasks();
        epicTaskSetList.clear();
        epicTask.setListOfRelatedSubTasks(epicTaskSetList);
        listOfEpics.remove(identifier);
    }

    @Override
    public void removeSubTaskByID(int identifier) {
        historyManager.remove(identifier);
        SubTask subTask = listOfSubTasks.get(identifier);
        EpicTask epicTask = subTask.getRelatedEpicTask();
        if (epicTask.getListOfRelatedSubTasks().size() == 1) {
            epicTask.getListOfRelatedSubTasks().clear();
            listOfSubTasks.remove(identifier);
            historyManager.remove(epicTask.getIdentifier());
            listOfEpics.remove(epicTask.getIdentifier());
        } else {
            epicTask.getListOfRelatedSubTasks().remove(subTask.getTaskName());
            listOfSubTasks.remove(identifier);
        }
    }

    private Set<Task> initialTree() {
        Comparator<Task> comparator = (o1, o2) -> {
            if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            }
            return 0;
        };
        return new TreeSet<>(comparator);
    }
}
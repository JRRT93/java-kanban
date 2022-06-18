package taskManager;

import utilityClasses.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskRequestHistory;

    public InMemoryHistoryManager() {
        this.taskRequestHistory = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        taskRequestHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> lastTenTaskRequest = new ArrayList<>();
        int wholeHistorySize = taskRequestHistory.size();
        if (wholeHistorySize <= 10) {
            lastTenTaskRequest = taskRequestHistory;
            return lastTenTaskRequest;
        }
        for (int i = 0; i < 10; i++) {
            lastTenTaskRequest.add(taskRequestHistory.get(wholeHistorySize - 10 + i));
        }
        return lastTenTaskRequest;
    }
}
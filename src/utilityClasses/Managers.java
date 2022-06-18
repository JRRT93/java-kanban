package utilityClasses;

import taskManager.InMemoryHistoryManager;
import taskManager.InMemoryTaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

package util;

import managers.*;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedManagerByPath(String path) {
        return new FileBackedTasksManager(path);
    }
}

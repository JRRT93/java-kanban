package util;

import managers.*;

import java.net.URL;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileBackedManagerByPath(String path) {
        return new FileBackedTasksManager(path);
    }
    public static HTTPTaskManager getHTTPTaskManagerByPath(URL url) {
        return new HTTPTaskManager(url);
    }
}

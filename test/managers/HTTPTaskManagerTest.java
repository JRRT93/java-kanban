package managers;

import api.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;
import util.TaskCreatorForTests;

import java.io.IOException;
import java.net.URL;


import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskManagerTest {
    HTTPTaskManager taskManager;
    KVServer kvServer;

    @BeforeEach
    void createManager() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = Managers.getHTTPTaskManagerByPath(new URL("http://localhost:8078/"));
        TaskCreatorForTests.createTasks(taskManager);
    }

    @AfterEach
    void stopKV() {
        kvServer.stopServer();
    }

    @Test
    void saveAndLoad() {
        taskManager.getEpicTaskByID(5);
        taskManager.getTaskByID(0);
        int taskCounty = taskManager.getListOfTasks().size();
        int epicCounty = taskManager.getListOfEpics().size();
        int subCounty = taskManager.getListOfSubTasks().size();
        int historyCounty = taskManager.getHistoryManager().getHistory().size();

        taskManager.saveFile("TestSave");
        taskManager.removeAllTask();
        taskManager.removeAllEpicTask();
        assertEquals(0, taskManager.getListOfTasks().size());
        assertEquals(0, taskManager.getListOfEpics().size());
        assertEquals(0, taskManager.getListOfSubTasks().size());
        assertEquals(0, taskManager.getHistoryManager().getHistory().size());

        taskManager.loadFromFile("TestSave");
        assertEquals(taskCounty, taskManager.getListOfTasks().size());
        assertEquals(epicCounty, taskManager.getListOfEpics().size());
        assertEquals(subCounty, taskManager.getListOfSubTasks().size());
        assertEquals(historyCounty, taskManager.getHistoryManager().getHistory().size());
    }
}
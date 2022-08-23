package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    private SubTask subTask;

    @BeforeEach
    public void beforeEach() {
        EpicTask epicTask = new EpicTask("Epic", "EpicDescription", 1);
        subTask = new SubTask("SubTask", "SubTaskDescription", 2, epicTask,
                60, LocalDateTime.of(2022, Month.OCTOBER, 9, 10, 0));
    }

    @Test
    void getRelatedEpicTask() {
        EpicTask epicTask = new EpicTask("Epic", "EpicDescription", 1);
        EpicTask gottenEpicTask = subTask.getRelatedEpicTask();
        assertEquals(epicTask, gottenEpicTask);
    }

    @Test
    void setRelatedEpicTask() {
        EpicTask epicTaskToSet = new EpicTask("AnotherEpic", "AnotherEpicDescription", 3);
        subTask.setRelatedEpicTask(epicTaskToSet);
        EpicTask gottenEpicTask = subTask.getRelatedEpicTask();
        assertEquals(epicTaskToSet, gottenEpicTask);
    }
}
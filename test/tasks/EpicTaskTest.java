package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest {
    private EpicTask epicTask;

    @BeforeEach
    void createEpicTask() {
        epicTask = new EpicTask("Epic", "EpicDescription", 1);
        Map<String, SubTask> listOfRelated = new HashMap<>();
        listOfRelated.put("SubTask", new SubTask("SubTask", "SubTaskDescription", 2, epicTask,
                60, LocalDateTime.of(2022, Month.OCTOBER, 9, 10, 0)));
        epicTask.setListOfRelatedSubTasks(listOfRelated);
    }

    /**
     * Подготовка: создается новая эпик задача с одним сабтаском, потом добавляется второй сабстаск
     * Исполнение: вызываются getEndTime(), getStartTime(), getDuration()
     * Проверка:
     * 1) с одной сабтаской у эпика начало 2022.10.09-10:00, конец 2022.09.10-11:00, продолжительность 60
     * 2) с двумя у эпика начало 2022.09.10-10:00, конец 2022.09.10-13:40, продолжительность 220
     */
    @Test
    void getEndTime() {
        SubTask subTask2 = new SubTask("SubTask2", "SubTaskDescription2", 3, epicTask,
                100, LocalDateTime.of(2022, Month.OCTOBER, 9, 12, 0));

        LocalDateTime expectedStartTime = LocalDateTime.of(2022, 10, 9, 10, 0);
        LocalDateTime expectedEndTime = LocalDateTime.of(2022, 10, 9, 11, 0);
        Duration expectedDuration = Duration.ofMinutes(60);
        LocalDateTime startTime = epicTask.getStartTime();
        LocalDateTime endTime = epicTask.getEndTime();
        Duration duration = epicTask.getDuration();

        assertEquals(expectedStartTime, startTime);
        assertEquals(expectedEndTime, endTime);
        assertEquals(expectedDuration, duration);

        epicTask.addRelatedSubTasks(subTask2);

        expectedEndTime = LocalDateTime.of(2022, 10, 9, 13, 40);
        expectedDuration = Duration.ofMinutes(220);
        startTime = epicTask.getStartTime();
        endTime = epicTask.getEndTime();
        duration = epicTask.getDuration();

        assertEquals(expectedStartTime, startTime);
        assertEquals(expectedEndTime, endTime);
        assertEquals(expectedDuration, duration);
    }

    @Test
    void getListOfRelatedSubTasks() {
        Map<String, SubTask> listOfRelated = new HashMap<>();
        listOfRelated.put("SubTask", new SubTask("SubTask", "SubTaskDescription", 2, epicTask,
                60, LocalDateTime.of(2022, Month.OCTOBER, 9, 10, 0)));

        Map<String, SubTask> gottenListOfRelated = epicTask.getListOfRelatedSubTasks();
        assertEquals(listOfRelated, gottenListOfRelated, "Возвращаемый список подзадач не совпадает с " +
                "установленным при создании");

        listOfRelated.clear();
        listOfRelated.put("AnotherSubTask", new SubTask("AnotherSubTask", "AnotherSubTaskDescription",
                3, epicTask, 60, LocalDateTime.of(2022, Month.OCTOBER, 9, 10, 0)));
        epicTask.setListOfRelatedSubTasks(listOfRelated);

        gottenListOfRelated = epicTask.getListOfRelatedSubTasks();

        assertEquals(listOfRelated, gottenListOfRelated, "Возвращаемый список подзадач не совпадает с " +
                "новым списком, который установили методом setListOfRelatedSubTasks(Map<String, SubTask> listOfRelated)");

        SubTask subTaskToAdd = new SubTask("SubTaskToAdd", "AddedSubTaskDescription", 4,
                epicTask, 60, LocalDateTime.of(2022, Month.OCTOBER, 9, 10, 0));

        listOfRelated.put("SubTaskToAdd", subTaskToAdd);
        epicTask.addRelatedSubTasks(subTaskToAdd);
        gottenListOfRelated = epicTask.getListOfRelatedSubTasks();

        assertEquals(listOfRelated, gottenListOfRelated, "Возвращаемый список подзадач после добавления новой " +
                "сабтаски не совпадает с списком, установленным при создании и добавленным в него новой сабтаской");
    }

}
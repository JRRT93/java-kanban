package managers;

import input.InputSubTask;
import input.InputTask;
import input.InputTaskEpic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import util.Managers;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

    /**
     * Подготовка: создание задач, файл "Для проверки загрузки" уже размещен в нужной директории, в нем другие задачи
     * Исполнение: чтение файла "Для проверки загрузки"
     * Проверка:
     * 1) Размер перечня задач - 1, эпиков - 1, подзадач - 1
     * 2) История просмотра: {1,2,0}
     * 3) Эквивалентность созданных задач прогруженным
     */
    @Test
    void shouldCreateTasksAndHistoryFromPreFile() {
        Task task = new Task("Задача", "Описание задачи", 0, 30,
                LocalDateTime.of(2000, Month.JANUARY, 1, 6,0));
        task.setStatus(TaskStatus.IN_PROGRESS);
        EpicTask epicTask = new EpicTask("Эпик", "Описание эпика", 1);
        SubTask subTask = new SubTask("Сабтаск", "Описание сабтаска", 2, epicTask,
                2, LocalDateTime.of(2020, Month.DECEMBER, 31, 23, 59));
        subTask.setStatus(TaskStatus.DONE);
        epicTask.addRelatedSubTasks(subTask);
        List<Integer> expectedHistory = List.of(1, 2, 0);
        taskManager.removeAllTask();
        taskManager.removeAllEpicTask();

        taskManager.loadFromFile("Для проверки загрузки");

        assertEquals(1, taskManager.getListOfTasks().size());
        assertEquals(1, taskManager.getListOfEpics().size());
        assertEquals(1, taskManager.getListOfSubTasks().size());
        assertEquals(expectedHistory, InMemoryHistoryManagerTest.historyToArray(taskManager.getHistoryManager().getHistory()));
        assertEquals(task, taskManager.getListOfTasks().get(0));
        assertEquals(epicTask, taskManager.getListOfEpics().get(1));
        assertEquals(subTask, taskManager.getListOfSubTasks().get(2));
    }

    /**
     * Подготовка: создание задач
     * Исполнение: сохранение файла "Только задачи", чтение файла "Только задачи"
     * Проверка:
     * 1) Размер перечня задач - 3, эпиков - 2, подзадач - 3
     * 2) История просмотра: {}
     *
     * Подготовка: наполнение истории - вызываем задачу, эпик с 2 подзадачами, удаляем одну из подзадач
     * Исполнение: сохранение файла "Нормальное сохранение файла", чтение файла "Нормальное сохранение файла"
     * Проверка:
     * 1) Размер перечня задач - 3, эпиков - 2, подзадач - 3
     * 2) История просмотра: {1, 5, 7}
     */
    @Test
     void saveAndLoadNormalCaseAndOnlyTasksNoHistory() {
        List<Integer> expectedHistory = new ArrayList<>();
        taskManager.saveFile("Только задачи");
        taskManager.loadFromFile("Только задачи");

        assertEquals(3, taskManager.getListOfTasks().size());
        assertEquals(2, taskManager.getListOfEpics().size());
        assertEquals(3, taskManager.getListOfSubTasks().size());
        assertEquals(expectedHistory, InMemoryHistoryManagerTest.historyToArray(taskManager.getHistoryManager().getHistory()));

        taskManager.getTaskByID(1);
        taskManager.getEpicTaskByID(5);
        taskManager.removeSubTaskByID(6);
        expectedHistory = List.of(1, 5, 7);

        taskManager.saveFile("Нормальное сохранение файла");
        taskManager.loadFromFile("Нормальное сохранение файла");

        assertEquals(3, taskManager.getListOfTasks().size());
        assertEquals(2, taskManager.getListOfEpics().size());
        assertEquals(2, taskManager.getListOfSubTasks().size());
        assertEquals(expectedHistory, InMemoryHistoryManagerTest.historyToArray(taskManager.getHistoryManager().getHistory()));
    }

    /**
     * Подготовка: очистка списков задач
     * Исполнение: сохранение файла "Чистый файл", чтение файла "Чистый файл"
     * Проверка:
     * 1) Размер перечня задач - 0, эпиков - 0, подзадач - 0
     * 2) История {}
     */
    @Test
    void saveAndLoadOnlyHistoryNoTasks() {
        List<Integer> expectedHistory = List.of();
        taskManager.removeAllTask();
        taskManager.removeAllEpicTask();

        taskManager.saveFile("Чистый файл");
        taskManager.loadFromFile("Чистый файл");

        assertEquals(0, taskManager.getListOfTasks().size());
        assertEquals(0, taskManager.getListOfEpics().size());
        assertEquals(0, taskManager.getListOfSubTasks().size());
        assertEquals(expectedHistory, InMemoryHistoryManagerTest.historyToArray(taskManager.getHistoryManager().getHistory()));
    }

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getFileBackedManagerByPath("resources\\tests\\managerData.csv");
        InputTask inputTask1 = inputTaskCreator.createInputTask("Взять чек", "Получить у босса",
                60, LocalDateTime.of(2022, Month.FEBRUARY, 24, 4, 0));
        InputTask inputTask2 = inputTaskCreator.createInputTask("Обналичить чек", "Зайти в банк",
                120, LocalDateTime.of(2022, Month.FEBRUARY, 24, 12, 0));
        InputTask inputTask3 = inputTaskCreator.createInputTask("Купить молоко", "Зайти в бакалею",
                180, LocalDateTime.of(2022, Month.FEBRUARY, 24, 19, 0));
        inputTaskList.add(inputTask1);
        inputTaskList.add(inputTask2);
        inputTaskList.add(inputTask3);

        InputTaskEpic inputTaskEpic1 = inputTaskCreator.createEpicTask("Фальшивый эпик таск",
                "Внутри всего один сабтаск!");
        InputTaskEpic inputTaskEpic2 = inputTaskCreator.createEpicTask("Завершить спринт 4",
                "Успеть получить зачёт к жесткому дедлайну");
        inputTaskEpicList.add(inputTaskEpic1);
        inputTaskEpicList.add(inputTaskEpic2);

        InputSubTask inputSubTask1 = inputTaskCreator.createInputSubTask("Одинокий сабтаск","Один совсем один",
                480, LocalDateTime.of(2022, Month.MARCH, 31, 12, 0));
        InputSubTask inputSubTask2 = inputTaskCreator.createInputSubTask("Теория", "Пройти до 17.06",
                10080, LocalDateTime.of(2022, Month.JUNE, 1, 22, 0));
        InputSubTask inputSubTask3 = inputTaskCreator.createInputSubTask("Аниме", "Не смотреть",
                10080, LocalDateTime.of(2022, Month.JUNE, 13, 22, 0));


        inputTaskCreator.putSubTaskInEpic(inputSubTask1, inputTaskEpic1);
        inputTaskCreator.putSubTaskInEpic(inputSubTask2, inputTaskEpic2);
        inputTaskCreator.putSubTaskInEpic(inputSubTask3, inputTaskEpic2);

        inputSubTaskList.add(inputSubTask1);
        inputSubTaskList.add(inputSubTask2);
        inputSubTaskList.add(inputSubTask3);

        for (InputTask inputTask : inputTaskList) {
            taskManager.createTask(inputTask);
        }
        for (InputTaskEpic inputTaskEpic : inputTaskEpicList) {
            taskManager.createEpicTask(inputTaskEpic);
        }
    }
}
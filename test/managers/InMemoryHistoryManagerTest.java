package managers;

import input.InputSubTask;
import input.InputTask;
import input.InputTaskCreator;
import input.InputTaskEpic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import util.Managers;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final List<InputTask> inputTaskList = new ArrayList<>();
    private final List<InputTaskEpic> inputTaskEpicList = new ArrayList<>();
    private final List<InputSubTask> inputSubTaskList = new ArrayList<>();
    private final TaskManager inMemoryTaskManager = Managers.getDefault();
    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private final InputTaskCreator inputTaskCreator = new InputTaskCreator();

    /**
     * Согласно ТЗ, при вызове эпик задачи по ID, её подзадачи должны также быть добавлены в историю просмотра.
     * Данная функциональность реализована в методах TaskManager'а getEpicTaskByID(), getSubTaskByID().
     * В данном тесте проверяется поведение методов HistoryManager, поэтому при вызове эпика в историю не будут
     * добавляться её подзадачи. Этот функционал будет проверен в отдельном тесте
     *
     * Подготовка: создание задач методом createInputTasks().
     * Исполнение: Вызов истории
     * Проверка: Не должно быть возвращено null, размер истории должен быть равен 0
     *
     * Исполнение: вызов метода add() для уникальных (без повторов) задачи, эпика, подзадачи, на каждом шаге вызов истории
     *      * Проверка:
     *      * 1) После вызова задачи размер истории 1. Сама история {1}
     *      * 2) После вызова эпика размер истории 2. Сама история {1, 5}
     *      * 3) После вызова подзадачи размер истории 3. Сама история {1, 5, 4}
     */

    @Test
    void shouldAddTasksToHistoryInRightOrder() {
        List<Integer> expectedHistory;
        List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfTasks().get(1));
        history = inMemoryHistoryManager.getHistory();
        expectedHistory = List.of(1);
        assertEquals(1, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");

        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfEpics().get(5));
        history = inMemoryHistoryManager.getHistory();
        expectedHistory = List.of(1, 5);
        assertEquals(2, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");

        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfSubTasks().get(4));
        history = inMemoryHistoryManager.getHistory();
        expectedHistory = List.of(1, 5, 4);
        assertEquals(3, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");
    }

    /**
     * Подготовка: получаем размер истории 5, вызвав метод add() нужное кол-во раз.
     * Исполнение: вызываем повторно эпическую ID 5, затем задачу ID 1
     * Проверка:
     * 1) После вызова задач 0, 3 размер истории 5. Сама история {1, 5, 4, 0, 3}
     * 2) После вызова эпика 5 размер истории 5. Сама история {1, 4, 0, 3, 5}
     * 3) После вызова задачи 1 размер истории 5. Сама история {4, 0, 3, 5, 1}
     *
     * Исполнение: удаление из истории задачи 3 (середина), задачи 4 (начало), задачи 1 (конец)
     *      * Проверка:
     *      * 1) После удаления задачи 3, размер истории 4. Сама история {4, 0, 5, 1}
     *      * 2) После удаления задачи 4, размер истории 4. Сама история {0, 5, 1}
     *      * 3) После удаления задачи 1, размер истории 4. Сама история {0, 5}
     */

    @Test
    void shouldRemoveTasksFromHistoryAndAddToEndIfCalledSecondTime() {
        List<Integer> expectedHistory;
        List<Task> history;
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfTasks().get(1));
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfEpics().get(5));
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfSubTasks().get(4));
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfTasks().get(0));
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfEpics().get(3));

        history = inMemoryHistoryManager.getHistory();
        expectedHistory = List.of(1, 5, 4, 0, 3);
        assertEquals(5, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");

        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfEpics().get(5));
        history = inMemoryHistoryManager.getHistory();
        expectedHistory = List.of(1, 4, 0, 3, 5);
        assertEquals(5, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");

        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfTasks().get(1));
        history = inMemoryHistoryManager.getHistory();
        expectedHistory = List.of(4, 0, 3, 5, 1);
        assertEquals(5, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");
    }

    /**
     * Подготовка: получаем размер истории 5, вызвав метод add() нужное кол-во раз.
     * Исполнение: удаление из истории задачи 3 (середина), задачи 4 (начало), задачи 1 (конец)
     * Проверка:
     * 1) После удаления задачи 3, размер истории 4. Сама история {4, 0, 5, 1}
     * 2) После удаления задачи 4, размер истории 4. Сама история {0, 5, 1}
     * 3) После удаления задачи 1, размер истории 4. Сама история {0, 5}
     */
    @Test
    void shouldRemoveTasksFromHistoryInDifferentPlaces() {
        List<Integer> expectedHistory;
        List<Task> history;
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfSubTasks().get(4));
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfTasks().get(0));
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfEpics().get(3));
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfEpics().get(5));
        inMemoryHistoryManager.add(inMemoryTaskManager.getListOfTasks().get(1));

        inMemoryHistoryManager.remove(3);
        history = inMemoryHistoryManager.getHistory();
        expectedHistory = List.of(4, 0, 5, 1);
        assertEquals(4, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");

        inMemoryHistoryManager.remove(4);
        history = inMemoryHistoryManager.getHistory();
        expectedHistory = List.of(0, 5, 1);
        assertEquals(3, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");

        inMemoryHistoryManager.remove(1);
        history = inMemoryHistoryManager.getHistory();
        expectedHistory = List.of(0, 5);
        assertEquals(2, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");
    }

    /**
     * Подготовка: вызываем подзадачу 4 методом taskManager.getSubTaskByID()
     * Исполнение: вызываем эпик 5 taskManager.getEpicTaskByID()
     * Проверка:
     * 1) После вызова задачи 5, размер истории 4. Сама история {4, 5, 6, 7}
     * 2) После вызова задачи 3, размер истории 5. Сама история {5, 6, 7, 3, 4}, т.к. изначально вызванная подзадача 4
     * будет удалена из начала списка и добавлена в конец после вызова её родительской эпической задачи
     */
    @Test
    void shouldAddSubTasksToHistoryAfterGetEpicWhenAddMethodCalledFromTaskManager() {
        List<Integer> expectedHistory;
        List<Task> history;
        inMemoryTaskManager.getSubTaskByID(4);
        inMemoryTaskManager.getEpicTaskByID(5);
        history = inMemoryTaskManager.getHistoryManager().getHistory();
        expectedHistory = List.of(4, 5, 6, 7);
        assertEquals(4, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");

        inMemoryTaskManager.getEpicTaskByID(3);
        history = inMemoryTaskManager.getHistoryManager().getHistory();
        expectedHistory = List.of(5, 6, 7, 3, 4);
        assertEquals(5, history.size());
        assertEquals(expectedHistory, historyToArray(history), "Gotten history different than expected");
    }

    @BeforeEach
    void createInputTasks() {
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
            inMemoryTaskManager.createTask(inputTask);
        }
        for (InputTaskEpic inputTaskEpic : inputTaskEpicList) {
            inMemoryTaskManager.createEpicTask(inputTaskEpic);
        }
    }

    public static List<Integer> historyToArray(List<Task> history) {
        List<Integer> historyAsID = new ArrayList<>();
        history.stream()
                .mapToInt(Task::getIdentifier)
                .forEach(historyAsID::add);
        return historyAsID;
    }
}
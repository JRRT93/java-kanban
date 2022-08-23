package managers;

import input.InputSubTask;
import input.InputTask;
import input.InputTaskEpic;
import org.junit.jupiter.api.BeforeEach;
import util.Managers;

import java.time.LocalDateTime;
import java.time.Month;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
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
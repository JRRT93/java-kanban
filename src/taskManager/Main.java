package taskManager;

import testing.InputTask;
import testing.InputTaskEpic;
import testing.InputSubTask;
import testing.InputTaskCreator;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        runTestingProgram();
    }

    public static void runTestingProgram() {
        InputTaskCreator inputTaskCreator = new InputTaskCreator();
        TaskManager taskManager = new TaskManager();
        Random random = new Random();

        System.out.println("НАЧАЛО ТЕСТА. СОЗДАНИЕ ВХОДЯЩИХ ОБЪЕКТОВ");
        InputTask inputTask1 = inputTaskCreator.createInputTask("Взять чек", "Получить у босса");
        InputTask inputTask2 = inputTaskCreator.createInputTask("Обналичить чек", "Зайти в банк");
        InputTask inputTask3 = inputTaskCreator.createInputTask("Купить молоко", "Зайти в бакалею");

        InputTaskEpic inputTaskEpic1 = inputTaskCreator.createEpicTask("Завершить спринт 3",
                "Сдать финальное задание с первого раза!");
        InputTaskEpic inputTaskEpic2 = inputTaskCreator.createEpicTask("Завершить спринт 4",
                "Успеть получить зачёт к жесткому дедлайну");

        InputSubTask inputSubTask1 = inputTaskCreator.createInputSubTask("Эффективный тайм-менеджмент",
                "Получи вдохновение, пиши код до утра, иди на работу, там пускай слюни и усни",
                inputTaskEpic1);
        InputSubTask inputSubTask2 = inputTaskCreator.createInputSubTask("Теория", "Пройти до 17.06",
                inputTaskEpic2);
        InputSubTask inputSubTask3 = inputTaskCreator.createInputSubTask("Аниме", "Не смотреть",
                inputTaskEpic2);
        inputTaskCreator.putSubTaskInEpic(inputSubTask1, inputTaskEpic1);
        inputTaskCreator.putSubTaskInEpic(inputSubTask2, inputTaskEpic2);
        inputTaskCreator.putSubTaskInEpic(inputSubTask3, inputTaskEpic2);

        System.out.println(taskManager.createTask(inputTask1));
        System.out.println(taskManager.createTask(inputTask2));
        System.out.println(taskManager.createTask(inputTask3) + "\n");
        System.out.println(taskManager.createEpicTask(inputTaskEpic1) + "\n");
        System.out.println(taskManager.createEpicTask(inputTaskEpic2) + "\n");
        System.out.println("Входящие объекты для теста созданы✅✅✅\n");

        getManagerTaskListsTest(taskManager);
        getTaskByIDTest(taskManager);
        updateTasksTest(taskManager, inputTaskCreator);
        getListOfRelatedSubTasksTest(taskManager);
        removeTaskByID(taskManager);
        removeAllTasksTest(taskManager);
    }

    public static void getManagerTaskListsTest(TaskManager taskManager) {
        System.out.println("ПОЛУЧЕНИЕ СПИСКОВ ЗАДАЧ ИЗ МЕНЕДЖЕРА");
        System.out.println("ОСНОВНЫЕ ЗАДАЧИ:\n" + taskManager.getListOfTasks());
        System.out.println("ЭПИЧЕСКИЕ ЗАДАЧИ:\n" + taskManager.getListOfEpics());
        System.out.println("ВСЕ ПОДЗАДАЧИ:\n" + taskManager.getListOfSubTasks());
        System.out.println("✅✅✅");
    }

    public static void getTaskByIDTest(TaskManager taskManager) {
        System.out.println("\nПОЛУЧЕНИЕ ЗАДАЧИ ПО НОМЕРУ ID");
        int randomID = -1;
        while (!taskManager.getListOfTasks().containsKey(randomID)) {
            randomID = pickRandomID(taskManager);
        }
        System.out.println("СЛУЧАЙНАЯ ОСНОВНАЯ ЗАДАЧА:\n" + taskManager.getTaskByID(randomID));

        randomID = -1;
        while (!taskManager.getListOfEpics().containsKey(randomID)) {
            randomID = pickRandomID(taskManager);
        }
        System.out.println("СЛУЧАЙНАЯ ЭПИЧЕСКАЯ ЗАДАЧА:\n" + taskManager.getEpicTaskByID(randomID));

        randomID = -1;
        while (!taskManager.getListOfSubTasks().containsKey(randomID)) {
            randomID = pickRandomID(taskManager);
        }
        System.out.println("СЛУЧАЙНАЯ ПОДЗАДАЧА:\n" + taskManager.getSubTaskByID(randomID));
        System.out.println("✅✅✅");
    }

    public static void updateTasksTest(TaskManager taskManager, InputTaskCreator inputTaskCreator) {
        System.out.println("\nОТПРАВКА ЗАДАЧИ ВО ВНЕ, ПОЛУЧЕНИЕ ИЗМЕНЕННОЙ ЗАДАЧИ, ОБНОВЛЕНИЕ ИНФОРМАЦИИ В МЕНЕДЖЕРЕ");
        int randomID = -1;
        while (!taskManager.getListOfTasks().containsKey(randomID)) {
            randomID = pickRandomID(taskManager);
        }
        Task taskForUpdate = taskManager.getTaskByID(randomID);
        System.out.println("СЛУЧАЙНАЯ ОСНОВНАЯ ЗАДАЧА ДО ОБНОВЛЕНИЯ:\n" + taskForUpdate);
        InputTask updatedInputTask = inputTaskCreator.updateTask(taskForUpdate);
        Task updatedTask = taskManager.updateTask(updatedInputTask);
        System.out.println("СЛУЧАЙНАЯ ОСНОВНАЯ ЗАДАЧА ПОСЛЕ:\n" + updatedTask);

        randomID = -1;
        while (!taskManager.getListOfEpics().containsKey(randomID)) {
            //randomID = pickRandomID(taskManager);
            randomID = 5;
        }
        EpicTask epicTaskForUpdate = taskManager.getEpicTaskByID(randomID);
        System.out.println("СЛУЧАЙНАЯ ЭПИЧЕСКАЯ ЗАДАЧА ДО ОБНОВЛЕНИЯ:\n" + epicTaskForUpdate);
        InputTaskEpic updatedInputTaskEpic = inputTaskCreator.updateEpicTask(epicTaskForUpdate);
        EpicTask updatedEpicTask = taskManager.updateEpicTask(updatedInputTaskEpic);
        System.out.println("СЛУЧАЙНАЯ ЭПИЧЕСКАЯ ЗАДАЧА ПОСЛЕ:\n" + updatedEpicTask);
        System.out.println("✅✅✅");
    }

    public static void getListOfRelatedSubTasksTest(TaskManager taskManager) {
        System.out.println("\nПОЛУЧЕНИЕ СПИСКА ПОДЗАДАЧ ДЛЯ СЛУЧАЙНОГО ЭПИКА:");
        int randomID = -1;
        while (!taskManager.getListOfEpics().containsKey(randomID)) {
            randomID = pickRandomID(taskManager);
        }
        System.out.println(taskManager.getListOfEpicsSubTasks(taskManager.getEpicTaskByID(randomID)));
        System.out.println("✅✅✅");
    }

    public static void removeTaskByID(TaskManager taskManager) {
        System.out.println("\nУДАЛЕНИЕ ЗАДАЧИ ПО НОМЕРУ ID");
        int randomID = -1;
        while (!taskManager.getListOfTasks().containsKey(randomID)) {
            randomID = pickRandomID(taskManager);
        }
        System.out.println("ПЕРЕЧЕНЬ ОСНОВНЫХ ЗАДАЧ ДО УДАЛЕНИЯ: " + taskManager.getListOfTasks());
        taskManager.removeTaskByID(randomID);
        System.out.println("ПЕРЕЧЕНЬ ОСНОВНЫХ ЗАДАЧ ПОСЛЕ: " + taskManager.getListOfTasks());

        randomID = -1;
        while (!taskManager.getListOfEpics().containsKey(randomID)) {
            randomID = pickRandomID(taskManager);
        }
        System.out.println("\nПЕРЕЧЕНЬ ЭПИЧЕСКИХ ЗАДАЧ ДО УДАЛЕНИЯ: \n" + taskManager.getListOfEpics());
        taskManager.removeEpicTaskByID(randomID);
        System.out.println("\nПЕРЕЧЕНЬ ЭПИЧЕСКИХ ЗАДАЧ ПОСЛЕ: \n" + taskManager.getListOfEpics());

        randomID = -1;
        while (!taskManager.getListOfSubTasks().containsKey(randomID)) {
            randomID = pickRandomID(taskManager);
        }
        System.out.println("\nПЕРЕЧЕНЬ ПОДЗАДАЧ ДО УДАЛЕНИЯ: " + taskManager.getListOfSubTasks());
        taskManager.removeSubTaskByID(randomID);
        System.out.println("ПЕРЕЧЕНЬ ПОДЗАДАЧ ЗАДАЧ ПОСЛЕ: " + taskManager.getListOfSubTasks());
        System.out.println("✅✅✅");
    }

    public static void removeAllTasksTest(TaskManager taskManager) {
        System.out.println("\nУДАЛЕНИЕ ВСЕХ ЗАДАЧ");
        taskManager.removeAllEpicTask();
        taskManager.removeAllSubTask();
        taskManager.removeAllTask();
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfSubTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println("✅✅✅");
        System.out.println("ТЕСТ ЗАВЕРШЕН");
    }

    public static int pickRandomID(TaskManager taskManager) {
        Random random = new Random();
        return random.nextInt(taskManager.getIdentifier());
    }
}

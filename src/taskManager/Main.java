package taskManager;

import testing.InputTask; //todo сделать методы для выбора рандомной задачи, подзадачи, эпика и потом их вызывать где надо
import testing.InputTaskEpic;
import testing.InputSubTask; //todo в методах проверки продумать и прописать булевы результаты успеха или неуспеха теста
import testing.InputTaskCreator;
import utilityClasses.HistoryManager;
import utilityClasses.Managers;
import utilityClasses.TaskManager;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        runTestingProgram();
    }

    public static void runTestingProgram() {
        InputTaskCreator inputTaskCreator = new InputTaskCreator();
        TaskManager inMemoryTaskManager = Managers.getDefault();
        HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        List<InputTask> inputTaskList = new ArrayList<>();
        List<InputTaskEpic> inputTaskEpicList = new ArrayList<>();
        List<InputSubTask> inputSubTaskList = new ArrayList<>();


        createInputObjects(inputTaskCreator, inputTaskList, inputTaskEpicList, inputSubTaskList);
        createObjects(inMemoryTaskManager, inputTaskList, inputTaskEpicList);
        getManagerTaskListsTest(inputTaskList, inputSubTaskList, inputTaskEpicList, inMemoryTaskManager);
        getTaskByIDTest(inMemoryTaskManager, inMemoryHistoryManager);
        updateTasksTest(inMemoryTaskManager, inMemoryHistoryManager, inputTaskCreator);
        getListOfRelatedSubTasksTest(inMemoryTaskManager, inMemoryHistoryManager);
        removeTaskByID(inMemoryTaskManager);
        removeAllTasksTest(inMemoryTaskManager);
    }

    public static void createInputObjects(InputTaskCreator inputTaskCreator, List<InputTask> inputTaskList,
                                          List<InputTaskEpic> inputTaskEpicList, List<InputSubTask> inputSubTaskList) {
        System.out.println("НАЧАЛО ТЕСТА. \nСОЗДАНИЕ ВХОДЯЩИХ ОБЪЕКТОВ");
        InputTask inputTask1 = inputTaskCreator.createInputTask("Взять чек", "Получить у босса");
        InputTask inputTask2 = inputTaskCreator.createInputTask("Обналичить чек", "Зайти в банк");
        InputTask inputTask3 = inputTaskCreator.createInputTask("Купить молоко", "Зайти в бакалею");
        inputTaskList.add(inputTask1);
        inputTaskList.add(inputTask2);
        inputTaskList.add(inputTask3);

        InputTaskEpic inputTaskEpic1 = inputTaskCreator.createEpicTask("Фальшивый эпик таск",
                "Внутри всего один сабтаск!");
        InputTaskEpic inputTaskEpic2 = inputTaskCreator.createEpicTask("Завершить спринт 4",
                "Успеть получить зачёт к жесткому дедлайну");
        InputTaskEpic inputTaskEpic3 = inputTaskCreator.createEpicTask("Занятия в бассейне",
                "Научиться плавать кролем");
        inputTaskEpicList.add(inputTaskEpic1);
        inputTaskEpicList.add(inputTaskEpic2);
        inputTaskEpicList.add(inputTaskEpic3);

        InputSubTask inputSubTask1 = inputTaskCreator.createInputSubTask("Одинокий сабтаск",
                "Один, совсем один",
                inputTaskEpic1);
        InputSubTask inputSubTask2 = inputTaskCreator.createInputSubTask("Теория", "Пройти до 17.06",
                inputTaskEpic2);
        InputSubTask inputSubTask3 = inputTaskCreator.createInputSubTask("Аниме", "Не смотреть",
                inputTaskEpic2);
        InputSubTask inputSubTask4 = inputTaskCreator.createInputSubTask("Бассейн",
                "Найти подходящий",
                inputTaskEpic1);
        InputSubTask inputSubTask5 = inputTaskCreator.createInputSubTask("Тренер", "Договориться",
                inputTaskEpic2);
        InputSubTask inputSubTask6 = inputTaskCreator.createInputSubTask("Экипировка",
                "Плавки, шапочка, очки", inputTaskEpic2);

        inputTaskCreator.putSubTaskInEpic(inputSubTask1, inputTaskEpic1);
        inputTaskCreator.putSubTaskInEpic(inputSubTask2, inputTaskEpic2);
        inputTaskCreator.putSubTaskInEpic(inputSubTask3, inputTaskEpic2);
        inputTaskCreator.putSubTaskInEpic(inputSubTask4, inputTaskEpic3);
        inputTaskCreator.putSubTaskInEpic(inputSubTask5, inputTaskEpic3);
        inputTaskCreator.putSubTaskInEpic(inputSubTask6, inputTaskEpic3);

        inputSubTaskList.add(inputSubTask1);
        inputSubTaskList.add(inputSubTask2);
        inputSubTaskList.add(inputSubTask3);
        inputSubTaskList.add(inputSubTask4);
        inputSubTaskList.add(inputSubTask5);
        inputSubTaskList.add(inputSubTask6);
        System.out.println("ВХОДЯЩИЕ ОБЪЕКТЫ СОЗДАНЫ✅");
    }

    public static void createObjects(TaskManager inMemoryTaskManager, List<InputTask> inputTaskList,
                                     List<InputTaskEpic> inputTaskEpicList) {
        for (InputTask inputTask : inputTaskList) {
            System.out.println(inMemoryTaskManager.createTask(inputTask));
        }

        for (InputTaskEpic inputTaskEpic : inputTaskEpicList) {
            System.out.println(inMemoryTaskManager.createEpicTask(inputTaskEpic));
        }
        System.out.println("ОСНОВНЫЕ ОБЪЕКТЫ СОЗДАНЫ✅\n");
    }

    public static void getManagerTaskListsTest(List<InputTask> inputTaskList, List<InputSubTask> inputSubTaskList,
                                               List<InputTaskEpic> inputTaskEpicList, TaskManager inMemoryTaskManager) {
        System.out.println("ПОЛУЧЕНИЕ СПИСКОВ ЗАДАЧ ИЗ МЕНЕДЖЕРА");
        System.out.println("ОСНОВНЫЕ ЗАДАЧИ:");
        printListOfTasks(inMemoryTaskManager.getListOfTasks());
        System.out.println("ЭПИЧЕСКИЕ ЗАДАЧИ:");
        printListOfEpicTasks(inMemoryTaskManager.getListOfEpics());
        System.out.println("ВСЕ ПОДЗАДАЧИ:");
        printListOfSubTasks(inMemoryTaskManager.getListOfSubTasks());
        checkInputAgainstObject(inputTaskList, inputSubTaskList, inputTaskEpicList, inMemoryTaskManager);
    }

    public static void getTaskByIDTest(TaskManager inMemoryTaskManager, HistoryManager inMemoryHistoryManager) {
        int randomID;
        List<Task> requestedTasks = new ArrayList<>();

        System.out.println("\nПОЛУЧЕНИЕ ЗАДАЧИ ПО НОМЕРУ ID");
        randomID = -1;
        while (!inMemoryTaskManager.getListOfTasks().containsKey(randomID)) {
            randomID = pickRandomID(inMemoryTaskManager);
        }
        Task requestedTask = inMemoryTaskManager.getTaskByID(randomID, inMemoryHistoryManager);
        requestedTasks.add(requestedTask);
        System.out.println("СЛУЧАЙНАЯ ОСНОВНАЯ ЗАДАЧА:\n" + requestedTask);

        randomID = -1;
        while (!inMemoryTaskManager.getListOfEpics().containsKey(randomID)) {
            randomID = pickRandomID(inMemoryTaskManager);
        }
        EpicTask requestedEpicTask = inMemoryTaskManager.getEpicTaskByID(randomID, inMemoryHistoryManager);
        requestedTasks.add(requestedEpicTask);
        System.out.println("СЛУЧАЙНАЯ ЭПИЧЕСКАЯ ЗАДАЧА:\n" + requestedEpicTask);

        randomID = -1;
        while (!inMemoryTaskManager.getListOfSubTasks().containsKey(randomID)) {
            randomID = pickRandomID(inMemoryTaskManager);
        }
        SubTask requestedSubTask = inMemoryTaskManager.getSubTaskByID(randomID, inMemoryHistoryManager);
        requestedTasks.add(requestedSubTask);
        System.out.println("СЛУЧАЙНАЯ ПОДЗАДАЧА:\n" + requestedSubTask);
        requestHistoryTest(requestedTasks, inMemoryHistoryManager);

        System.out.println("\nВЫЗЫВАЕМ ЕЩЁ 9 ПОДЗАДАЧ В ДОПОЛНЕНИЕ К 3 ВЫЗВАННЫМ РАНЕЕ:");
        for (int i = 0; i < 9; i++) {
            randomID = -1;
            while (!inMemoryTaskManager.getListOfSubTasks().containsKey(randomID)) {
                randomID = pickRandomID(inMemoryTaskManager);
            }
            SubTask requested = inMemoryTaskManager.getSubTaskByID(randomID, inMemoryHistoryManager);
            if (requestedTasks.size() < 10) {
                requestedTasks.add(requested);
                System.out.println(requestedTasks.get(i + 3));
            } else {
                requestedTasks.remove(0);
                requestedTasks.add(requested);
                System.out.println(requestedTasks.get(9));
            }
        }
        requestHistoryTest(requestedTasks, inMemoryHistoryManager);
    }

    public static void requestHistoryTest(List<Task> requestedTasks, HistoryManager inMemoryHistoryManager) {
        System.out.println("История просмотра:");
        List<Task> lastTenTaskRequest = inMemoryHistoryManager.getHistory();
        for (Task task : lastTenTaskRequest) {
            System.out.println(task);
        }
        if (checkHistoryTaskQuantity(requestedTasks.size(), inMemoryHistoryManager)) {
            System.out.println("Успех✅✅✅ Количество вызванных задач и количество задач в истории просмотра совпадают.");
        } else {
            System.out.println("Провал. Количество просмотренных и записанных задач в историю не совпадают");
        }
        if (requestedTasks.equals(inMemoryHistoryManager.getHistory())) {
            System.out.println("Успех✅✅✅ Порядок вызванных задач и порядок задач в истории просмотра совпадают.");
        } else {
            System.out.println("Порядок просмотренных и порядок записанных задач в историю не совпадают");
        }
    }

    public static void updateTasksTest(TaskManager inMemoryTaskManager, HistoryManager inMemoryHistoryManager,
                                       InputTaskCreator inputTaskCreator) {
        System.out.println("\nОТПРАВКА ЗАДАЧИ ВО ВНЕ, ПОЛУЧЕНИЕ ИЗМЕНЕННОЙ ЗАДАЧИ, ОБНОВЛЕНИЕ ИНФОРМАЦИИ В МЕНЕДЖЕРЕ");
        int randomID = -1;
        while (!inMemoryTaskManager.getListOfTasks().containsKey(randomID)) {
            randomID = pickRandomID(inMemoryTaskManager);
        }
        Task taskForUpdate = inMemoryTaskManager.getTaskByID(randomID, inMemoryHistoryManager);
        System.out.println("СЛУЧАЙНАЯ ОСНОВНАЯ ЗАДАЧА ДО ОБНОВЛЕНИЯ:\n" + taskForUpdate);
        InputTask updatedInputTask = inputTaskCreator.updateTask(taskForUpdate);
        Task updatedTask = inMemoryTaskManager.updateTask(updatedInputTask);
        System.out.println("СЛУЧАЙНАЯ ОСНОВНАЯ ЗАДАЧА ПОСЛЕ:\n" + updatedTask);

        randomID = -1;
        while (!inMemoryTaskManager.getListOfEpics().containsKey(randomID)) {
            randomID = pickRandomID(inMemoryTaskManager);
        }
        EpicTask epicTaskForUpdate = inMemoryTaskManager.getEpicTaskByID(randomID, inMemoryHistoryManager);
        System.out.println("СЛУЧАЙНАЯ ЭПИЧЕСКАЯ ЗАДАЧА ДО ОБНОВЛЕНИЯ:\n" + epicTaskForUpdate);
        for (Map.Entry<String, SubTask> entry : epicTaskForUpdate.getListOfRelatedSubTasks().entrySet()) {
            System.out.println(entry.getValue());
        }
        InputTaskEpic updatedInputTaskEpic = inputTaskCreator.updateEpicTask(epicTaskForUpdate);
        EpicTask updatedEpicTask = inMemoryTaskManager.updateEpicTask(updatedInputTaskEpic);
        System.out.println("СЛУЧАЙНАЯ ЭПИЧЕСКАЯ ЗАДАЧА ПОСЛЕ:\n" + updatedEpicTask);
        for (Map.Entry<String, SubTask> entry : updatedEpicTask.getListOfRelatedSubTasks().entrySet()) {
            System.out.println(entry.getValue());
        }
        System.out.println("ЗАДАЧИ ОБНОВЛЕНЫ✅");
    }

    public static void getListOfRelatedSubTasksTest(TaskManager inMemoryTaskManager,
                                                    HistoryManager inMemoryHistoryManager) {
        System.out.println("\nПОЛУЧЕНИЕ СПИСКА ПОДЗАДАЧ ДЛЯ СЛУЧАЙНОГО ЭПИКА:");
        int randomID = -1;
        while (!inMemoryTaskManager.getListOfEpics().containsKey(randomID)) {
            randomID = pickRandomID(inMemoryTaskManager);
        }
        System.out.println(inMemoryTaskManager.getListOfEpicsSubTasks(inMemoryTaskManager.getEpicTaskByID(randomID,
                inMemoryHistoryManager)));
        System.out.println("СПИСОК ВЫВЕДЕН✅");
    }

    public static void removeTaskByID(TaskManager inMemoryTaskManager) {
        System.out.println("\nУДАЛЕНИЕ ЗАДАЧИ ПО НОМЕРУ ID");
        int randomID = -1;
        while (!inMemoryTaskManager.getListOfTasks().containsKey(randomID)) {
            randomID = pickRandomID(inMemoryTaskManager);
        }
        System.out.println("ПЕРЕЧЕНЬ ОСНОВНЫХ ЗАДАЧ ДО УДАЛЕНИЯ:");
        printListOfTasks(inMemoryTaskManager.getListOfTasks());
        inMemoryTaskManager.removeTaskByID(randomID);
        System.out.println("ПЕРЕЧЕНЬ ОСНОВНЫХ ЗАДАЧ ПОСЛЕ:");
        printListOfTasks(inMemoryTaskManager.getListOfTasks());

        randomID = -1;
        while (!inMemoryTaskManager.getListOfEpics().containsKey(randomID)) {
            randomID = pickRandomID(inMemoryTaskManager);
        }
        System.out.println("\nУДАЛЯЮ ЗАДАЧУ С ID 5, Т.К. У НЕЁ 2 САБТАСКИ. ПОСЛЕ УДАЛЕНИЯ ЭПИКА ОНИ ТАКЖЕ ДОЛЖНЫ БЫТЬ\n" +
                "УДАЛЕНЫ ИЗ ПЕРЕЧНЯ САБСТАСКОВ. ПЕРЕЧЕНЬ ЭПИЧЕСКИХ ЗАДАЧ ДО УДАЛЕНИЯ:");
        printListOfEpicTasks(inMemoryTaskManager.getListOfEpics());
        printListOfSubTasks(inMemoryTaskManager.getListOfSubTasks());
        inMemoryTaskManager.removeEpicTaskByID(5);
        System.out.println("\nПЕРЕЧЕНЬ ЭПИЧЕСКИХ ЗАДАЧ ПОСЛЕ:");
        printListOfEpicTasks(inMemoryTaskManager.getListOfEpics());
        printListOfSubTasks(inMemoryTaskManager.getListOfSubTasks());
        checkDeletionOfEpicID5(inMemoryTaskManager);

        randomID = -1;
        while (!inMemoryTaskManager.getListOfSubTasks().containsKey(randomID)) {
            randomID = pickRandomID(inMemoryTaskManager);
        }
        System.out.println("\nУДАЛЯЮ ЗАДАЧУ С ID 4, Т.К. ОНА ЕДИНСТВЕННАЯ САБТАСКА ДЛЯ ЭПИКА ID 3. ПОСЛЕ УДАЛЕНИЯ\n " +
                "САБТАСКА ИЗ ПЕРЕЧНЯ ЭПИКОВ ДОЛЖЕН БЫТЬ УДАЛЕН РОДИТЕЛЬСКИЙ ЭПИК. ПЕРЕЧЕНЬ ПОДЗАДАЧ ДО УДАЛЕНИЯ:");
        printListOfSubTasks(inMemoryTaskManager.getListOfSubTasks());
        System.out.println("ПЕРЕЧЕНЬ ЭПИЧЕСКИХ ЗАДАЧ ДО:");
        printListOfEpicTasks(inMemoryTaskManager.getListOfEpics());
        inMemoryTaskManager.removeSubTaskByID(4);
        System.out.println("ПЕРЕЧЕНЬ ПОДЗАДАЧ ЗАДАЧ ПОСЛЕ:");
        printListOfSubTasks(inMemoryTaskManager.getListOfSubTasks());
        System.out.println("ПЕРЕЧЕНЬ ЭПИЧЕСКИХ ЗАДАЧ ПОСЛЕ:");
        printListOfEpicTasks(inMemoryTaskManager.getListOfEpics());
        checkDeletionOfSubID4(inMemoryTaskManager);

    }

    public static void removeAllTasksTest(TaskManager inMemoryTaskManager) {
        System.out.println("\nУДАЛЕНИЕ ВСЕХ ЗАДАЧ");
        inMemoryTaskManager.removeAllEpicTask();
        inMemoryTaskManager.removeAllSubTask();
        inMemoryTaskManager.removeAllTask();
        System.out.println(inMemoryTaskManager.getListOfTasks());
        System.out.println(inMemoryTaskManager.getListOfSubTasks());
        System.out.println(inMemoryTaskManager.getListOfEpics());
        System.out.println("✅✅✅");
        System.out.println("ТЕСТ ЗАВЕРШЕН");
    }

    public static int pickRandomID(TaskManager inMemoryTaskManager) {
        Random random = new Random();
        return random.nextInt(inMemoryTaskManager.getIdentifier());
    }

    public static boolean checkQuantityOfCreatedTasks(List<InputTask> inputTaskList, List<InputSubTask> inputSubTaskList,
                                                      List<InputTaskEpic> inputTaskEpicList,
                                                      TaskManager inMemoryTaskManager) {
        return inputTaskList.size() == inMemoryTaskManager.getListOfTasks().size()
                && inputTaskEpicList.size() == inMemoryTaskManager.getListOfEpics().size()
                && inputSubTaskList.size() == inMemoryTaskManager.getListOfSubTasks().size();

    }

    public static boolean checkUniquenessOfID(TaskManager inMemoryTaskManager) {
        boolean success = false;
        List<Integer> listOfID = new ArrayList<>();
        List<Integer> listOfDuplicates = new ArrayList<>();
        for (Map.Entry<Integer, Task> entry : inMemoryTaskManager.getListOfTasks().entrySet()) {
            listOfID.add(entry.getKey());
        }
        for (Map.Entry<Integer, EpicTask> entry : inMemoryTaskManager.getListOfEpics().entrySet()) {
            listOfID.add(entry.getKey());
        }
        for (Map.Entry<Integer, SubTask> entry : inMemoryTaskManager.getListOfSubTasks().entrySet()) {
            listOfID.add(entry.getKey());
        }
        Collections.sort(listOfID);
        for (int i = 1; i < listOfID.size(); i++) {
            if (listOfID.get(i - 1).equals(listOfID.get(i))) {
                listOfDuplicates.add(listOfID.get(i));
            }
        }
        if (listOfDuplicates.size() == 0) {
            success = true;
        }
        return success;
    }

    public static boolean checkTasksNamesDescriptions(List<InputTask> inputTaskList, List<InputSubTask> inputSubTaskList,
                                                      List<InputTaskEpic> inputTaskEpicList, TaskManager inMemoryTaskManager) {
        List<String> inputTasksNamesList = new ArrayList<>();
        List<String> inputTasksDescriptionList = new ArrayList<>();
        List<String> tasksNamesList = new ArrayList<>();
        List<String> tasksDescriptionList = new ArrayList<>();

        for (Map.Entry<Integer, Task> entry : inMemoryTaskManager.getListOfTasks().entrySet()) {
            tasksNamesList.add(entry.getValue().getTaskName());
            tasksDescriptionList.add(entry.getValue().getDescription());
        }
        for (Map.Entry<Integer, EpicTask> entry : inMemoryTaskManager.getListOfEpics().entrySet()) {
            tasksNamesList.add(entry.getValue().getTaskName());
            tasksDescriptionList.add(entry.getValue().getDescription());
        }
        for (Map.Entry<Integer, SubTask> entry : inMemoryTaskManager.getListOfSubTasks().entrySet()) {
            tasksNamesList.add(entry.getValue().getTaskName());
            tasksDescriptionList.add(entry.getValue().getDescription());
        }

        for (InputTask inputTask : inputTaskList) {
            inputTasksNamesList.add(inputTask.getTaskName());
            inputTasksDescriptionList.add(inputTask.getDescription());
        }
        for (InputTaskEpic inputTaskEpic : inputTaskEpicList) {
            inputTasksNamesList.add(inputTaskEpic.getTaskName());
            inputTasksDescriptionList.add(inputTaskEpic.getDescription());
        }
        for (InputSubTask inputSubTask : inputSubTaskList) {
            inputTasksNamesList.add(inputSubTask.getTaskName());
            inputTasksDescriptionList.add(inputSubTask.getDescription());
        }

        Collections.sort(inputTasksNamesList);
        Collections.sort(inputTasksDescriptionList);
        Collections.sort(tasksNamesList);
        Collections.sort(tasksDescriptionList);

        if (inputTasksDescriptionList.size() != tasksDescriptionList.size() || inputTasksNamesList.size()
                != tasksNamesList.size()) {
            return false;
        }
        return inputTasksDescriptionList.equals(tasksDescriptionList) && inputTasksNamesList.equals(tasksNamesList);
    }

    public static boolean checkHistoryTaskQuantity(int requestedTasks, HistoryManager historyManager) {
        if (requestedTasks <= 10) {
            return requestedTasks == historyManager.getHistory().size();
        } else {
            return historyManager.getHistory().size() == 10;
        }
    }

    public static void checkInputAgainstObject (List<InputTask> inputTaskList, List<InputSubTask> inputSubTaskList,
                                                List<InputTaskEpic> inputTaskEpicList, TaskManager inMemoryTaskManager) {
        if (checkQuantityOfCreatedTasks(inputTaskList, inputSubTaskList, inputTaskEpicList, inMemoryTaskManager)) {
            System.out.println("Успех✅✅✅ Количество созданных объектов и объектов в TaskManager совпадает.");
        } else {
            System.out.println("Провал. Количество созданных объектов и объектов в TaskManager не совпадает.");
        }
        if (checkUniquenessOfID(inMemoryTaskManager)) {
            System.out.println("Успех✅✅✅ Все ID уникальные");
        } else {
            System.out.println("Провал. Повторяющиеся значения ID");
        }
        if (checkTasksNamesDescriptions(inputTaskList, inputSubTaskList, inputTaskEpicList, inMemoryTaskManager)) {
            System.out.println("Успех✅✅✅ Все названия и описание созданных задач соответствуют входным данным");
        } else {
            System.out.println("Провал. Есть несовпадения в именах");
        }
    }

    public static void printListOfTasks(Map<Integer, Task> map) {
        for (Map.Entry<Integer, Task> entry : map.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public static void printListOfEpicTasks(Map<Integer, EpicTask> map) {
        for (Map.Entry<Integer, EpicTask> entry : map.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public static void printListOfSubTasks(Map<Integer, SubTask> map) {
        for (Map.Entry<Integer, SubTask> entry : map.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public static void checkDeletionOfEpicID5 (TaskManager inMemoryTaskManager) {
        boolean isEpicInDaList = inMemoryTaskManager.getListOfEpics().containsKey(5);
        boolean isSubTasksInDaList = inMemoryTaskManager.getListOfSubTasks().size() == 4;
        if (!isEpicInDaList && isSubTasksInDaList) {
            System.out.println("Успех✅✅✅ ТАСК №5 УДАЛЕНА КОРРЕКТНО, ВМЕСТЕ С САБТАСКАМИ");
        }
        else {
            System.out.println("Провал✅✅✅ Включай отладчик");
        }
    }

    public static void checkDeletionOfSubID4 (TaskManager inMemoryTaskManager) {
        boolean isEpicInDaList = inMemoryTaskManager.getListOfEpics().containsKey(3);
        boolean isSubTasksInDaList = inMemoryTaskManager.getListOfSubTasks().containsKey(4);
        if (!isEpicInDaList && !isSubTasksInDaList) {
            System.out.println("Успех✅✅✅ САБТАСК №4 УДАЛЕНА КОРРЕКТНО, ВМЕСТЕ С РОДИТЕЛЬСКИМ ЭПИКОМ");
        }
        else {
            System.out.println("Провал✅✅✅ Включай отладчик");
        }
    }
}
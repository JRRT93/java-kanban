package managers;

import input.InputSubTask;
import input.InputTask;
import input.InputTaskCreator;
import input.InputTaskEpic;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

 abstract class TaskManagerTest<T extends TaskManager> {
    List<InputTask> inputTaskList = new ArrayList<>();
    List<InputTaskEpic> inputTaskEpicList = new ArrayList<>();
    List<InputSubTask> inputSubTaskList = new ArrayList<>();
    T taskManager;
    InputTaskCreator inputTaskCreator = new InputTaskCreator();

    /**
     * В этом тесте проверяется действительно ли создаются задачи методами .createTask() .createEpicTask() .createSubTask(),
     * а также валидация по продолжительности
     * Подготовка: создание задач методом createInputTasks()
     * Исполнение: вызов перечня задач, эпиков, подзадач
     * Проверка:
     * 1) Размер перечня задач - 3, эпиков - 2, подзадач - 3
     * 2) Проверка уникальности ID для всех созданных задач специальным методом, должен вернуть TRUE
     * 3) Проверка инициализации полей Name и Description для всех созданных задач специальным методом, должен вернуть TRUE
     *
     * Подготовка: создание задач, которые имеют пересечение по времени выполнения
     * Исполнение: вызов метода createTask(), .createEpicTask() .createSubTask()
     * Проверка: должны быть выброшены исключения IllegalArgumentException
     */
    @Test
    void createTasks() {
       assertEquals(3, taskManager.getListOfTasks().size(), "Количество созданных " +
               "задач не совпадает");
       assertEquals(2, taskManager.getListOfEpics().size(), "Количество созданных " +
               "эпических задач не совпадает");
       assertEquals(3, taskManager.getListOfSubTasks().size(), "Количество созданных " +
               "сабтасков не совпадает");
       assertTrue(checkUniquenessOfID(), "Не все ID уникальны");
       assertTrue(checkTasksNamesDescriptions(), "Наименование или описание созданных задач не соответствуют " +
               "входящим");

       InputTask inputTask = new InputTask("Задача", "С перечением по времени",
               2880, LocalDateTime.of(2022, Month.FEBRUARY, 23, 12, 0));

       assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(inputTask));
    }

    /**
     * Подготовка: создается новая задача, с такими же полями как в методе createInputTasks()
     * Исполнение: вызывается задача по ID
     * Проверка:
     * 1) эквивалентность объекта задачи, при вызове с корректным ID
     * 2) Выбрасывание NullPointerException при несуществующем ID -1
     * 3) Выбрасывание NullPointerException при пустом перечне задач
     */
    @Test
    void getTaskByID() {
       Task task = new Task("Обналичить чек", "Зайти в банк", 1,
               120, LocalDateTime.of(2022, Month.FEBRUARY, 24, 12, 0));
       Task requestedTask = taskManager.getTaskByID(1);
       assertEquals(task, requestedTask, "Возвращаемая задача не equals ожидаемой");

       assertThrows(NullPointerException.class, () -> taskManager.getTaskByID(-1));

       taskManager.removeAllTask();
       assertThrows(NullPointerException.class, () -> taskManager.getTaskByID(1));
    }

    /**
     * Подготовка: создается новая эпическая задача, с такими же полями и подзадачами как в методе createInputTasks()
     * Исполнение: вызывается задача по ID
     * Проверка:
     * 1) эквивалентность объекта задачи, при вызове с корректным ID
     * 2) Выбрасывание NullPointerException при несуществующем ID -1
     * 3) Выбрасывание NullPointerException при пустом перечне задач
     */
    @Test
    void getEpicTaskByID() {
       EpicTask epicTask = new EpicTask("Завершить спринт 4",
               "Успеть получить зачёт к жесткому дедлайну", 5);
       SubTask subTask1 = new SubTask("Теория", "Пройти до 17.06", 6, epicTask,
               12000, LocalDateTime.of(2022, Month.JUNE, 11, 22, 0));
       SubTask subTask2 = new SubTask("Аниме", "Не смотреть", 7, epicTask,
               120000, LocalDateTime.of(2022, Month.JANUARY, 1, 0, 30));
       epicTask.addRelatedSubTasks(subTask1);
       epicTask.addRelatedSubTasks(subTask2);

       EpicTask requestedTask = taskManager.getEpicTaskByID(5);
       assertEquals(epicTask, requestedTask, "Возвращаемая задача не equals ожидаемой");

       assertThrows(NullPointerException.class, () -> taskManager.getEpicTaskByID(-1));

       taskManager.removeAllEpicTask();
       assertThrows(NullPointerException.class, () -> taskManager.getEpicTaskByID(5));
    }

    /**
     * Подготовка: создается новая подзадача, с такими же полями и связанным эпиком как в методе createInputTasks()
     * Исполнение: вызывается задача по ID
     * Проверка:
     * 1) эквивалентность объекта задачи, при вызове с корректным ID
     * 2) Выбрасывание NullPointerException при несуществующем ID -1
     * 3) Выбрасывание NullPointerException при пустом перечне задач
     */
    @Test
    void getSubTaskByID() {
       EpicTask epicTask = new EpicTask("Фальшивый эпик таск","Внутри всего один сабтаск!", 3);
       SubTask subTask = new SubTask("Одинокий сабтаск", "Один совсем один", 4, epicTask,
               480, LocalDateTime.of(2022, Month.MARCH, 31, 12, 0));
       epicTask.addRelatedSubTasks(subTask);

       SubTask requestedTask = taskManager.getSubTaskByID(4);

       assertEquals(subTask, requestedTask, "Возвращаемая задача не equals ожидаемой");
       assertThrows(NullPointerException.class, () -> taskManager.getSubTaskByID(-1));
       taskManager.removeAllSubTask();
       assertThrows(NullPointerException.class, () -> taskManager.getSubTaskByID(3));
    }

    /**
     * Подготовка: фиксируются значения полей задачи с ID 0 перед обновлением
     * Исполнение: вызывается метод обновления задачи
     * Проверка:
     * 1) ID должен не измениться
     * 2) Должно появиться UPDATED! для полей Name и Description
     * 3) Статус не должен быть равен NEW (в реализации обновления рандомно присваивается IN_PROGRESS или DONE)
     * 4) Выбрасывание NullPointerException при несуществующем ID -1
     * 5) Выбрасывание NullPointerException при пустом перечне задач
     */
    @Test
    void updateTask() {
       Task taskForUpdate = taskManager.getTaskByID(0);
       String name = taskForUpdate.getTaskName();
       String description = taskForUpdate.getDescription();
       int id = taskForUpdate.getIdentifier();
       TaskStatus status = taskForUpdate.getStatus();

       InputTask updatedInputTask = inputTaskCreator.updateTask(taskForUpdate);
       Task updatedTask = taskManager.updateTask(updatedInputTask);

       assertEquals(id, updatedTask.getIdentifier(), "ID error. After update its shouldn't change");
       assertEquals("UPDATED! " + name, updatedTask.getTaskName(), "Name error");
       assertEquals("UPDATED! " + description, updatedTask.getDescription(),
               "Description error");
       assertNotEquals(status, updatedTask.getStatus(), "After update Status should be changed");
       assertThrows(NullPointerException.class, () -> taskManager.updateTask(
               inputTaskCreator.updateTask(new Task("Левая", "Левая-левая", -1,
                       60, LocalDateTime.of(2007, Month.OCTOBER, 24, 4, 0)))));
       taskManager.removeAllTask();
       assertThrows(NullPointerException.class, () -> taskManager.updateTask(updatedInputTask));
    }

    /**
     * Подготовка: фиксируются значения полей эпика с ID 5 перед обновлением. Этот эпик включает 2 подзадачи. В текущей
     * реализации они также должны обновиться. Значения полей подзадач перед обновлением складываются в отдельные HashMap
     * Исполнение: вызывается метод обновления эпика
     * Проверка:
     * 1) ID каждой подзадачи должен не измениться
     * 2) Для каждой подзадачи должно появиться UPDATED! для полей Name и Description
     * 3) Статус каждой подзадачи не должен быть равен NEW (в реализации обновления рандомно присваивается IN_PROGRESS или DONE)
     * 4) ID эпика должен не измениться
     * 5) Должно появиться UPDATED! для полей Name и Description
     * 6) Статус не должен быть равен NEW (в реализации обновления присваивается IN_PROGRESS или DONE в зависимости от
     * статусов подзадач, проверка осуществляется в отдельном тесте)
     * 7) Выбрасывание NullPointerException при несуществующем ID -1
     * 8) Выбрасывание NullPointerException при пустом перечне задач
     */
    @Test
    void updateEpicTask() {
       EpicTask taskForUpdate = taskManager.getListOfEpics().get(5);
       String name = taskForUpdate.getTaskName();
       String description = taskForUpdate.getDescription();
       int id = taskForUpdate.getIdentifier();

       TaskStatus status = taskForUpdate.getStatus();
       HashMap<Integer, String> subNames = new HashMap<>();
       HashMap<Integer, String> subDescriptions = new HashMap<>();
       HashMap<Integer, Integer> subID = new HashMap<>();
       subNames.put(6, "Теория");
       subDescriptions.put(6, "Пройти до 17.06");
       subID.put(6, 6);
       subNames.put(7, "Аниме");
       subDescriptions.put(7, "Не смотреть");
       subID.put(7, 7);

       InputTaskEpic updatedInputTask = inputTaskCreator.updateEpicTask(taskForUpdate);
       EpicTask updatedTask = taskManager.updateEpicTask(updatedInputTask);


       for (Map.Entry<String, SubTask> entry: updatedTask.getListOfRelatedSubTasks().entrySet()) {
          int subIdentifier = entry.getValue().getIdentifier();

          assertEquals(subID.get(subIdentifier), subIdentifier, "ID subtask error after Epic Update." +
                  " Its shouldn't change");
          assertEquals("UPDATED! " + subNames.get(subIdentifier) , entry.getValue().getTaskName(),
                  "Name subtask error after Epic Update");
          assertEquals("UPDATED! " + subDescriptions.get(subIdentifier), entry.getValue().getDescription(),
                  "Description subtask error after Epic Update");
          assertNotEquals(TaskStatus.NEW, entry.getValue().getStatus(), "Subtask Status should be changed " +
                  "after Epic Update");
       }

       assertEquals(id, updatedTask.getIdentifier(), "ID error after Epic Update. Its shouldn't change");
       assertEquals("UPDATED! " + name, updatedTask.getTaskName(), "Name error after Epic Update");
       assertEquals("UPDATED! " + description, updatedTask.getDescription(),
               "Description error after Epic Update");
       assertNotEquals(status, updatedTask.getStatus(), "Status should be changed after Epic Update");
       assertThrows(NullPointerException.class, () -> taskManager.updateEpicTask(
               inputTaskCreator.updateEpicTask(new EpicTask("Левая", "Левая-левая", -1))));
       taskManager.removeAllEpicTask();
       assertThrows(NullPointerException.class, () -> taskManager.updateEpicTask(updatedInputTask));
    }

    /**
     * Подготовка: фиксируются значения полей подзадачи с ID 4 перед обновлением. Это единственная подзадача эпика с ID 3.
     * В текущей реализации обновляется статус связанного эпика и обновляется перечень подзадач
     * Исполнение: вызывается метод обновления подзадачи
     * Проверка:
     * 1) ID подзадачи должен не измениться
     * 2) Должно появиться UPDATED! для полей Name и Description
     * 3) Статус подзадачи не должен быть равен NEW (в реализации обновления рандомно присваивается IN_PROGRESS или DONE)
     * 4) Статус эпика не должен быть равен NEW (в реализации обновления присваивается IN_PROGRESS или DONE в зависимости от
     * статусов подзадач, проверка осуществляется в отдельном тесте)
     * 5) Возвращает null при запросе подзадачи из перечня подзадач эпика по ключу до обновления
     * 6) Возвращение обновленной подзадачи по обновлённому ключу
     * 7) Выбрасывание NullPointerException при несуществующем ID -1
     * 8) Выбрасывание NullPointerException при пустом перечне задач
     */
    @Test
    void updateSubTask() {
       SubTask subTaskForUpdate = taskManager.getListOfSubTasks().get(4);
       String name = subTaskForUpdate.getTaskName();
       String description = subTaskForUpdate.getDescription();
       int id = subTaskForUpdate.getIdentifier();
       TaskStatus subStatus = subTaskForUpdate.getStatus();

       EpicTask relatedEpicTask = subTaskForUpdate.getRelatedEpicTask();
       String oldKeyForSubTask = name;
       String newKeyForSubTask = "UPDATED! " + oldKeyForSubTask;
       TaskStatus epicStatus = relatedEpicTask.getStatus();

       InputSubTask updatedInputSubTask = inputTaskCreator.updateSubTask(subTaskForUpdate);
       SubTask updatedTask = taskManager.updateSubTask(updatedInputSubTask);

       assertEquals(id, updatedTask.getIdentifier(), "ID error after Epic Update. Its shouldn't change");
       assertEquals("UPDATED! " + name, updatedTask.getTaskName(), "Name error after Epic Update");
       assertEquals("UPDATED! " + description, updatedTask.getDescription(),
               "Description error after Epic Update");
       assertNotEquals(subStatus, updatedTask.getStatus(), "Status should be changed after Epic Update");
       assertNotEquals(epicStatus, relatedEpicTask.getStatus(), "Status should be changed after Epic Update");
       assertNull(relatedEpicTask.getListOfRelatedSubTasks().get(oldKeyForSubTask), "Should return null");
       assertEquals(updatedTask, relatedEpicTask.getListOfRelatedSubTasks().get(newKeyForSubTask),
               "Updated SubTask and SubTask in ListOfSubTask for related Epic should be equal");
       assertThrows(NullPointerException.class, () -> taskManager.updateSubTask(
               inputTaskCreator.updateSubTask(new SubTask("Левая", "Левая-левая", -1, relatedEpicTask,
                       100, LocalDateTime.of(1999, 12, 31, 23, 59)))));

       taskManager.removeAllSubTask();
       assertThrows(NullPointerException.class, () -> taskManager.updateSubTask(updatedInputSubTask));
    }

    /**
     * Подготовка: рассматриваем статус эпика ID 5, у него 2 подзадачи. Убеждаемся, что статусы подзадач NEW
     * Исполнение: получаем статус эпика
     * Проверка: Должен быть NEW
     *
     * Подготовка: устанавливаем статус подзадач DONE
     * Исполнение: получаем статус эпика
     * Проверка: Должен быть NEW
     *
     * Подготовка: устанавливаем статус подзадач ID 6 DONE & ID 7 NEW
     * Исполнение: получаем статус эпика
     * Проверка: Должен быть IN_PROGRESS
     *
     * Подготовка: устанавливаем статус подзадач IN_PROGRESS
     * Исполнение: получаем статус эпика
     * Проверка: Должен быть IN_PROGRESS
     *
     * Подготовка: рассматриваем статус эпика ID 5, у него 2 подзадачи. Выставляем пустой перечень подзадач.
     * Исполнение: получаем статус эпика
     * Проверка: Должен быть NEW
     *
     * При попытке установить эпику статус через setStatus() выбрасывает IllegalArgumentException
     */
    @Test
    void shouldSetDifferentStatusToEpic() {
       EpicTask epicTask = taskManager.getListOfEpics().get(5);
       SubTask subTask6 = taskManager.getListOfSubTasks().get(6);
       SubTask subTask7 = taskManager.getListOfSubTasks().get(7);

       assertEquals(TaskStatus.NEW, subTask6.getStatus(), "Should be NEW");
       assertEquals(TaskStatus.NEW, subTask7.getStatus(), "Should be NEW");
       assertEquals(TaskStatus.NEW, epicTask.getStatus(), "Should be NEW");

       subTask6.setStatus(TaskStatus.DONE);
       subTask7.setStatus(TaskStatus.DONE);
       assertEquals(TaskStatus.DONE, epicTask.getStatus(), "Should be DONE");

       subTask6.setStatus(TaskStatus.DONE);
       subTask7.setStatus(TaskStatus.NEW);
       assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus(), "Should be IN_PROGRESS");

       subTask6.setStatus(TaskStatus.IN_PROGRESS);
       subTask7.setStatus(TaskStatus.IN_PROGRESS);
       assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus(), "Should be IN_PROGRESS");

       Map<String, SubTask> unfilledListOfSubtask = new HashMap<>();
       epicTask.setListOfRelatedSubTasks(unfilledListOfSubtask);
       assertEquals(TaskStatus.NEW, epicTask.getStatus(), "Should be NEW");

       assertThrows(IllegalArgumentException.class, () -> epicTask.setStatus(TaskStatus.DONE));
       assertThrows(IllegalArgumentException.class, () -> epicTask.setStatus(TaskStatus.NEW));
       assertThrows(IllegalArgumentException.class, () -> epicTask.setStatus(TaskStatus.IN_PROGRESS));
    }

    /**
     * Подготовка: убеждаемся в существовании задачи - вызов задачи с ID(2) не возвращает null
     * Исполнение: удаляется задача по ID
     * Проверка: Выбрасывание NullPointerException при вызове задачи с ID(2) после удаления
     * При вызове метода при пустом списке или с неправильным ID ничего не происходит. Потому что если пытаешься удалить
     * из ХэшМапы по несуществующему ключу, то ничего не происходит
     */
    @Test
    void removeTaskByID() {
       assertNotNull(taskManager.getTaskByID(2), "Task for deletion" +
               " at least should exist before operation");

       taskManager.removeTaskByID(2);

       assertThrows(NullPointerException.class, () -> taskManager.getTaskByID(2));

       taskManager.removeTaskByID(-2);
       taskManager.removeAllTask();
       taskManager.removeTaskByID(2);
    }

    /**
     * Подготовка: убеждаемся в существовании эпика - вызов задачи с ID 3 не возвращает null. Этот эпик включает 1 подзадачи.
     * В текущей реализации она также должна быть удалена. Убеждаемся в существовании подзадач ID 4
     * Исполнение: удаляется эпик по ID
     * Проверка:
     * 1) Выбрасывание NullPointerException при вызове эпика с ID(3) после удаления
     * 2) Выбрасывание NullPointerException при вызове подзадачи с ID(4) после удаления эпика
     * 3) Выбрасывание NullPointerException при попытке повторно удалить эпик с ID(3) после удаления
     * 4) Выбрасывание NullPointerException при попытке эпик с некорректным ID(-3)
     * 5) Выбрасывание NullPointerException при попытке повторно удалить эпик с ID(3) при пустом перечне эпиков
     */
    @Test
    void removeEpicTaskByID() {
       assertNotNull(taskManager.getEpicTaskByID(3), "Task for deletion" +
               " at least should exist before operation");

       taskManager.removeEpicTaskByID(3);

       assertThrows(NullPointerException.class, () -> taskManager.getEpicTaskByID(3));
       assertThrows(NullPointerException.class, () -> taskManager.getSubTaskByID(4));

       assertThrows(NullPointerException.class, () -> taskManager.removeEpicTaskByID(3));
       assertThrows(NullPointerException.class, () -> taskManager.removeEpicTaskByID(-3));
       taskManager.removeAllEpicTask();
       assertThrows(NullPointerException.class, () -> taskManager.removeEpicTaskByID(3));
    }

    /**
     * Подготовка: убеждаемся в существовании подзадач. Вызов подзадач с ID 6 и 7 не возвращают null. Эти две подзадачи
     * принадлежат эпику с ID 5. В текущей реализации если удалены все подзадачи эпика, то и эпик также должен быть удален.
     * Исполнение_1: удаляется подзадача по ID 6
     * Проверка:
     * 1) Выбрасывание NullPointerException при вызове подзадачи с ID(6) после удаления
     * 2) Возвращает null при запросе подзадачи из перечня подзадач эпика
     * 3) Размер списка подзадач эпика был 2, становится 1
     * 4) Выбрасывание NullPointerException при попытке повторно удалить сабтаск с ID(6) после удаления
     * 5) Выбрасывание NullPointerException при попытке сабтаск с некорректным ID(-6)
     *
     * Исполнение_2: удаляется подзадача по ID 7
     * Проверка:
     * 1) Выбрасывание NullPointerException при вызове подзадачи с ID(7) после удаления
     * 2) Выбрасывание NullPointerException при вызове эпика с ID(5) после удаления
     * 3) Выбрасывание NullPointerException при попытке повторно удалить сабтаск с ID(3) при пустом перечне сабтасков
     */
    @Test
    void removeSubTaskByID() {
       SubTask subTask6 = taskManager.getSubTaskByID(6);
       String keyForSubTask6 = subTask6.getTaskName();
       assertNotNull(subTask6, "Task for deletion at least should exist before operation");
       taskManager.removeSubTaskByID(6);
       assertThrows(NullPointerException.class, () -> taskManager.getSubTaskByID(6));
       assertNull(subTask6.getRelatedEpicTask().getListOfRelatedSubTasks().get(keyForSubTask6), "Should return null");
       assertEquals(1, subTask6.getRelatedEpicTask().getListOfRelatedSubTasks().size(), "Size should be 1");

       assertThrows(NullPointerException.class, () -> taskManager.removeSubTaskByID(6));
       assertThrows(NullPointerException.class, () -> taskManager.removeSubTaskByID(-6));

       SubTask subTask7 = taskManager.getSubTaskByID(7);
       assertNotNull(subTask7, "Task for deletion at least should exist before operation");
       taskManager.removeSubTaskByID(7);
       assertThrows(NullPointerException.class, () -> taskManager.getSubTaskByID(7));
       assertThrows(NullPointerException.class, () -> taskManager.getEpicTaskByID(5));

       taskManager.removeAllSubTask();
       assertThrows(NullPointerException.class, () -> taskManager.removeSubTaskByID(6));
    }

    /**
     * Подготовка: убеждаемся в существовании задач. Перечень задач должен быть не пустым перед удалением.
     * Исполнение: удаляем все задачи
     * Проверка: перечень задач должен быть пуст, при повторном вызове метода при пустом списке ничего не происходит
     */
    @Test
    void removeAllTask() {
       assertFalse(taskManager.getListOfTasks().isEmpty(), "List of Tasks should be empty before deletion");
       taskManager.removeAllTask();
       assertTrue(taskManager.getListOfTasks().isEmpty(), "List of Tasks should be empty after deletion");
       taskManager.removeAllTask();
    }

    /**
     * Подготовка: убеждаемся в существовании задач. Перечень эпиков должен быть не пустым перед удалением.
     * Исполнение: удаляем все эпики
     * Проверка:
     * 1) перечень эпиков должен быть пуст
     * 2) перечень подзадач должен быть пуст
     * при повторном вызове удаления всех эпиков ничего не происходит
     */
    @Test
    void removeAllEpicTask() {
       assertFalse(taskManager.getListOfEpics().isEmpty(), "List of Epics should be empty before deletion");
       taskManager.removeAllEpicTask();
       assertTrue(taskManager.getListOfEpics().isEmpty(), "List of Epics should be empty after deletion");
       assertTrue(taskManager.getListOfSubTasks().isEmpty(), "List of SubTasks should be empty after deletion");

       taskManager.removeAllEpicTask();
    }

    /**
     * Подготовка: убеждаемся в существовании задач. Перечень подзадач должен быть не пустым перед удалением.
     * Исполнение: удаляем все подзадачи
     * Проверка:
     * 1) перечень подзадач должен быть пуст
     * 2) перечень эпиков должен быть пуст
     * при повторном вызове удаления всех сабтасков ничего не происходит
     */
    @Test
    void removeAllSubTask() {
       assertFalse(taskManager.getListOfEpics().isEmpty(), "List of SubTasks should be empty before deletion");
       taskManager.removeAllSubTask();
       assertTrue(taskManager.getListOfSubTasks().isEmpty(), "List of SubTasks should be empty after deletion");
       assertTrue(taskManager.getListOfEpics().isEmpty(), "List of Epics should be empty after deletion");

       taskManager.removeAllSubTask();
    }

    /**
     * Подготовка: создаём ещё одну задачу без пересечения, её id будет 8
     * Исполнение: вызываем getPrioritizedTasks(), добавляем ещё одну задачу, снова вызываем
     * Проверка:
     * 1) метод проверки отсортированности возврщает True
     * 2) новая задача занимает конкретную позицию в массиве из TreeSet'а
     */
    @Test
    void getPrioritizedTasks() {
       InputTask inputTask = new InputTask("Задача", "Без пересечения по времени",
               2880, LocalDateTime.of(2022, Month.FEBRUARY, 22, 3, 59));

       assertTrue(isSorted(taskManager.getPrioritizedTasks()));

       taskManager.createTask(inputTask);
       Task[] taskArray = taskManager.getPrioritizedTasks().toArray(new Task[0]);
       assertEquals(taskArray[0], taskManager.getTaskByID(8));
    }

    boolean checkUniquenessOfID() {
       boolean success = false;
       List<Integer> listOfID = new ArrayList<>();
       List<Integer> listOfDuplicates = new ArrayList<>();
       for (Map.Entry<Integer, Task> entry : taskManager.getListOfTasks().entrySet()) {
          listOfID.add(entry.getKey());
       }
       for (Map.Entry<Integer, EpicTask> entry : taskManager.getListOfEpics().entrySet()) {
          listOfID.add(entry.getKey());
       }
       for (Map.Entry<Integer, SubTask> entry : taskManager.getListOfSubTasks().entrySet()) {
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

    boolean checkTasksNamesDescriptions() {
       List<String> inputTasksNamesList = new ArrayList<>();
       List<String> inputTasksDescriptionList = new ArrayList<>();
       List<String> tasksNamesList = new ArrayList<>();
       List<String> tasksDescriptionList = new ArrayList<>();

       for (Map.Entry<Integer, Task> entry : taskManager.getListOfTasks().entrySet()) {
          tasksNamesList.add(entry.getValue().getTaskName());
          tasksDescriptionList.add(entry.getValue().getDescription());
       }
       for (Map.Entry<Integer, EpicTask> entry : taskManager.getListOfEpics().entrySet()) {
          tasksNamesList.add(entry.getValue().getTaskName());
          tasksDescriptionList.add(entry.getValue().getDescription());
       }
       for (Map.Entry<Integer, SubTask> entry : taskManager.getListOfSubTasks().entrySet()) {
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

    boolean isSorted(Set<Task> treeSet) {
       Task[] taskArray = treeSet.toArray(new Task[0]);
       for (int i = 1; i < taskArray.length; i++) {
          LocalDateTime prevFinishTime = taskArray[i - 1].getEndTime();
          LocalDateTime thisStartTime = taskArray[i].getStartTime();
          if (thisStartTime.isBefore(prevFinishTime)) return false;
       }
       return true;
    }

 }
package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import input.InputSubTask;
import input.InputTaskCreator;
import input.InputTaskEpic;
import managers.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import util.TaskCreatorForTests;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;

class HttpTaskServerTest {
    HttpTaskServer server;
    FileBackedTasksManager taskManager;
    Gson gson;

    @BeforeEach
    public void createMangersAndTasks() {
        server = new HttpTaskServer();
        taskManager = server.getFileBackedTasksManager();
        gson = server.getGson();
        TaskCreatorForTests.createTasks(taskManager);
    }

    @AfterEach
    public void stopServer() {
        server.stopServer();
    }

    /**
     * Подготовка: создание запроса списка задач
     * Исполнение: отправка запроса, преобразование тела ответа в ожидаемый тип
     * Проверка:
     * 1) Код исполнения 200
     * 2) Вернули не null
     * 3) Ожидаемый размер списка задач 3
     * 4) Такая же созданная задача с ID 0 эквивалентна задаче из переченя
     */
    @Test
    public void taskGetList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/list");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<Integer, Task>>(){}.getType();
        Map<Integer, Task> listOfTasks = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(3, listOfTasks.size());
        assertEquals(new Task("Взять чек", "Получить у босса",0, 60,
                        LocalDateTime.of(2022, Month.FEBRUARY, 24, 4, 0)), listOfTasks.get(0));

    }

    /**
     * Подготовка: создание запроса получения задачи с ID 0
     * Исполнение: отправка запроса, преобразование тела ответа в ожидаемый тип
     * Проверка:
     * 1) Код исполнения 200
     * 2) Вернули не null
     * 3) Такая же созданная задача с ID 0 эквивалентна задаче из переченя
     *
     * Отправка запроса с некорректным ID -99
     * 1) Сервер возвращает ответ "Задачи с таким ID не существует"
     * 2) Код исполнения 404
     */
    @Test
    public void taskGetByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Task>(){}.getType();
        Task task = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(task);
        assertEquals(new Task("Взять чек", "Получить у босса",0, 60,
                LocalDateTime.of(2022, Month.FEBRUARY, 24, 4, 0)), task);

        url = URI.create("http://localhost:8080/tasks/task/?id=-99");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задачи с таким ID не существует", response.body());
    }

    /**
     * Подготовка: создание запроса удаления задачи с ID 0
     * Исполнение: отправка запроса
     * Проверка:
     * 1) Код исполнения 200
     * 2) Сервер возвращает ответ "Задача успешно удалена."
     *
     * Создание запроса получения задачи с ID 0 уже после удаления
     * 1) Сервер возвращает ответ "Задачи с таким ID не существует"
     * 2) Код исполнения 404
     *
     * Запрос перечня всех задач
     * 1) Код исполнения 200
     * 2) Ожидаемый размер списка 2
     */
    @Test
    public void taskDeleteByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задача успешно удалена.", response.body());

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задача успешно удалена.", response.body());

        url = URI.create("http://localhost:8080/tasks/task/?id=0");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задачи с таким ID не существует", response.body());

        url = URI.create("http://localhost:8080/tasks/task/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<Integer, Task>>(){}.getType();
        Map<Integer, Task> listOfTasks = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(2, listOfTasks.size());
    }

    /**
     * Подготовка: создание запроса удаления всех задач
     * Исполнение: отправка запроса
     * Проверка:
     * 1) Код исполнения 200
     * 2) Сервер возвращает ответ "Все задачи удалены"
     *
     * Создание запроса получения задачи с ID 1 уже после удаления всех
     * 1) Сервер возвращает ответ "Задачи с таким ID не существует"
     * 2) Код исполнения 404
     *
     * Запрос перечня всех задач
     * 1) Код исполнения 200
     * 2) Ожидаемый размер списка 0
     */
    @Test
    public void taskDeleteAll() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/list");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertEquals("Все задачи удалены", response.body());

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertEquals("Все задачи удалены", response.body());

        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задачи с таким ID не существует", response.body());

        url = URI.create("http://localhost:8080/tasks/task/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<Integer, Task>>(){}.getType();
        Map<Integer, Task> listOfTasks = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(0, listOfTasks.size());
    }

    /**
     * Подготовка: создание запроса для создания новой задачи
     * Исполнение: отправка запроса
     * Проверка:
     * 1) Код исполнения 200
     * 2) Сервер возвращает ответ "Задача успешно создана"
     *
     * Запрос перечня всех задач
     * 1) Код исполнения 200
     * 2) не null
     * 3) ожидаемый размер 4
     * 4) созданная задача через сервер эквивалентна созданной в тесте
     */
    @Test
    public void taskPostCreate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/new");
        Task task = new Task("Task HTTP", "Description HTTP", 8, 180,
                LocalDateTime.of(2000, 2, 2, 12, 0));
        String jsonTask = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задача успешно создана", response.body());

        url = URI.create("http://localhost:8080/tasks/task/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<Integer, Task>>(){}.getType();
        Map<Integer, Task> listOfTasks = server.getGson().fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(4, listOfTasks.size());
        assertEquals(task, listOfTasks.get(8));
    }

    /**
     * Подготовка: создание запроса для обновления задачи
     * Исполнение: отправка запроса
     * Проверка:
     * 1) Код исполнения 200
     * 2) Сервер возвращает ответ "Задача успешно обновлена."
     *
     * Запрос перечня всех задач
     * 1) Код исполнения 200
     * 2) не null
     * 3) ожидаемый размер 3
     * 4) созданная задача через сервер эквивалентна созданной в тесте
     */
    @Test
    public void taskPostUpdate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        Task task = new Task("UPDATED TASK", "Description UPDATED", 1, 240,
                LocalDateTime.of(1990, 2, 2, 12, 0));
        String jsonTask = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertTrue(response.body().startsWith("Задача успешно обновлена."));

        url = URI.create("http://localhost:8080/tasks/task/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<Integer, Task>>(){}.getType();
        Map<Integer, Task> listOfTasks = server.getGson().fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(3, listOfTasks.size());
        assertEquals(task, listOfTasks.get(1));
    }

    @Test
    public void historyGetList() throws IOException, InterruptedException {
        taskManager.getTaskByID(0);
        taskManager.getEpicTaskByID(3);
        taskManager.getTaskByID(2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<List<Task>>(){}.getType();
        List<Task> history = server.getGson().fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(history);
        assertEquals(4, history.size());
    }

    @Test void prioritizedGetList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Set<Task>>(){}.getType();
        Set<Task> history = server.getGson().fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(history);
        assertEquals(6, history.size());
    }

    @Test
    public void epicGetList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/list");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<Integer, EpicTask>>(){}.getType();
        Map<Integer, EpicTask> listOfTasks = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(2, listOfTasks.size());
    }

    /**
     * Подготовка: создание запроса получения задачи с ID 3
     * Исполнение: отправка запроса, преобразование тела ответа в ожидаемый тип
     * Проверка:
     * 1) Код исполнения 200
     * 2) Вернули не null
     *
     * Отправка запроса с некорректным ID -99
     * 1) Сервер возвращает ответ "Задачи с таким ID не существует"
     * 2) Код исполнения 404
     */
    @Test
    public void epicGetByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<EpicTask>(){}.getType();
        EpicTask task = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(task);

        url = URI.create("http://localhost:8080/tasks/task/?id=-99");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задачи с таким ID не существует", response.body());
    }

    /**
     * Подготовка: создание запроса удаления задачи с ID 5
     * Исполнение: отправка запроса
     * Проверка:
     * 1) Код исполнения 200
     * 2) Сервер возвращает ответ "Задача успешно удалена."
     *
     * Создание запроса получения задачи с ID 5 уже после удаления
     * 1) Сервер возвращает ответ "Задачи с таким ID не существует"
     * 2) Код исполнения 404
     *
     * Запрос перечня всех задач
     * 1) Код исполнения 200
     * 2) Ожидаемый размер списка 1
     *
     * Запрос перечня подзадач. Удалили эпик - с ним удалились две подзадачи
     * 1) Код исполнения 200
     * 2) Ожидаемый размер списка 1
     */
    @Test
    public void epicDeleteByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задача успешно удалена.", response.body());

        url = URI.create("http://localhost:8080/tasks/epic/?id=5");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задачи с таким ID не существует", response.body());

        url = URI.create("http://localhost:8080/tasks/epic/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<Integer, EpicTask>>(){}.getType();
        Map<Integer, EpicTask> listOfTasks = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(1, listOfTasks.size());

        url = URI.create("http://localhost:8080/tasks/subtask/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        userType = new TypeToken<Map<Integer, SubTask>>(){}.getType();
        Map<Integer, SubTask> listOfSubTasks = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfSubTasks);
        assertEquals(1, listOfSubTasks.size());
    }

    /**
     * Подготовка: создание запроса удаления всех задач
     * Исполнение: отправка запроса
     * Проверка:
     * 1) Код исполнения 200
     * 2) Сервер возвращает ответ "Все задачи удалены"
     *
     * Создание запроса получения задачи с ID 1 уже после удаления всех
     * 1) Сервер возвращает ответ "Задачи с таким ID не существует"
     * 2) Код исполнения 404
     *
     * Запрос перечня всех задач
     * 1) Код исполнения 200
     * 2) Ожидаемый размер списка 0
     *
     * Запрос перечня всех подзадач
     * 1) Код исполнения 200
     * 2) Ожидаемый размер списка 0
     */
    @Test
    public void epicDeleteAll() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/list");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertEquals("Все задачи удалены", response.body());

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertEquals("Все задачи удалены", response.body());

        url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задачи с таким ID не существует", response.body());

        url = URI.create("http://localhost:8080/tasks/epic/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<Integer, EpicTask>>(){}.getType();
        Map<Integer, EpicTask> listOfTasks = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(0, listOfTasks.size());

        url = URI.create("http://localhost:8080/tasks/subtask/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        userType = new TypeToken<Map<Integer, SubTask>>(){}.getType();
        Map<Integer, SubTask> listOfSubTasks = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(0, listOfSubTasks.size());
    }

    /**
     * Подготовка: создание запроса для создания новой задачи
     * Исполнение: отправка запроса
     * Проверка:
     * 1) Код исполнения 200
     * 2) Сервер возвращает ответ "Задача успешно создана"
     *
     * Запрос перечня всех задач
     * 1) Код исполнения 200
     * 2) не null
     * 3) ожидаемый размер 3
     *
     * Запрос перечня всех подзадач:
     * 1) Код исполнения 200
     * 2) не null
     * 3) ожидаемый размер 4
     */
    @Test
    public void epicPostCreate() throws IOException, InterruptedException {
        InputTaskCreator inputTaskCreator = new InputTaskCreator();
        InputTaskEpic inputTaskEpic1 = inputTaskCreator.createEpicTask("Фальшивый эпик таск",
                "Внутри всего один сабтаск!");
        InputSubTask inputSubTask1 = inputTaskCreator.createInputSubTask("Одинокий сабтаск","Один совсем один",
                480, LocalDateTime.of(2015, Month.MARCH, 31, 12, 0));
        inputTaskCreator.putSubTaskInEpic(inputSubTask1, inputTaskEpic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/new");
        String jsonTask = gson.toJson(inputTaskEpic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertEquals("Задача успешно создана", response.body());

        url = URI.create("http://localhost:8080/tasks/epic/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<Map<Integer, EpicTask>>(){}.getType();
        Map<Integer, EpicTask> listOfTasks = server.getGson().fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(3, listOfTasks.size());

        url = URI.create("http://localhost:8080/tasks/subtask/list");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        userType = new TypeToken<Map<Integer, SubTask>>(){}.getType();
        Map<Integer, EpicTask> listOfSubTasks = server.getGson().fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(listOfTasks);
        assertEquals(4, listOfSubTasks.size());
    }

    /**
     * Подготовка: создание запроса для обновления задачи
     * Исполнение: отправка запроса
     * Проверка:
     * 1) Код исполнения 200
     * 2) Сервер возвращает ответ "Задача успешно обновлена."
     *
     * Запрос перечня всех задач
     * 1) Код исполнения 200
     * 2) не null
     * 3) ожидаемый размер 3
     * 4) созданная задача через сервер эквивалентна созданной в тесте
     */
    @Test
    public void epicPostUpdate() throws IOException, InterruptedException {
        InputTaskCreator inputTaskCreator = new InputTaskCreator();
        InputTaskEpic inputTaskEpic1 = inputTaskCreator.createEpicTask("UPDATE",
                "UPDATE DESCRR");
        InputSubTask inputSubTask1 = inputTaskCreator.createInputSubTask("UPDATE RR", "UP",
                600, LocalDateTime.of(2011, Month.MARCH, 31, 12, 0));
        inputSubTask1.setStatus(TaskStatus.DONE);
        inputTaskEpic1.setIdentifier(3);
        inputSubTask1.setIdentifier(4);
        inputTaskCreator.putSubTaskInEpic(inputSubTask1, inputTaskEpic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        String jsonTask = gson.toJson(inputTaskEpic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertTrue(response.body().startsWith("Задача успешно обновлена."));

        url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type userType = new TypeToken<EpicTask>() {
        }.getType();
        EpicTask task = gson.fromJson(response.body(), userType);

        assertEquals(200, response.statusCode(), "Код статуса некорректный");
        assertNotNull(task);
        assertEquals("UPDATE", task.getTaskName());
        assertEquals("UPDATE DESCRR", task.getDescription());
    }
}
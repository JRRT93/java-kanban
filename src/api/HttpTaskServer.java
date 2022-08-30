package api;

import com.google.gson.Gson;

import com.sun.net.httpserver.HttpServer;
import input.InputSubTask;
import input.InputTask;
import input.InputTaskEpic;
import managers.FileBackedTasksManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import util.DefaultGSON;
import util.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private FileBackedTasksManager fileBackedTasksManager;
    private HttpServer server;
    private Gson gson;

    HttpTaskServer() {
        fileBackedTasksManager = Managers.getFileBackedManagerByPath("resources\\managerDataHTTP.csv");
        gson = DefaultGSON.setGSONOptions();
        startServer();
    }

    private void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        createTaskContext();
        createEpicTaskContext();
        createSubTaskContext();
        createHistoryContext();
        createPrioritizedContext();

        server.start();
    }

    public void stopServer() {
        server.stop(0);
    }

    public Gson getGson() {
        return gson;
    }

    private void createTaskContext() {
        server.createContext("/tasks/task", exchange -> {
            URI requestURI = exchange.getRequestURI();
            String query = requestURI.getQuery();
            String path = requestURI.getPath();
            System.out.println("Началась обработка " + path + " запроса от клиента.");

            String[] splitPath = path.split("/");

            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), CHARSET);

            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case ("GET"):
                    if (query != null) {
                        String[] splitQuery = query.split("=");
                        int id = Integer.parseInt(splitQuery[1]);
                        String response;
                        try {
                            Task task = fileBackedTasksManager.getTaskByID(id);
                            response = gson.toJson(task);
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException exception) {
                            response = "Задачи с таким ID не существует";
                            exchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }

                    if (splitPath.length > 3 && splitPath[3].equals("list")) {
                        String response = gson.toJson(fileBackedTasksManager.getListOfTasks());
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                    break;

                case ("POST"):
                    System.out.println("Тело запроса:\n" + body);
                    if (splitPath.length > 3 && splitPath[3].equals("new")) {
                        try {
                            InputTask inputTask = gson.fromJson(body, InputTask.class);
                            boolean fail = false;
                            try {
                                fileBackedTasksManager.createTask(inputTask);
                            } catch (IllegalArgumentException exception) {
                                fail = true;
                            }
                            if (fail) {
                                exchange.sendResponseHeaders(400, 0);
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write("Ошибка при создании задачи. Пересечение дат выполнения".getBytes());
                                }
                            } else {
                                exchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write("Задача успешно создана".getBytes());
                                }
                            }
                        } catch (IllegalArgumentException exception) {
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(("Ошибка при создании задачи: " + exception.getMessage()).getBytes());
                            }
                        }
                    }
                    if (query != null) {
                        String response;
                        try {
                            InputTask updatedInputTask = gson.fromJson(body, InputTask.class);
                            Task task = fileBackedTasksManager.getListOfTasks().get(updatedInputTask.getIdentifier());
                            String beforeUpdate = gson.toJson(task);
                            task = fileBackedTasksManager.updateTask(updatedInputTask);
                            System.out.println(task);
                            String afterUpdate = gson.toJson(task);
                            response = "Задача успешно обновлена. Задача перед обновлением: \n" + beforeUpdate +
                                    "Задача после обновления: \n" + afterUpdate;
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException exception) {
                            response = "Задачи с таким ID не существует";
                            exchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }
                    break;

                case ("DELETE"):
                    if (splitPath.length > 3 && splitPath[3].equals("list")) {
                        fileBackedTasksManager.removeAllTask();
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write("Все задачи удалены".getBytes());
                        }
                    }
                    if (query != null) {
                        String[] splitQuery = query.split("=");
                        int id = Integer.parseInt(splitQuery[1]);
                        String response;
                        try {
                            fileBackedTasksManager.removeTaskByID(id);
                            response = "Задача успешно удалена.";
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException exception) {
                            response = "Задачи с таким ID не существует";
                            exchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("Данный запрос не обрабатывается в этом api".getBytes());
                    }
            }
        });
    }

    private void createEpicTaskContext() {
        server.createContext("/tasks/epic", exchange -> {
            URI requestURI = exchange.getRequestURI();
            String query = requestURI.getQuery();
            String path = requestURI.getPath();
            System.out.println("Началась обработка " + path + " запроса от клиента.");

            String[] splitPath = path.split("/");

            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), CHARSET);

            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case ("GET"):
                    if (query != null) {
                        String[] splitQuery = query.split("=");
                        int id = Integer.parseInt(splitQuery[1]);
                        String response;
                        try {
                            EpicTask task = fileBackedTasksManager.getEpicTaskByID(id);
                            response = gson.toJson(task);
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException exception) {
                            response = "Задачи с таким ID не существует";
                            exchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }

                    if (splitPath.length > 3 && splitPath[3].equals("list")) {
                        String response = gson.toJson(fileBackedTasksManager.getListOfEpics());
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                    break;

                case ("POST"):
                    System.out.println("Тело запроса:\n" + body);
                    if (splitPath.length > 3 && splitPath[3].equals("new")) {
                        InputTaskEpic inputTask = gson.fromJson(body, InputTaskEpic.class);
                        System.out.println(inputTask);
                        fileBackedTasksManager.createEpicTask(inputTask);
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write("Задача успешно создана".getBytes());
                        }
                    }
                    if (query != null) {
                        String response;
                        try {
                            InputTaskEpic updatedInputTask = gson.fromJson(body, InputTaskEpic.class);
                            EpicTask task = fileBackedTasksManager.getListOfEpics().get(updatedInputTask.getIdentifier());
                            String beforeUpdate = gson.toJson(task);
                            task = fileBackedTasksManager.updateEpicTask(updatedInputTask);
                            String afterUpdate = gson.toJson(task);
                            response = "Задача успешно обновлена. Задача перед обновлением: \n" + beforeUpdate +
                                    "Задача после обновления: \n" + afterUpdate;
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException exception) {
                            response = "Задачи с таким ID не существует";
                            exchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }
                    break;

                case ("DELETE"):
                    if (splitPath.length > 3 && splitPath[3].equals("list")) {
                        fileBackedTasksManager.removeAllEpicTask();
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write("Все задачи удалены".getBytes());
                        }
                    }
                    if (query != null) {
                        String[] splitQuery = query.split("=");
                        int id = Integer.parseInt(splitQuery[1]);
                        String response;
                        try {
                            fileBackedTasksManager.removeEpicTaskByID(id);
                            response = "Задача успешно удалена.";
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException exception) {
                            response = "Задачи с таким ID не существует";
                            exchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("Данный запрос не обрабатывается в этом api".getBytes());
                    }
            }
        });
    }

    private void createSubTaskContext() {
        server.createContext("/tasks/subtask", exchange -> {
            URI requestURI = exchange.getRequestURI();
            String query = requestURI.getQuery();
            String path = requestURI.getPath();
            System.out.println("Началась обработка " + path + " запроса от клиента.");

            String[] splitPath = path.split("/");

            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), CHARSET);

            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case ("GET"):
                    if (query != null) {
                        String[] splitQuery = query.split("=");
                        int id = Integer.parseInt(splitQuery[1]);
                        String response;
                        try {
                            SubTask task = fileBackedTasksManager.getSubTaskByID(id);
                            response = gson.toJson(task);
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException exception) {
                            response = "Задачи с таким ID не существует";
                            exchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }

                    if (splitPath.length > 3 && splitPath[3].equals("list")) {
                        String response = gson.toJson(fileBackedTasksManager.getListOfSubTasks());
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                    break;

                case ("POST"):
                    if (query != null) {
                        String response;
                        try {
                            InputSubTask updatedInputTask = gson.fromJson(body, InputSubTask.class);
                            SubTask task = fileBackedTasksManager.getListOfSubTasks().get(updatedInputTask.getIdentifier());
                            String beforeUpdate = gson.toJson(task);
                            task = fileBackedTasksManager.updateSubTask(updatedInputTask);
                            String afterUpdate = gson.toJson(task);
                            response = "Задача успешно обновлена. Задача перед обновлением: \n" + beforeUpdate +
                                    "Задача после обновления: \n" + afterUpdate;
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException exception) {
                            response = "Задачи с таким ID не существует";
                            exchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }
                    break;

                case ("DELETE"):
                    if (splitPath.length > 3 && splitPath[3].equals("list")) {
                        fileBackedTasksManager.removeAllSubTask();
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write("Все задачи удалены".getBytes());
                        }
                    }
                    if (query != null) {
                        String[] splitQuery = query.split("=");
                        int id = Integer.parseInt(splitQuery[1]);
                        String response;
                        try {
                            fileBackedTasksManager.removeSubTaskByID(id);
                            response = "Задача успешно удалена.";
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException exception) {
                            response = "Задачи с таким ID не существует";
                            exchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("Данный запрос не обрабатывается в этом api".getBytes());
                    }
            }
        });
    }

    private void createHistoryContext() {
        server.createContext("/tasks/history", exchange -> {
            String requestMethod = exchange.getRequestMethod();
            if ("GET".equals(requestMethod)) {
                String response = gson.toJson(fileBackedTasksManager.getHistoryManager().getHistory());
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("Данный запрос не обрабатывается в этом api".getBytes());
                }
            }
        });
    }

    private void createPrioritizedContext() {
        server.createContext("/tasks/prioritized", exchange -> {
            String requestMethod = exchange.getRequestMethod();
            if ("GET".equals(requestMethod)) {
                String response = gson.toJson(fileBackedTasksManager.getPrioritizedTasks());
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("Данный запрос не обрабатывается в этом api".getBytes());
                }
            }
        });
    }

    public FileBackedTasksManager getFileBackedTasksManager() {
        return fileBackedTasksManager;
    }
}

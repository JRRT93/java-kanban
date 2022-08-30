package managers;

import api.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import util.DefaultGSON;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient taskClient;
    private final Gson gson;
    private final Type taskUserType = new TypeToken<Map<Integer, Task>>() {
    }.getType();
    private final Type epicTaskUserType = new TypeToken<Map<Integer, EpicTask>>() {
    }.getType();
    private final Type subTaskUserType = new TypeToken<Map<Integer, SubTask>>() {
    }.getType();
    private final Type historyUserType = new TypeToken<List<Task>>() {
    }.getType();

    public HTTPTaskManager(URL url) {
        super("resources\\HTTPTaskManager\\managerData.csv");
        taskClient = new KVTaskClient(url);
        gson = DefaultGSON.setGSONOptions();
    }

    @Override
    public void saveFile(String name) {
        String tasks = gson.toJson(listOfTasks, taskUserType);
        String epicTasks = gson.toJson(listOfEpics, epicTaskUserType);
        String subTasks = gson.toJson(listOfSubTasks, subTaskUserType);
        String history = gson.toJson(historyManager.getHistory(), historyUserType);

        try {
            taskClient.put(name + "tasks", tasks);
            taskClient.put(name + "epicTasks", epicTasks);
            taskClient.put(name + "subTasks", subTasks);
            taskClient.put(name + "history", history);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadFromFile(String name) {
        removeAllTask();
        removeAllEpicTask();
        try {
            listOfTasks = gson.fromJson(taskClient.load(name + "tasks"), taskUserType);
            listOfEpics = gson.fromJson(taskClient.load(name + "epicTasks"), epicTaskUserType);
            listOfSubTasks = gson.fromJson(taskClient.load(name + "subTasks"), subTaskUserType);
            List<Task> history = gson.fromJson(taskClient.load(name + "history"), historyUserType);
            for (Task task : history) {
                if (listOfTasks.containsValue(task)) {
                    historyManager.add(task);
                } else if (listOfEpics.containsKey(task.getIdentifier())) {
                    historyManager.add(task);
                } else {
                    historyManager.add(task);
                }
            }
            } catch(IOException | InterruptedException e){
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

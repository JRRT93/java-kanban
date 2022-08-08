package managers;

import customExceptions.ManagerSaveException;
import input.InputSubTask;
import input.InputTask;
import input.InputTaskEpic;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static Path mainManagerFile;
    private static Path temporaryFile;

    public FileBackedTasksManager(String relativePath) {
        Path file = Paths.get(relativePath);
        Path temporary = file.resolveSibling("temporaryHistory.csv");
        try {
            mainManagerFile = Files.createFile(file);
        } catch (IOException exception) {
            mainManagerFile = file;
        }
        try {
            temporaryFile = Files.createFile(file.resolveSibling("temporaryHistory.csv"));
        } catch (IOException exception) {
            temporaryFile = temporary;
        }
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(mainManagerFile.toString(), StandardCharsets.UTF_8, false)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task: listOfTasks.values()) {
                fileWriter.write(taskToString(task) + "\n");
            }
            for (EpicTask epicTask: listOfEpics.values()) {
                fileWriter.write(taskToString(epicTask) + "\n");
            }
            for (SubTask subTask: listOfSubTasks.values()) {
                fileWriter.write(taskToString(subTask) + "\n");
            }
            if (!(listOfTasks.isEmpty() && listOfEpics.isEmpty() && listOfSubTasks.isEmpty())) {
                fileWriter.write("\n" + makeHistoryString());
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при работе с файлами, вы нарочно вызвали это исключение");
        }
    }

    private void saveTemporary(List<Task> taskList) {
        try (FileWriter fileWriter = new FileWriter(temporaryFile.toString(),
                StandardCharsets.UTF_8, false)) {
            for (int i = 0; i < taskList.size(); i++) {
                if (i == taskList.size() - 1) {
                    fileWriter.write(String.format("%d", taskList.get(i).getIdentifier()));
                } else {
                    fileWriter.write(String.format("%d,", taskList.get(i).getIdentifier()));
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при работе с файлами, вы нарочно вызвали это исключение");
        }
    }

    private String taskToString (Task task) {
        String type = task.getClass().getSimpleName();

        if (type.equals("Task")) {
            return String.format("%s,%s,%s,%s,%s,%s", task.getIdentifier(), type, task.getTaskName(),
                    task.getStatus(), task.getDescription(), "");
        } else if (type.equals("EpicTask")) {
            return String.format("%s,%s,%s,%s,%s,%s", task.getIdentifier(), type, task.getTaskName(),
                    task.getStatus(), task.getDescription(), "");
        } else {
            SubTask subTask = (SubTask)task;
            return String.format("%s,%s,%s,%s,%s,%s", task.getIdentifier(), type, task.getTaskName(),
                    task.getStatus(), task.getDescription(), subTask.getRelatedEpicTask().getIdentifier());
        }
    }

    public static String[] loadFromFile() {
        List<String> readedLines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("resources\\07 После обновления " +
                "эпика. Перед очисткой.csv", StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String property = bufferedReader.readLine();
                readedLines.add(property);
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Жесть какая-то");
        }
        return readedLines.toArray(new String[0]);
    }

    public void restoreTasks(String[] readedLines, HistoryManager inMemoryTaskManager) {
        List<Integer> history = new ArrayList<>();
        for (String line : readedLines) {
            if (!line.isEmpty()) {
                String[] properties = line.split(",");
                if (properties[1].equals("Task") || properties[1].equals("EpicTask") || properties[1].equals("SubTask")) {
                    switch (properties[1]) {
                        case "Task":
                            Task task = stringToTask(properties);
                            listOfTasks.put(task.getIdentifier(), task);
                            break;
                        case "EpicTask":
                            EpicTask epicTask = stringToEpic(properties);
                            listOfEpics.put(epicTask.getIdentifier(), epicTask);
                            break;
                        case "SubTask":
                            SubTask subTask = stringToSub(properties);
                            listOfSubTasks.put(subTask.getIdentifier(), subTask);
                            break;
                    }
                } else if (!properties[0].equals("id")) {
                    for (String id: properties) {
                        history.add(Integer.parseInt(id));
                    }
                }
            }
        }
        restoreAdditionalConnections(readedLines);
        for (int id: history) {
            if (listOfTasks.containsKey(id)) {
                inMemoryTaskManager.add(listOfTasks.get(id));
            } else if (listOfEpics.containsKey(id)) {
                inMemoryTaskManager.add(listOfEpics.get(id));
            } else {
                inMemoryTaskManager.add(listOfSubTasks.get(id));
            }
        }
    }

    private Task stringToTask(String[] properties) {
        Task task = new Task(properties[2], properties[4], Integer.parseInt(properties[0]));
        task.setStatus(defineLoadedStatus(properties[3]));
        return task;
    }

    private EpicTask stringToEpic(String[] properties) {
        EpicTask epicTask = new EpicTask(properties[2], properties[4], Integer.parseInt(properties[0]));
        epicTask.setStatus(defineLoadedStatus(properties[3]));
        return epicTask;
    }

    private SubTask stringToSub(String[] properties) {
        SubTask subTask = new SubTask(properties[2], properties[4],
                Integer.parseInt(properties[0]), null);
        subTask.setStatus(defineLoadedStatus(properties[3]));
        return subTask;
    }

    private TaskStatus defineLoadedStatus (String status) {
        switch (status) {
            case "NEW":
                return TaskStatus.NEW;
            case "IN_PROGRESS":
                return TaskStatus.IN_PROGRESS;
            case "DONE":
                return TaskStatus.DONE;
            default:
                return null;
        }
    }

    private void restoreAdditionalConnections(String[] readedLines) {

        for (String line : readedLines) {
            if (!line.isEmpty()) {
                String[] properties = line.split(",");
                if (properties[1].equals("SubTask")) {
                    SubTask subTask = listOfSubTasks.get(Integer.parseInt(properties[0]));
                    EpicTask epicTask = listOfEpics.get(Integer.parseInt(properties[5]));
                    subTask.setRelatedEpicTask(epicTask);
                    epicTask.addRelatedSubTasks(subTask);
                }
            }
        }
    }

    private String makeHistoryString() {
        String viewHistory = "ОШИБКА ОШИБКА НИЧЕ НЕ РАБОТАЕТ";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(temporaryFile.toString(),
                StandardCharsets.UTF_8))) {
            if (!bufferedReader.ready()) return "";
            while(bufferedReader.ready()) {
                viewHistory = bufferedReader.readLine();
            }
        } catch (IOException exception) {
            System.out.println("Ошибка при чтении временного файла");
            exception.getStackTrace();
        }
        return viewHistory;
    }

    @Override
    public Task createTask(InputTask frontendInputTask) {
        Task task = super.createTask(frontendInputTask);
        save();
        return task;
    }

    @Override
    public EpicTask createEpicTask(InputTaskEpic frontendTaskEpic) {
        EpicTask epicTask = super.createEpicTask(frontendTaskEpic);
        save();
        return epicTask;
    }

    @Override
    public SubTask createSubTask(InputSubTask frontendInputSubTask, EpicTask relatedEpicTask) {
        SubTask subTask = super.createSubTask(frontendInputSubTask, relatedEpicTask);
        save();
        return subTask;
    }

    @Override
    public Task updateTask(InputTask updatedInputTask) {
        Task task = super.updateTask(updatedInputTask);
        save();
        return task;
    }

    @Override
    public EpicTask updateEpicTask(InputTaskEpic updatedInputTaskEpic) {
        EpicTask epicTask = super.updateEpicTask(updatedInputTaskEpic);
        save();
        return epicTask;
    }

    @Override
    public SubTask updateSubTask(InputSubTask updatedInputSubTask) {
        SubTask subTask = super.updateSubTask(updatedInputSubTask);
        save();
        return subTask;
    }

    @Override
    public Task getTaskByID(int identifier, HistoryManager historyManager) {
        Task task = super.getTaskByID(identifier, historyManager);
        saveTemporary(historyManager.getHistory());
        save();
        return task;
    }

    @Override
    public EpicTask getEpicTaskByID(int identifier, HistoryManager historyManager) {
        EpicTask epicTask = super.getEpicTaskByID(identifier, historyManager);
        saveTemporary(historyManager.getHistory());
        save();
        return epicTask;
    }

    @Override
    public SubTask getSubTaskByID(int identifier, HistoryManager historyManager) {
        SubTask subTask = super.getSubTaskByID(identifier, historyManager);
        saveTemporary(historyManager.getHistory());
        save();
        return subTask;
    }

    @Override
    public void removeTaskByID(int identifier, HistoryManager historyManager) {
        super.removeTaskByID(identifier, historyManager);
        saveTemporary(historyManager.getHistory());
        save();
    }

    @Override
    public void removeEpicTaskByID(int identifier, HistoryManager historyManager) {
        super.removeEpicTaskByID(identifier, historyManager);
        saveTemporary(historyManager.getHistory());
        save();
    }

    @Override
    public void removeSubTaskByID(int identifier, HistoryManager historyManager) {
        super.removeSubTaskByID(identifier, historyManager);
        saveTemporary(historyManager.getHistory());
        save();
    }

    @Override
    public void removeAllTask(HistoryManager historyManager) {
        super.removeAllTask(historyManager);
        saveTemporary(historyManager.getHistory());
        save();
    }

    public void fakeReload() {
        save();
    }
}
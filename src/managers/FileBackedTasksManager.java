package managers;

import customExceptions.ManagerSaveException;
import input.InputSubTask;
import input.InputTask;
import input.InputTaskEpic;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import util.DefaultFormatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static Path mainManagerFile;
    private static String folder;

    public FileBackedTasksManager(String relativePath) {
        Path file = Paths.get(relativePath);
        folder = file.getParent().toString();
        try {
            mainManagerFile = Files.createFile(file);
        } catch (IOException exception) {
            mainManagerFile = file;
        }
    }

    public void saveFile(String name) {
        try {
            Files.copy(mainManagerFile, mainManagerFile.resolveSibling(name + ".csv"));
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(mainManagerFile.resolveSibling(name + ".csv"));
                Files.copy(mainManagerFile, mainManagerFile.resolveSibling(name + ".csv"));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при работе с файлами, вы нарочно вызвали это исключение");
            }
        }
    }

    public void loadFromFile(String name) {
        List<String> readedLines = new ArrayList<>();
        try (BufferedReader bufferedReader =
                     new BufferedReader(new FileReader(folder + "\\" + name + ".csv", StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String property = bufferedReader.readLine();
                readedLines.add(property);
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Жесть какая-то");
        }
        restoreTasks(readedLines.toArray(new String[0]));
    }

    private void updateMainManagerFile() {
        try (FileWriter fileWriter = new FileWriter(mainManagerFile.toString(), StandardCharsets.UTF_8, false)) {
            fileWriter.write("id,type,name,status,description,startTime,endTime,duration,epic\n");
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
                List<Integer> historyAsID = new ArrayList<>();
                StringBuilder history = new StringBuilder();
                historyManager.getHistory().stream()
                        .mapToInt(Task::getIdentifier)
                        .forEach(historyAsID::add);
                if (!historyAsID.isEmpty()) {
                    history.append(historyAsID.get(0));
                    for (int i = 1; i < historyAsID.size(); i++) {
                        history.append("," + historyAsID.get(i));
                    }
                }
                fileWriter.write("\n" + history);

            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при работе с файлами, вы нарочно вызвали это исключение");
        }
    }

    private String taskToString (Task task) {
        String type = task.getClass().getSimpleName();

        if (type.equals("Task")) {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", task.getIdentifier(), type, task.getTaskName(),
                    task.getStatus(), task.getDescription(), task.getStartTime().format(DefaultFormatter.FORMATTER),
                    task.getEndTime().format(DefaultFormatter.FORMATTER), task.getDuration().toMinutes(), "");
        } else if (type.equals("EpicTask")) {
            String startTime;
            String endTime;
            long duration;
            if(task.getStartTime() == null) {
                startTime = null;
            } else {
                startTime = task.getStartTime().format(DefaultFormatter.FORMATTER);
            }
            if(task.getEndTime() == null) {
                endTime = null;
            } else {
                endTime = task.getEndTime().format(DefaultFormatter.FORMATTER);
            }
            if(task.getDuration() == null) {
                duration = 0;
            } else {
                duration = task.getDuration().toMinutes();
            }
            return String.format("%s,%s,%s,%s,%s,%s,%s,%d,%s", task.getIdentifier(), type, task.getTaskName(),
                    task.getStatus(), task.getDescription(), startTime, endTime, duration, "");
        } else {
            SubTask subTask = (SubTask)task;
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", task.getIdentifier(), type, task.getTaskName(),
                    task.getStatus(), task.getDescription(), task.getStartTime().format(DefaultFormatter.FORMATTER),
                    task.getEndTime().format(DefaultFormatter.FORMATTER), task.getDuration().toMinutes(),
                    subTask.getRelatedEpicTask().getIdentifier());
        }
    }

    private void restoreTasks(String[] readedLines) {
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
                historyManager.add(listOfTasks.get(id));
            } else if (listOfEpics.containsKey(id)) {
                historyManager.add(listOfEpics.get(id));
            } else {
                historyManager.add(listOfSubTasks.get(id));
            }
        }
        updateMainManagerFile();
    }

    private Task stringToTask(String[] properties) {
        String name = properties[2];
        String description = properties[4];
        int id = Integer.parseInt(properties[0]);
        LocalDateTime startTime = LocalDateTime.parse(properties[5], DefaultFormatter.FORMATTER);
        long minutes = Long.parseLong(properties[7]);

        Task task = new Task(name, description, id, minutes, startTime);
        task.setStatus(defineLoadedStatus(properties[3]));
        return task;
    }

    private EpicTask stringToEpic(String[] properties) {
        String name = properties[2];
        String description = properties[4];
        int id = Integer.parseInt(properties[0]);
        EpicTask epicTask = new EpicTask(name, description, id);
        epicTask.updateEpicTaskStatus();
        return epicTask;
    }

    private SubTask stringToSub(String[] properties) {
        String name = properties[2];
        String description = properties[4];
        int id = Integer.parseInt(properties[0]);
        LocalDateTime startTime = LocalDateTime.parse(properties[5], DefaultFormatter.FORMATTER);
        long minutes = Long.parseLong(properties[7]);
        SubTask subTask = new SubTask(name, description, id, null, minutes, startTime);
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
                    EpicTask epicTask = listOfEpics.get(Integer.parseInt(properties[8]));
                    subTask.setRelatedEpicTask(epicTask);
                    epicTask.addRelatedSubTasks(subTask);
                }
            }
        }
    }

    @Override
    public Task createTask(InputTask frontendInputTask) {
        Task task = super.createTask(frontendInputTask);
        updateMainManagerFile();
        return task;
    }

    @Override
    public EpicTask createEpicTask(InputTaskEpic frontendTaskEpic) {
        EpicTask epicTask = super.createEpicTask(frontendTaskEpic);
        updateMainManagerFile();
        return epicTask;
    }

    @Override
    public SubTask createSubTask(InputSubTask frontendInputSubTask, EpicTask relatedEpicTask) {
        SubTask subTask = super.createSubTask(frontendInputSubTask, relatedEpicTask);
        updateMainManagerFile();
        return subTask;
    }

    @Override
    public Task updateTask(InputTask updatedInputTask) {
        Task task = super.updateTask(updatedInputTask);
        updateMainManagerFile();
        return task;
    }

    @Override
    public EpicTask updateEpicTask(InputTaskEpic updatedInputTaskEpic) {
        EpicTask epicTask = super.updateEpicTask(updatedInputTaskEpic);
        updateMainManagerFile();
        return epicTask;
    }

    @Override
    public SubTask updateSubTask(InputSubTask updatedInputSubTask) throws IllegalArgumentException {
        SubTask subTask = super.updateSubTask(updatedInputSubTask);
        updateMainManagerFile();
        return subTask;
    }

    @Override
    public Task getTaskByID(int identifier) {
        Task task = super.getTaskByID(identifier);
        updateMainManagerFile();
        return task;
    }

    @Override
    public EpicTask getEpicTaskByID(int identifier) {
        EpicTask epicTask = super.getEpicTaskByID(identifier);
        updateMainManagerFile();
        return epicTask;
    }

    @Override
    public SubTask getSubTaskByID(int identifier) {
        SubTask subTask = super.getSubTaskByID(identifier);
        updateMainManagerFile();
        return subTask;
    }

    @Override
    public void removeTaskByID(int identifier) {
        super.removeTaskByID(identifier);
        updateMainManagerFile();
    }

    @Override
    public void removeEpicTaskByID(int identifier) {
        super.removeEpicTaskByID(identifier);
        updateMainManagerFile();
    }

    @Override
    public void removeSubTaskByID(int identifier) {
        super.removeSubTaskByID(identifier);
        updateMainManagerFile();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        updateMainManagerFile();
    }
}
package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class EpicTask extends Task {
    protected Map<String, SubTask> listOfRelatedSubTasks;

    public EpicTask(String taskName, String description, int identifier) {
        super(taskName, description, identifier);
        this.listOfRelatedSubTasks = new HashMap<>();
    }

    public Map<String, SubTask> getListOfRelatedSubTasks() {
        return listOfRelatedSubTasks;
    }

    public void setListOfRelatedSubTasks(Map<String, SubTask> listOfRelatedSubTasks) {
        this.listOfRelatedSubTasks = listOfRelatedSubTasks;
        this.updateEpicTaskStatus();
        this.initializeStartTimeAndDuration();
    }

    public void addRelatedSubTasks(SubTask subTask) {
        listOfRelatedSubTasks.put(subTask.getTaskName(), subTask);
        this.updateEpicTaskStatus();
        this.initializeStartTimeAndDuration();
    }

    @Override
    public LocalDateTime getEndTime() {
        if(duration != null && startTime != null) return startTime.plus(duration);
        return null;
    }

    private void initializeStartTimeAndDuration() {
        if (!listOfRelatedSubTasks.isEmpty()) {
            String firstKey = null;
            for (Map.Entry<String, SubTask> entry : listOfRelatedSubTasks.entrySet()) {
                firstKey = entry.getKey();
            }
            LocalDateTime earliestStart = listOfRelatedSubTasks.get(firstKey).getStartTime();
            LocalDateTime latestFinish = listOfRelatedSubTasks.get(firstKey).getEndTime();

            for (Map.Entry<String, SubTask> entry : listOfRelatedSubTasks.entrySet()) {
                if (earliestStart.isAfter(entry.getValue().getStartTime()))
                    earliestStart = entry.getValue().getStartTime();
                if (latestFinish.isBefore(entry.getValue().getEndTime())) latestFinish = entry.getValue().getEndTime();
            }

            this.startTime = earliestStart;
            this.duration = Duration.between(earliestStart, latestFinish);
        } else {
            this.startTime = null;
            this.duration = null;
        }
    }

    @Override
    public void setStatus(TaskStatus status) {
        throw new IllegalArgumentException("Статус эпической задачи не может быть установлен в ручном режиме. " +
                "Статус определяется автоматически");
    }

    public void updateEpicTaskStatus() {
        if (listOfRelatedSubTasks.isEmpty()) {
            this.status = TaskStatus.NEW;
        } else {
            List<TaskStatus> subTasksStatus = new ArrayList<>();
            for (Map.Entry<String, SubTask> entry : listOfRelatedSubTasks.entrySet()) {
                subTasksStatus.add(entry.getValue().getStatus());
            }
            if (!subTasksStatus.contains(TaskStatus.DONE) && !subTasksStatus.contains(TaskStatus.IN_PROGRESS)) {
                this.status = TaskStatus.NEW;
            } else if (!subTasksStatus.contains(TaskStatus.NEW) && !subTasksStatus.contains(TaskStatus.IN_PROGRESS)) {
                this.status = TaskStatus.DONE;
            } else {
                this.status = TaskStatus.IN_PROGRESS;
            }
        }
    }

    @Override
    public String toString() {
        return  "EpicTask{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", identifier='" + identifier + '\'' +
                ", Количество подзадач='" + listOfRelatedSubTasks.size() + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EpicTask epicTask = (EpicTask) o;

        return listOfRelatedSubTasks.equals(epicTask.listOfRelatedSubTasks);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (listOfRelatedSubTasks != null ? listOfRelatedSubTasks.hashCode() : 0);
        return result;
    }
}

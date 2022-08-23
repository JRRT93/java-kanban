package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;

    @BeforeEach
    public void beforeEach() {
        task = new Task("Task", "Description", 1);
    }

    @Test
    void getTaskName() {
        assertEquals("Task", task.getTaskName());
    }

    @Test
    void setTaskName() {
        task.setTaskName("AnotherName");
        assertEquals("AnotherName", task.getTaskName());
    }

    @Test
    void getDescription() {
        assertEquals("Description", task.getDescription());
    }

    @Test
    void setDescription() {
        task.setDescription("AnotherDescription");
        assertEquals("AnotherDescription", task.getDescription());
    }

    @Test
    void getStatus() {
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void setStatus() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void getIdentifier() {
        assertEquals(1, task.getIdentifier());
    }
}
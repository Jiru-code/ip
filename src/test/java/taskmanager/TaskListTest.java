package taskmanager;

import mryapper.YapperException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

public class TaskListTest {
    private TaskList taskList;

    // This method runs before each test to set up a clean list
    @BeforeEach
    void setUp() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("task1"));
        tasks.add(new ToDo("task2"));
        tasks.add(new ToDo("task3"));
        taskList = new TaskList(tasks);
    }

    @Test
    void testDelete_validIndex_shouldRemoveTaskAndReturnCorrectTask() throws YapperException {
        int initialSize = taskList.getSize();
        Task removedTask = taskList.delete(1); // Deleting "task2"
        assertEquals(initialSize - 1, taskList.getSize());
        assertEquals("task2", removedTask.getDescription());
        assertEquals("task3", taskList.getTasks().get(1).getDescription());
    }

    @Test
    void testDelete_invalidIndex_shouldThrowYapperException() {
        // Test an index that is too large
        YapperException exception1 = assertThrows(YapperException.class, () -> taskList.delete(100));
        assertEquals("Task number out of bounds!", exception1.getMessage());
        
        // Test a negative index
        YapperException exception2 = assertThrows(YapperException.class, () -> taskList.delete(-1));
        assertEquals("Task number out of bounds!", exception2.getMessage());
    }
}

package taskmanager;

import mryapper.YapperException;
import java.util.ArrayList;

/**
 * Represents the enntire lists of tasks, stored as an ArrayList<Task>.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Constructs a new TaskList when there is no file storing all the Tasks as a text file on the device.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a new TaskList loading the file that stores all the Tasks as a text file on the device.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Add a task called by TaskList
     * 
     * @param task A Task object
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Delete a task on the TaskList called by the command "delete"
     * 
     * @param index The index number of the task on the TaskList. You may check the index number using the command "list"
     * @return The Task at the specified index number.
     * @throws YapperException when index is invalid. Valid range: 0 <= index < number of tasks. Error-by-one handled by main code on MrYapper.
     */
    public Task delete(int index) throws YapperException {
        if (index < 0 || index >= tasks.size()) {
            throw new YapperException("Task number out of bounds!");
        }
        return tasks.remove(index);
    }

    /**
     * Mark a task on the TaskList as done, from the command "mark"
     * 
     * @param index The index number of the task to be marked on the TaskList. 
     * @return a string representation when the method markDone() is called on the Task specified.
     * @throws YapperException when index is invalid. Valid range: 0 <= index < number of tasks.
     */
    public String markTaskAsDone(int index) throws YapperException {
        if (index < 0 || index >= tasks.size()) {
            throw new YapperException("Task number out of bounds!");
        }
        return tasks.get(index).markDone();
    }

    /**
     * Unmark a task on the TaskList as undone, from the command "unmark"
     * 
     * @param index The index number of the task to be unmarked on the TaskList. 
     * @return a string representation when the method markUndone() is called on the Task specified.
     * @throws YapperException when index is invalid. Valid range: 0 <= index < number of tasks.
     */
    public String markTaskAsUndone(int index) throws YapperException {
        if (index < 0 || index >= tasks.size()) {
            throw new YapperException("Task number out of bounds!");
        }
        return tasks.get(index).markUndone();
    }

    /**
     * Returns a ArrayList<Task>.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int getSize() {
        return tasks.size();
    }
}
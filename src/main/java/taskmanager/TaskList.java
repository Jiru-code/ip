package taskmanager;

import mryapper.YapperException;
import java.util.ArrayList;

/**
 * Represents the lists of tasks and functions operation on a the task list
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Constructs a new TaskList assuming no TaskList can be found from existing data.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a new TaskList given that tasks can be found from existing data
     * @param tasks Tasks found from existing data.
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
     * Mark a task as done and display accordingly.
     * 
     * @param index The index number of a task when you call the command "list".
     * @return String that is returned by the markDone() function when a Task calls it.
     * @throws YapperException when invalid index number is given.
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
     * Find all the tasks in a tasklist that has the specified keyword.
     * 
     * @param keyword The keyword we are searching for in the task description.
     * @return a mataching ArrayList of tasks.
     */
    public ArrayList<Task> findTasks(String keyword) {
        ArrayList<Task> found = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                found.add(task);
            }
        }
        return found;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int getSize() {
        return tasks.size();
    }
}
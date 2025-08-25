import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task delete(int index) throws YapperException {
        if (index < 0 || index >= tasks.size()) {
            throw new YapperException("Task number out of bounds!");
        }
        return tasks.remove(index);
    }

    public String markTaskAsDone(int index) throws YapperException {
        if (index < 0 || index >= tasks.size()) {
            throw new YapperException("Task number out of bounds!");
        }
        return tasks.get(index).markDone();
    }

    public String markTaskAsUndone(int index) throws YapperException {
        if (index < 0 || index >= tasks.size()) {
            throw new YapperException("Task number out of bounds!");
        }
        return tasks.get(index).markUndone();
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int getSize() {
        return tasks.size();
    }
}
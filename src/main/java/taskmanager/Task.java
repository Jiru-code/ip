package taskmanager;

/**
 * Represents a task on the task list which could be either a
 * todo, deadline or an event task
 */
public class Task {
    protected String description;
    //Flag to know if a task is marked done or undone
    protected boolean isDone; 

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }
    
    public String getDescription() {
        return this.description;
    }

    public String getStatusIcon() {
        return isDone
                ? "X" 
                : " ";
    }

    /**
     * Checks if a task has already been marked as done before and if not,
     * mark it as done. A corresponding message to update the user on the status of the Task will be returned.
     * 
     * @return Resulting boolean of the mark status to MrYapper to print corresponding message
     */
    public boolean markDone() {
        if (isDone) {
            return false;
        }
        this.isDone = true;
        return true;
    }

    /** 
     * Checks if a task has already been marked as done before, and if so,
     * unmark it.
     * 
     * @return Resulting boolean of the mark status to MrYapper to print corresponding message
     */
    public boolean markUndone() {
        if (!isDone) {
            return false;
        }
        this.isDone = false;
        return true;
    }

    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.getDescription();
    }

    /**
     * Returns task as a formatted string to be saved in a 
     * .txt file that stores 1 task per line.
     * 
     * @return String written in format saved in file.
     */
    public String toFileString() {
        return " | " + (isDone ? "1" : "0") + " | " + description;
    }
}

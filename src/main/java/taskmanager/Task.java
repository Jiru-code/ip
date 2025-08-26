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
     * @return String that will be displayed to the user regarding the status of task.
     */
    public String markDone() {
        if (isDone) {
            return "Task has already been marked done!";
        } else {
            this.isDone = true;
            String beDone = "Nice! I've marked this task as done: \n"
                            + "  [" + this.getStatusIcon() + "] " + this.getDescription();
            return beDone;
        }
    }

    /** 
     * Checks if a task has already been marked as done before, and if so,
     * unmark it. A corresponding message to update the user on the status of the task Task will be returned.
     * 
     * @return String that will be displayed to the user regarding the status of the task.
     */
    public String markUndone() {
        if(isDone) {
            this.isDone = false;
            String beUndone = "Ok, I've marked this task as not done yet: \n"
                             + "  [" + this.getStatusIcon() + "] " + this.getDescription();
            return beUndone;
        } else {
            return "Task has not been marked done!";
        }
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

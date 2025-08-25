package taskmanager;

public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }
    
    public String getDescription() {
        return this.description;
    }

    public String getStatusIcon() {
        return (isDone? "X" : " ");
    }

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

    public String toFileString() {
        return " | " + (isDone ? "1" : "0") + " | " + description;
    }
}

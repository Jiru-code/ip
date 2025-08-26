package mryapper;

import taskmanager.Task;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all interactions with user, including reading input and printing outputs.
 */
public class Ui {
    /** Placeholder line to segment out the output from the chatbot */
    private static final String LINE = "_".repeat(50);
    private final Scanner scanner;

    /**
     * Constructs a new Ui instance with a Scanner for reading user input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public void showGreeting() {
        System.out.println(LINE + "\n" + "Hello! I'm Mr Yapper \n" +
                "What can I do for you?" + "\n" + LINE);
    }

    public void showGoodbye() {
        System.out.println(LINE + "\n" + "Bye. Hope to see you again soon!" + "\n" + LINE);
    }

    /**
     * Displays an error message when tasks fail to load from the file.
     *
     * @param message The error message to be displayed.
     */
    public void showLoadingError(String message) {
        System.out.println(LINE + "\n" + "Error loading tasks: " + message + "\n" + LINE);
    }

    /**
     * Displays a confirmation message after a task has been successfully added.
     *
     * @param task       The task that was added.
     * @param totalTasks The total number of tasks in the list after adding.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        System.out.println(LINE + "\n" + "Got it. I've added this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + totalTasks + " tasks in the list.");
        System.out.println(LINE);
    }

    public void showTaskRemoved(Task task, int totalTasks) {
        System.out.println(LINE + "\n" + "The task has been removed:");
        System.out.println(task.toString());
        System.out.println("Now you have " + totalTasks + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Displays the current list of tasks.
     *
     * @param tasks The ArrayList of tasks to be displayed.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println(LINE);
        if (tasks.isEmpty()) {
            System.out.println("Empty tasks.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i).toString());
            }
        }
        System.out.println(LINE);
    }

    /**
     * Displays the tasks which includes certain keyword(s).
     * 
     * @throws YapperException none of the Tasks contain a matching description.
     */
    public void showTasksFound(ArrayList<Task> foundTasks) throws YapperException {
        System.out.println(LINE);
        if (foundTasks.isEmpty()) {
            System.out.println("No matching keyword found among tasks! Try something else.");
        } else {
            System.out.println("I found the following matches! ");
            for (int i = 0; i < foundTasks.size(); i++) {
                System.out.println((i + 1) + ". " + foundTasks.get(i).toString());
            }
        }
        System.out.println(LINE);
    }

    /**
     * * Displays a confirmation message after a task has been marked or unmarked.
     *
     * @param message The message to be displayed.
     */
    public void showMarkedTask(String message) {
        System.out.println(LINE + "\n" + message + "\n" + LINE);
    }

    public void showError(String message) {
        System.out.println(LINE + "\n" + "Error: " + message + "\n" + LINE);
    }

    public String readCommand() {
        return scanner.nextLine();
    }
}

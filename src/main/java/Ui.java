import java.util.ArrayList;
import java.util.Scanner;

public class Ui {
    private static final String LINE = "_".repeat(50);
    private final Scanner scanner;

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

    public void showLoadingError(String message) {
        System.out.println(LINE + "\n" + "Error loading tasks: " + message + "\n" + LINE);
    }

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

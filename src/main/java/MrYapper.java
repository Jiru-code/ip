import java.util.ArrayList;
import java.util.Scanner;

public class MrYapper {
    private static final String LINE = "_".repeat(50);
    private static final String GREETING = "Hello! I'm Mr Yapper \n"
            + "What can I do for you?";
    private static final String GOODBYE = "Bye. Hope to see you again soon!";  
    public static void main(String[] args) {
        System.out.println(LINE + "\n" + GREETING + "\n" + LINE);

        ArrayList<Task> tasks = new ArrayList<>();
        Storage storage = new Storage("data/tasks.txt");

        try {
            tasks = storage.loadTasks();
        } catch (YapperException e) { /* deal with data file being corrupted */
            System.out.println(LINE + "\n" + "Error: " + e.getMessage() + "\n" + LINE); 
        }

        Scanner scanner = new Scanner(System.in);
        String input;
        
        while (true) {
            input = scanner.nextLine();
            String[] parts = input.trim().split(" ", 2);
            String command = parts[0];
            String desc = parts.length > 1 ? parts[1] : "";

            try {
                if (command.trim().equalsIgnoreCase("bye")) {
                    System.out.println(LINE + "\n" + GOODBYE + "\n" + LINE);
                    storage.saveTasks(tasks);
                    break;
                } else if (command.trim().equalsIgnoreCase("list")) {
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
                } else if (command.trim().equalsIgnoreCase("mark") || command.trim().equalsIgnoreCase("unmark")) {
                    if (parts.length < 2) {
                        throw new YapperException("Please provide a task number to " + command + ".");
                    }
                    
                    int taskNumber = Integer.parseInt(parts[1]);
                    if (taskNumber > 0 && taskNumber <= tasks.size()) {
                        Task curr = tasks.get(taskNumber - 1);
                        if (command.trim().equalsIgnoreCase("mark")) {
                            System.out.println(LINE + "\n" + curr.markDone() + "\n" + LINE);
                        } else {
                            System.out.println(LINE + "\n" + curr.markUndone() + "\n" + LINE);
                        }
                    } else {
                        throw new YapperException("Put in a task number that is valid!");
                    }
                } else if (command.trim().equalsIgnoreCase("todo") || command.trim().equalsIgnoreCase("deadline") ||
                command.trim().equalsIgnoreCase("event")) {
                    if (desc.isEmpty()) {
                        throw new YapperException("Please provide a description");
                    }
                    System.out.println(LINE + "\n" + "Got it. I've added this task:");
                    Task newTask;
                    if (command.trim().equalsIgnoreCase("todo")) {
                        newTask = new ToDo(desc);
                    } else if (command.trim().equalsIgnoreCase("deadline")) {
                        newTask = new Deadline(desc);      
                    } else {
                        newTask = new Event(desc);
                    }
                    tasks.add(newTask);
                    storage.saveTasks(tasks); // Save changes to the file
                    System.out.println(newTask.toString());
                    System.out.println("Now you have " + Integer.toString(tasks.size()) + " tasks in the list.");
                    System.out.println(LINE);
                } else if (command.trim().equalsIgnoreCase("delete")) {
                    if (desc.isEmpty()) {
                        throw new YapperException("Please provide a task number to " + command + ".");
                    } 
                    int taskNumber = Integer.parseInt(desc);
                    if (taskNumber < 1 || taskNumber > tasks.size() + 1) {
                        throw new YapperException("Invalid task number");
                    }
                    System.out.println(LINE + "\n" + "The task has been removed:" + "\n" + tasks.get(taskNumber - 1).toString() + "\n" + LINE);
                    tasks.remove(taskNumber - 1);
                } else {
                    throw new YapperException("Unknown command.");
                }
            } catch(YapperException e) {
                System.out.println(LINE + "\n" + "Error: " + e.getMessage() + "\n" + LINE);
            }
        }
        scanner.close();
    }
}

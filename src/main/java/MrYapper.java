import java.util.ArrayList;
import java.util.Scanner;

public class MrYapper {
    public static void main(String[] args) {
        int len = 50;
        String line = "_".repeat(len);
        String greeting = "Hello! I'm Mr Yapper \n"
                        + "What can I do for you?";
        String goodbye = "Bye. Hope to see you again soon!";  
        System.out.println(line + "\n" + greeting + "\n" + line);

        ArrayList<Task> tasks = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        String input;
        
        while (true) {
            input = scanner.nextLine();
            String[] parts = input.trim().split(" ", 2);
            String command = parts[0];

            if (command.trim().equalsIgnoreCase("bye")) {
                System.out.println(line + "\n" + goodbye + "\n" + line);
                break;
            } else if (command.trim().equalsIgnoreCase("list")) {
                System.out.println(line);
                if (tasks.isEmpty()) {
                    System.out.println("Empty tasks.");
                } else {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + ". " + tasks.get(i).toString());
                    }
                }
                System.out.println(line);
            } else if (command.trim().equalsIgnoreCase("mark") || command.trim().equalsIgnoreCase("unmark")) {
                if (parts.length < 2) {
                    System.out.println("Add in a task number");
                    continue;
                }
                
                int taskNumber = Integer.parseInt(parts[1]);
                if (taskNumber > 0 && taskNumber <= tasks.size()) {
                    Task curr = tasks.get(taskNumber - 1);
                    if (command.trim().equalsIgnoreCase("mark")) {
                        System.out.println(line + "\n" + curr.markDone() + "\n" + line);
                    } else {
                        System.out.println(line + "\n" + curr.markUndone() + "\n" + line);
                    }
                } else {
                    System.out.println("Put in a task number that is valid!");
                }
            } else {
                tasks.add(new Task(input));
                System.out.println(line + "\n" + "added: " + input + "\n" + line);
            }
        }

        scanner.close();
    }
}

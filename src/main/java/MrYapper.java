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

        ArrayList<String> list = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        String input;
        
        while (true) {
            input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("bye")) {
                System.out.println(line + "\n" + goodbye + "\n" + line);
                break;
            } else if (input.trim().equalsIgnoreCase("list")) {
                System.out.println(line);
                if (list.isEmpty()) {
                    System.out.println("Empty list.");
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        System.out.println((i + 1) + ". " + list.get(i));
                    }
                }
                System.out.println(line);
            } else {
                list.add(input);
                System.out.println(line + "\n" + "added: " + input + "\n" + line);
            }
        }

        scanner.close();
    }
}

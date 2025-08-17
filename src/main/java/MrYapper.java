import java.util.Scanner;

public class MrYapper {
    public static void main(String[] args) {
        int len = 50;
        String line = "_".repeat(len);
        String greeting = "Hello! I'm Mr Yapper \n"
                        + "What can I do for you?";
        String goodbye = "Bye. Hope to see you again soon!";  
        System.out.println(line + "\n" + greeting + "\n" + line);

        Scanner scanner = new Scanner(System.in);
        String input;
        
        while (true) {
            input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("bye")) {
                System.out.println(line + "\n" + goodbye + "\n" + line);
                break;
            }

            System.out.println(line + "\n" + input + "\n" + line);
        }

        scanner.close();
    }
}

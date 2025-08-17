public class MrYapper {
    public static void main(String[] args) {
        int len = 50;
        String line = "_".repeat(len);
        String greeting = "Hello! I'm Mr Yapper \n"
                        + "What can I do for you?";
        String goodbye = "Bye. Hope to see you again soon!";  
        System.out.println(line + "\n" + greeting + "\n" + line + "\n"+ goodbye);
    }
}

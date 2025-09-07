package mryapper;

import taskmanager.Deadline;
import taskmanager.Event;
import taskmanager.Storage;
import taskmanager.Task;
import taskmanager.TaskList;
import taskmanager.ToDo;
import java.util.ArrayList;

/**
 * Kickstarts the chatbot up and running.
 */
public class MrYapper {
    /** Filepath of where the tasks are stored relative to project root directory */
    private static final String FILE_PATH = "data/tasks.txt";
    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private Parser parser;

    /**
     * Acts as a result carrier for both GUI and CLI interfaces.
     * */ 
    private static class CommandResult {
        final String message;
        final boolean isExit;
        CommandResult(String message, boolean isExit) {
            this.message = message;
            this.isExit = isExit;
        }
        static CommandResult of(String msg) { return new CommandResult(msg, false); }
        static CommandResult exit(String msg) { return new CommandResult(msg, true); }
    }

    /**
     * Constructs a new chatbot instance, with new Ui, Parser and Storage classes initialised. 
     * Also retrieves the tasks that have been stored previously into the current 'tasks' field.
     */
    public MrYapper() {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(FILE_PATH);
        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (YapperException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the chatbot for CLI
     */
    public void run() {
        ui.showGreeting();
        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();
            CommandResult result = processCommand(fullCommand);
            ui.showText(result.message);
            isExit = result.isExit;
        }
    }

    /**
     * Runs the GUI entry
     * @param fullCommand takes and parses the full user input text
     * @return reply text
     */
    public String getResponse(String fullCommand) {
        return processCommand(fullCommand).message;
    }

    private CommandResult processCommand(String fullCommand) {
        String[] parsed = parser.parseCommand(fullCommand);
        String command = parsed[0].toLowerCase();
        String args = parsed[1];

        try {
            switch (command) {
            case "bye":
                storage.saveTasks(tasks.getTasks());
                return CommandResult.exit("Bye. Hope to see you again soon!");

            case "list":
                return CommandResult.of(buildTaskListMessage(tasks.getTasks()));

            case "mark":
            case "unmark":
                return CommandResult.of(handleMarkCore(command, args));

            case "todo":
            case "deadline":
            case "event":
                return CommandResult.of(handleAddTaskCore(command, args));

            case "delete":
                return CommandResult.of(handleDeleteCore(args));

            case "find":
                return CommandResult.of(handleFindCore(args));

            default:
                throw new YapperException("Unknown command.");
            }
        } catch (YapperException e) {
            return CommandResult.of("Error: " + e.getMessage());
        }
    }

    private int parseIndexOneBased(String args, String actionVerb) throws YapperException {
        if (args == null || args.trim().isEmpty()) {
            throw new YapperException("Please provide a task number to " + actionVerb + ".");
        }
        try {
            return Integer.parseInt(args.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new YapperException("Please provide a valid task number to " + actionVerb + ".");
        }
    }

    private String handleMarkCore(String command, String args) throws YapperException {
        int idx = parseIndexOneBased(args, command);
        boolean changed = command.equals("mark")
                ? tasks.markTaskAsDone(idx)
                : tasks.markTaskAsUndone(idx);
        storage.saveTasks(tasks.getTasks());
        Task t = tasks.getTasks().get(idx);
        return command.equals("mark")
                ? (changed ? "Nice! I've marked this task as done: \n  " + t.toString()
                           : "Task has already been marked done!")
                : (changed ? "Ok, I've marked this task as not done yet: \n  " + t.toString()
                           : "Task has not been marked done!");
    }

    private String handleAddTaskCore(String command, String args) throws YapperException {
        if (args.isEmpty()) {
            throw new YapperException("The description of a " + command + " cannot be empty.");
        }
        Task newTask;
        if (command.equals("todo")) {
            newTask = new ToDo(args);
        } else if (command.equals("deadline")) {
            if (!args.contains("/by")) {
                throw new YapperException("Have you told me when your task is /by??");
            }
            newTask = new Deadline(args);
        } else {
            if (!args.contains("/from") || !args.contains("/to")) {
                throw new YapperException("Have you told me when your task is /from when /to when??");
            }
            newTask = new Event(args);
        }
        tasks.add(newTask);
        storage.saveTasks(tasks.getTasks());
        return "Got it. I've added this task:\n" + newTask
                + "\nNow you have " + tasks.getSize() + " tasks in the list.";
    }

    private String handleDeleteCore(String args) throws YapperException {
        int taskIndex = parseIndexOneBased(args, "delete");
        Task removedTask = tasks.delete(taskIndex);
        storage.saveTasks(tasks.getTasks());
        return "The task has been removed:\n" + removedTask
                + "\nNow you have " + tasks.getSize() + " tasks in the list.";
    }

    /**
     * Handle the right logic when the command "find" is entered.
     * 
     * @param args String words we want to find in our Task description.
     * @throws YapperException the user does not include keywords.
     */
    private String handleFindCore(String args) throws YapperException {
        if (args.isEmpty()) {
            throw new YapperException("Stop clowning and include a keyword for me to search for");
        }
        ArrayList<Task> foundTasks = tasks.findTasks(args);
        if (foundTasks.isEmpty()) {
            return "No matching keyword found among tasks! Try something else.";
        }
        StringBuilder sb = new StringBuilder("I found the following matches!\n");
        for (int i = 0; i < foundTasks.size(); i++) {
            sb.append(i + 1).append(". ").append(foundTasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private String buildTaskListMessage(ArrayList<Task> list) {
        if (list.isEmpty()) return "Empty tasks.";
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1).append(". ").append(list.get(i)).append("\n");
        }
        return sb.toString().trim();
    }
    
    public static void main(String[] args) {
        new MrYapper().run();
    }
}

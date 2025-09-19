package mryapper;

import taskmanager.Deadline;
import taskmanager.Event;
import taskmanager.Storage;
import taskmanager.Task;
import taskmanager.TaskList;
import taskmanager.ToDo;
import taskmanager.ViewSchedules;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;

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
                return CommandResult.exit("Had fun yapping with you. Come back and yap together soon!");

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

            case "schedule":
            case "view":
                return CommandResult.of(handleSchedule(args));
            default:
                throw new YapperException(
                    "Invalid command. The list of available commands are:\n"
                    + "/bye\n"
                    + "/list\n"
                    + "/mark\n"
                    + "/unmark\n"
                    + "/todo\n"
                    + "/deadline\n"
                    + "/event\n"
                    + "/delete\n"
                    + "/find\n"
                    + "/schedule\n"
                    + "/return\n"
                );
            }
        } catch (YapperException e) {
            return CommandResult.of("Error: " + e.getMessage());
        }
    }

    private int parseIndexOneBased(String args, String actionVerb) throws YapperException {
        if (args == null || args.trim().isEmpty()) {
            throw new YapperException("Please provide a task number to " + actionVerb + "!");
        }
        try {
            return Integer.parseInt(args.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new YapperException("I did not recognise that task number! Please provide a valid number to" 
            + actionVerb + "."
            );
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
                ? (changed ? "Great job completing the task:\n  " + t.toString()
                           : "Task has already been marked done!")
                : (changed ? (
                    "Oh no you thought you finished a task but you didn't?" 
                    + "Ok, I've marked this task as not done yet: \n  " + t.toString()
                )
                           : "Task has not been marked done!");
    }

    private String handleAddTaskCore(String command, String args) throws YapperException {
        if (args.isEmpty()) {
            throw new YapperException("Tell me a whooooooooole lot more about " + command + "! :)");
        }
        Task newTask;
        if (command.equals("todo")) {
            newTask = new ToDo(args);
        } else if (command.equals("deadline")) {
            if (!args.contains("/by")) {
                throw new YapperException("Hey hey hey have you told me when your task is /by??");
            }
            newTask = new Deadline(args);
        } else {
            if (!args.contains("/from") || !args.contains("/to")) {
                throw new YapperException("Hellloo? Have you told me when your task is /from when /to when??");
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
        return (
            removedTask + "is gone and out of the way... FOREVER!!"
            + "\nNow you have " + tasks.getSize() + " tasks in the list."
        );
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
            return "Girl there's no matching keyword found among tasks. C'mon try something else.";
        }
        StringBuilder sb = new StringBuilder("I found the following matches!\n");
        for (int i = 0; i < foundTasks.size(); i++) {
            sb.append(i + 1).append(". ").append(foundTasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private String handleSchedule(String args) {
        String raw = args == null ? "" : args.trim();
        if (raw.isEmpty()) {
            return "Please provide a date. Example: schedule 2025-09-07";
        }
    
        // Accept either ISO date (yyyy-MM-dd) or d/M/yyyy
        LocalDate date = null;
        try {
            date = LocalDate.parse(raw); // yyyy-MM-dd
        } catch (DateTimeParseException ex) {
            try {
                DateTimeFormatter DMY = DateTimeFormatter.ofPattern("d/M/yyyy");
                date = LocalDate.parse(raw, DMY);
            } catch (DateTimeParseException ex2) {
                return "Girl I couldn't understand that date. Try yyyy-MM-dd or d/M/yyyy, e.g. schedule 2025-09-07";
            }
        }
    
        String message = ViewSchedules.forDate(this.tasks, date);
        return message;
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

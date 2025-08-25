package mryapper;

import taskmanager.Deadline;
import taskmanager.Event;
import taskmanager.Storage;
import taskmanager.Task;
import taskmanager.TaskList;
import taskmanager.ToDo;

public class MrYapper {
    private static final String FILE_PATH = "data/tasks.txt";
    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private Parser parser; 

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

    public void run() {
        ui.showGreeting();
        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();
            String[] parsedCommand = parser.parseCommand(fullCommand);
            String command = parsedCommand[0].toLowerCase();
            String args = parsedCommand[1];

            try {
                switch (command) {
                case "bye":
                    isExit = true;
                    storage.saveTasks(tasks.getTasks());
                    ui.showGoodbye();
                    break;
                case "list":
                    ui.showTaskList(tasks.getTasks());
                    break;
                case "mark":
                case "unmark":
                    if (args.isEmpty()) {
                        throw new YapperException("Please provide a task number to " + command + ".");
                    }
                    int taskNumber = Integer.parseInt(args) - 1; 
                    if (command.equals("mark")) {
                        ui.showMarkedTask(tasks.markTaskAsDone(taskNumber));
                    } else {
                        ui.showMarkedTask(tasks.markTaskAsUndone(taskNumber));
                    }
                    storage.saveTasks(tasks.getTasks());
                    break;
                case "todo":
                    if (args.isEmpty()) {
                        throw new YapperException("What's your task about? Don't add empty tasks u dummy!");
                    }
                    Task newToDo = new ToDo(args);
                    tasks.add(newToDo);
                    storage.saveTasks(tasks.getTasks());
                    ui.showTaskAdded(newToDo, tasks.getSize());
                    break;
                case "deadline":
                    if (args.isEmpty() || !args.contains("/by")) {
                        throw new YapperException("Have you told me what your task is about? And when is it /by??");
                    }
                    Task newDeadline = new Deadline(args);
                    tasks.add(newDeadline);
                    storage.saveTasks(tasks.getTasks());
                    ui.showTaskAdded(newDeadline, tasks.getSize());
                    break;
                case "event":
                    if (args.isEmpty() || !args.contains("/from") || !args.contains("/to")) {
                        throw new YapperException("Have you told me what your task is about? /from when /to when??");
                    }
                    Task newEvent = new Event(args);
                    tasks.add(newEvent);
                    storage.saveTasks(tasks.getTasks());
                    ui.showTaskAdded(newEvent, tasks.getSize());
                    break;
                case "delete":
                    if (args.isEmpty()) {
                        throw new YapperException("Please provide a task number to delete.");
                    }
                    int taskIndex = Integer.parseInt(args) - 1;
                    Task removedTask = tasks.delete(taskIndex);
                    storage.saveTasks(tasks.getTasks());
                    ui.showTaskRemoved(removedTask, tasks.getSize());
                    break;
                default:
                    throw new YapperException("Unknown command.");
                }
            } catch (YapperException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new MrYapper().run();
    }
}

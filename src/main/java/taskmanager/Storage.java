package taskmanager;

import mryapper.YapperException;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Control all functionalities of storing the lists of tasks offline on the device.
 */
public class Storage {
    private final Path filePath;

    /**
     * Constructs a Storage object with the specified file path.
     *
     * @param filePath The path to the data file.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
        assert this.filePath != null : "filePath must resolve to a Path";
    }

    /**
     * Loads tasks from the data file.
     * If the file does not exist, it creates a new one and returns an empty list.
     *
     * @return An ArrayList of Task objects loaded from the file.
     * @throws YapperException If there is an error creating or reading the file.
     */
    public ArrayList<Task> loadTasks() throws YapperException {
        ArrayList<Task> tasks = new ArrayList<>();
        File taskFile = this.filePath.toFile();
        assert taskFile != null : "taskFile should not be null";

        if (!taskFile.exists()) {
            try {
                Files.createDirectories(this.filePath.getParent());
                Files.createFile(this.filePath); // use NIO to create file
            } catch (IOException e) {
                throw new YapperException("Could not create data file: " + e.getMessage());
            }
            return tasks;
        }

        try (BufferedReader r = Files.newBufferedReader(this.filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }       
                tasks.add(parseLine(line));
            }
        } catch (IOException e) {
            throw new YapperException("Error loading tasks! " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Saves the current list of tasks to the data file.
     *
     * @param tasks The ArrayList of Task objects to be saved.
     * @throws YapperException If there is an error writing to the file.
     */
    public void saveTasks(ArrayList<Task> tasks) throws YapperException {
        Objects.requireNonNull(tasks, "tasks");
        try (BufferedWriter w = Files.newBufferedWriter(this.filePath, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                w.write(task.toFileString());
                w.newLine();
            }
        } catch (IOException e) {
            throw new YapperException("Error saving tasks to a file! " + e.getMessage());
        }
    }

    /**
     * Parses a single line from the data file and creates a Task object.
     * The line format is expected to be "Type | isDone | Description | ...".
     *
     * @param line The string line to be parsed.
     * @return A Task object corresponding to the parsed line.
     * @throws YapperException If the task type is invalid.
     */
    private Task parseLine(String line) throws YapperException {
        String[] parts = line.split(" \\| ");
        assert parts.length >= 3 : "Malformed line (need at least type, isDone, description)";

        String type = parts[0];
        boolean isDone = parts[1].equals("1");

        switch (type) {
        case "T":
            ToDo todo = new ToDo(parts[2]);
            if (isDone) todo.markDone();
            return todo;
        case "D":
            Deadline deadline = new Deadline(parts[2], parts[3]);
            if (isDone) deadline.markDone();
            return deadline;
        case "E":
            Event event = new Event(parts[2], parts[3], parts[4]);
            if (isDone) event.markDone();
            return event;
        default:
            throw new YapperException("Invalid task type! Task type was recognised as: " + type);
        }
    }
}

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    public ArrayList<Task> loadTasks() throws YapperException {
        ArrayList<Task> tasks = new ArrayList<>();
        File taskFile = this.filePath.toFile();

        if (!taskFile.exists()) {
            try {
                Files.createDirectories(this.filePath.getParent());
                taskFile.createNewFile();
            } catch (IOException e) {
                throw new YapperException("Could not create data file: " + e.getMessage());
            }
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(taskFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                tasks.add(parseLine(line));
            }
        } catch (IOException e) {
            throw new YapperException("Error loading tasks! " + e.getMessage());
        }
        return tasks;
    }

    public void saveTasks(ArrayList<Task> tasks) throws YapperException {
        try (FileWriter writer = new FileWriter(this.filePath.toString())) {
            for (Task task : tasks) {
                writer.write(task.toFileString() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new YapperException("Error saving tasks to a file! " + e.getMessage());
        }
    }

    private Task parseLine(String line) throws YapperException {
        String[] parts = line.split(" \\| ");
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

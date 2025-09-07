package taskmanager;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a deadline task on the task list
 */
public class Deadline extends Task{
    protected LocalDateTime duration;
    // Formats in which we recognise dates and time by the User and stored as a LocalDateTime object.
    private static final DateTimeFormatter INPUT_FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter INPUT_FORMATTER_WITHOUT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // If not recognised in suitable DateTime format, we will simply store the deadline as a string 
    protected String stringDuration;

    /**
     * Constructs a new Deadline using user's input. Uses the keyword "/by" to
     * split up the user's input. Stores the Deadline details as two parts: its description 
     * and its duration.
     * 
     * @param input The entire string that the user keyed in, less the command.
     */
    public Deadline(String input) {
        super(input.split("/by")[0].trim());
        assert this.description != null : "description must be initialised";

        String dateString = input.split("/by")[1].trim();
        try {
            if (dateString.contains(" ")) {
                this.duration = LocalDateTime.parse(dateString, INPUT_FORMATTER_WITH_TIME);
            } else {
                this.duration = LocalDate.parse(dateString, INPUT_FORMATTER_WITHOUT_TIME).atStartOfDay();
            }
        } catch (DateTimeParseException e) {
            this.stringDuration = dateString;
        }
    }

    /**
     * Constructs a new Deadline using format of tasks stored in .txt file
     * 
     * @param description String description of the task 
     * @param durationString String duration stored in .txt file. Tries to convert into LocalDateTime format else stored as a string
     */
    public Deadline(String description, String durationString) {
        super(description);
        assert description != null : "description cannot be null";
        assert durationString != null : "durationString cannot be null";
        
        try {
            this.duration = LocalDateTime.parse(durationString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        } catch (DateTimeParseException e) {
            this.stringDuration = durationString;
        }
    }

    @Override 
    public String toString() {
        if (duration == null) {
            return "[D]" + super.toString() + " (by: " + this.stringDuration + ")";
        } else {
            return "[D]" + super.toString() + " (by: " + this.duration.format(DateTimeFormatter.ofPattern("MMM dd yyyy, ha")) + ")";
        }
    }   

    /**
     * Returns deadline task as a formatted string to be saved in a 
     * .txt file
     * 
     * @return String written in format saved in file, with the duration in proper format.
     */
    @Override
    public String toFileString() {
        if (duration == null) {
            return "D" + super.toFileString() + " | " + this.stringDuration;
        } else {
            return "D" + super.toFileString() + " | " + this.duration.toString();
        }
    }
}

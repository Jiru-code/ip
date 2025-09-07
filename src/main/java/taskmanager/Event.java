package taskmanager;

import mryapper.YapperException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents an Event task on the tasklist.
 */
public class Event extends Task{
    // If not recognised in suitable DateTime format, we will simply store the deadline as a string 
    protected String stringDuration;
    protected LocalDateTime from;
    protected String stringFrom;
    protected LocalDateTime to;
    protected String stringTo;

    // Formats in which we recognise dates and time by the User and stored as a LocalDateTime object.
    private static final DateTimeFormatter INPUT_FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter INPUT_FORMATTER_WITHOUT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructs a new Event using user's input. Uses the keyword "/from" and "/to" to
     * split up the user's input. Stores the Task details as three parts: its description, 
     * from when, and to when.
     * 
     * @param input The entire string that the user keyed in, less the command.
     */
    public Event(String input) throws YapperException {
        super(input.split("/from")[0].trim());
        assert this.description != null : "description must be initialised";

        String[] parts = input.split("/from")[1].split("/to");
        this.stringFrom = parts[0].trim();
        this.stringTo = parts[1].trim();
        try {
            if (this.stringFrom.contains(" ")) {
                this.from = LocalDateTime.parse(this.stringFrom, INPUT_FORMATTER_WITH_TIME);
            } else {
                this.from = LocalDate.parse(this.stringFrom, INPUT_FORMATTER_WITHOUT_TIME).atStartOfDay();
            }

            if (this.stringTo.contains(" ")) {
                this.to = LocalDateTime.parse(this.stringTo, INPUT_FORMATTER_WITH_TIME);
            } else {
                this.to = LocalDate.parse(this.stringTo, INPUT_FORMATTER_WITHOUT_TIME).atStartOfDay();
            }

        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            this.from = null;
            this.to = null;
        }
    }

    /**
     * Constructs a new Event using format of tasks stored in .txt file
     * 
     * @param description String description of the task. 
     * @param fromString String from when the event started is stored in .txt file. Tries to convert into LocalDateTime format else stored as a string.
     * @param toString String to when the event ends is stored in .txt file. Tries to convert into LocalDateTime format else stored as a string.
     */
    public Event(String description, String fromString, String toString) throws YapperException {
        super(description);
        assert description != null : "description cannot be null";
        assert fromString != null && toString != null : "fromString/toString cannot be null";

        this.stringFrom = fromString;
        this.stringTo = toString;
        try {
            this.from = LocalDateTime.parse(fromString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            this.to = LocalDateTime.parse(toString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        } catch (DateTimeParseException e) {
            this.from = null;
            this.to = null;
        }
    }

    @Override 
    public String toString() {
        if (from == null || to == null) {
            return "[E]" + super.toString() + " (from: " + this.stringFrom + " to: " + this.stringTo + ")";
        } else {
            return "[E]" + super.toString() + " (from: " + this.from.format(DateTimeFormatter.ofPattern("MMM dd yyyy, ha")) +
                " to: " + this.to.format(DateTimeFormatter.ofPattern("MMM dd yyyy, ha")) + ")";
        }
    }

    /**
     * Returns event task as a formatted string to be saved in a 
     * .txt file
     * 
     * @return String written in format saved in file, with the from and to in proper format.
     */
    @Override
    public String toFileString() {
        if (from == null || to == null) {
            return "E" + super.toFileString() + " | " + this.stringFrom + " | " + this.stringTo;
        } else {
            return "E" + super.toFileString() + " | " + this.from.toString() + " | " + this.to.toString();
        }
    }
}

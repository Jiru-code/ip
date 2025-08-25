package taskmanager;

import mryapper.YapperException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event extends Task{
    protected LocalDateTime from;
    protected String stringFrom;
    protected LocalDateTime to;
    protected String stringTo;
    private static final DateTimeFormatter INPUT_FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter INPUT_FORMATTER_WITHOUT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Event(String input) throws YapperException {
        super(input.split("/from")[0].trim());
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

    public Event(String description, String fromString, String toString) throws YapperException {
        super(description);
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

    @Override
    public String toFileString() {
        if (from == null || to == null) {
            return "E" + super.toFileString() + " | " + this.stringFrom + " | " + this.stringTo;
        } else {
            return "E" + super.toFileString() + " | " + this.from.toString() + " | " + this.to.toString();
        }
    }
}

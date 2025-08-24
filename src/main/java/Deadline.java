import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class Deadline extends Task{
    protected LocalDateTime duration;
    private static final DateTimeFormatter INPUT_FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter INPUT_FORMATTER_WITHOUT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    protected String stringDuration;

    public Deadline(String input) throws YapperException {
        super(input.split("/by")[0].trim());
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

    public Deadline(String description, String durationString) throws YapperException {
        super(description);
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

    @Override
    public String toFileString() {
        if (duration == null) {
            return "D" + super.toFileString() + " | " + this.stringDuration;
        } else {
            return "D" + super.toFileString() + " | " + this.duration.toString();
        }
    }
}

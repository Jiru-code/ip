import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event extends Task{
    protected LocalDateTime from;
    protected LocalDateTime to;
    private static final DateTimeFormatter INPUT_FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter INPUT_FORMATTER_WITHOUT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Event(String input) throws YapperException {
        super(input.split("/from")[0].trim());
        try {
            String[] parts = input.split("/from")[1].split("/to");
            String fromDateString = parts[0].trim();
            String toDateString = parts[1].trim();

            if (fromDateString.contains(" ")) {
                this.from = LocalDateTime.parse(fromDateString, INPUT_FORMATTER_WITH_TIME);
            } else {
                this.from = LocalDate.parse(fromDateString, INPUT_FORMATTER_WITHOUT_TIME).atStartOfDay();
            }

            if (toDateString.contains(" ")) {
                this.to = LocalDateTime.parse(toDateString, INPUT_FORMATTER_WITH_TIME);
            } else {
                this.to = LocalDate.parse(toDateString, INPUT_FORMATTER_WITHOUT_TIME).atStartOfDay();
            }

        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            throw new YapperException("Please provide event in the format description /from [date] /to [date]. Dates can be d/M/yyyy HHmm or yyyy-MM-dd.");
        }
    }

    public Event(String description, String fromString, String toString) throws YapperException {
        super(description);
        try {
            this.from = LocalDateTime.parse(fromString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            this.to = LocalDateTime.parse(toString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        } catch (DateTimeParseException e) {
            throw new YapperException("Error loading event dates from file.");
        }
    }

    @Override 
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from.format(DateTimeFormatter.ofPattern("MMM dd yyyy, ha")) +
                " to: " + this.to.format(DateTimeFormatter.ofPattern("MMM dd yyyy, ha")) + ")";
    }

    @Override
    public String toFileString() {
        return "E" + super.toFileString() + " | " + this.from.toString() + " | " + this.to.toString();
    }
}

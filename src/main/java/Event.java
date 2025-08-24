import java.util.StringJoiner;

public class Event extends Task{
    protected String duration;

    public Event(String input) {
        super(input.split("/")[0]);
        String[] parts = input.split("/");
        StringJoiner durationJoiner = new StringJoiner(" ");
        for (int i = 1; i < parts.length; i++) {
            String currentPart = parts[i].trim();
            int firstSpaceIndex = currentPart.indexOf(" ");
            if (firstSpaceIndex != -1) {
                String key = currentPart.substring(0, firstSpaceIndex);
                String value = currentPart.substring(firstSpaceIndex + 1);
                durationJoiner.add(key + ": " + value);
            } else {
                durationJoiner.add(currentPart);
            }
        }
        
        this.duration = "(" + durationJoiner.toString() + ")";
    }

    @Override 
    public String toString() {
        return "[E]" + super.toString() + this.duration;
    }

    @Override
    public String toFileString() {
        return "E" + " | " + (isDone ? "1" : "0") + " | " + 
                getDescription() + "| " + this.duration.substring(1, this.duration.length() - 1);
    }
}

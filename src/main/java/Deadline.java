import java.util.StringJoiner;

public class Deadline extends Task{
    protected String duration;

    public Deadline(String input) {
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
        return "[D]" + super.toString() + this.duration;
    }    

    @Override
    public String toFileString() {
        int startOfDuration = duration.indexOf(":") + 1;
        int endOfDuration = duration.indexOf(")");
        String editedDuration = duration.substring(startOfDuration, endOfDuration);
        return "D" + super.toFileString() + "|" + editedDuration;
    }
}

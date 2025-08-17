public class Deadline extends Task{
    protected String duration;

    public Deadline(String input) {
        super(input.split("/")[0]);
        String[] parts = input.split("/");
        StringBuilder durationBuilder = new StringBuilder("(");
        for (int i = 1; i < parts.length; i++) {
            String currentPart = parts[i].trim();
            String[] keyValue = currentPart.split(" ", 2);

            if (i > 1) {
                durationBuilder.append(" ");
            }

            durationBuilder.append(keyValue[0]).append(": ").append(keyValue[1]);
        }
        
        this.duration = durationBuilder.toString() + ")";
    }

    @Override 
    public String toString() {
        return "[D]" + super.toString() + this.duration;
    }    
}

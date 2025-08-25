package mryapper;

public class Parser {
    public String[] parseCommand(String fullCommand) {
        String[] parts = fullCommand.trim().split(" ", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";
        return new String[]{command, args};
    }
}

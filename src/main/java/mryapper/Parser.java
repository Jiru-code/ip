package mryapper;

public class Parser {

    /**
     * Returns a String array, where the first element is the command handle
     * and the second element would be the rest of the user input
     * 
     * @param fullCommand The entire user input entered in
     * @return Two-element String array
     */
    public String[] parseCommand(String fullCommand) {
        String[] parts = fullCommand.trim().split(" ", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";
        return new String[]{command, args};
    }
}

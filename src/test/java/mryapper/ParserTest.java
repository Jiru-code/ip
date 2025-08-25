package mryapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {

    private final Parser parser = new Parser();

    @Test
    void testParseCommand_withArgs_shouldReturnCorrectArray() {
        String fullCommand = "deadline read book /by 2023-10-15";
        String[] result = parser.parseCommand(fullCommand);
        assertEquals("deadline", result[0]);
        assertEquals("read book /by 2023-10-15", result[1]);
    }

    @Test
    void testParseCommand_withoutArgs_shouldReturnEmptyStringAsArgs() {
        String fullCommand = "list";
        String[] result = parser.parseCommand(fullCommand);
        assertEquals("list", result[0]);
        assertEquals("", result[1]);
    }

    @Test
    void testParseCommand_withOneArg_shouldReturnCorrectArray() {
        String fullCommand = "todo buy milk";
        String[] result = parser.parseCommand(fullCommand);
        assertEquals("todo", result[0]);
        assertEquals("buy milk", result[1]);
    }
}
package taskmanager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility to build a human-friendly, time-ordered schedule view
 * for a specific date from the existing TaskList.
 *
 * - Deadlines that fall on the given date
 * - Events that intersect the given date (multi-day supported)
 * - Orders by time; "time unknown" items are listed last
 * - Marks done items with " (done)".
 */
public final class ViewSchedules {

    private ViewSchedules() { }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final class Block {
        /**
         * Creates a schedule block.
         *
         * @param start The start time, or {@code null} if unknown.
         * @param end The end time, or {@code null} if unknown.
         * @param label The display label.
         */
        Block(LocalTime start, LocalTime end, String label) {
            this.start = start;
            this.end = end;
            this.label = label;
        }

        final LocalTime start; // Null denotes unknown start; placed after timed blocks.
        final LocalTime end;
        final String label;
    }

    /**
     * Forms a display of the user's schedule for the given date.
     *
     * @param taskList The list of tasks to render.
     * @param date The date to render.
     * @return A string representation of the schedule.
     */
    public static String forDate(TaskList taskList, LocalDate date) {
        List<Block> blocks = buildBlocks(taskList, date);
        sortBlocks(blocks);
        return formatSchedule(date, blocks);
    }

    /**
     * Builds schedule blocks for tasks that are relevant to the date.
     *
     * @param taskList The list of tasks.
     * @param date The date to consider.
     * @return A list of blocks for rendering.
     */
    private static List<Block> buildBlocks(TaskList taskList, LocalDate date) {
        List<Block> blocks = new ArrayList<>();
        for (Task t : taskList.getTasks()) {
            addBlocksForTask(t, date, blocks);
        }
        return blocks;
    }

    /**
     * Adds zero or more blocks for a single task depending on its type and timing.
     *
     * @param task The task to convert.
     * @param date The date to consider.
     * @param out The destination list.
     */
    private static void addBlocksForTask(Task task, LocalDate date, List<Block> out) {
        if (task instanceof Deadline) {
            addBlocksForDeadline((Deadline) task, date, out);
        } else if (task instanceof Event) {
            addBlocksForEvent((Event) task, date, out);
        } else {
            // ToDo and other non-time-bound tasks are excluded from per-day schedule.
        }
    }

    /**
     * Adds blocks for a deadline when it falls on the date.
     *
     * @param d The deadline task.
     * @param date The date to consider.
     * @param out The destination list.
     */
    private static void addBlocksForDeadline(Deadline d, LocalDate date, List<Block> out) {
        LocalDateTime when = d.duration;
        if (when != null) {
            if (when.toLocalDate().isEqual(date)) {
                out.add(new Block(
                        when.toLocalTime(),
                        null,
                        "[D] " + d.getDescription() + (d.isDone ? " (done)" : "")
                ));
            }
            return;
        }

        LocalDate parsed = parseDateLenient(d.stringDuration);
        if (parsed != null && parsed.isEqual(date)) {
            out.add(new Block(
                    null,
                    null,
                    "[D] " + d.getDescription() + " (time unknown)" + (d.isDone ? " (done)" : "")
            ));
        }
    }

    /**
     * Adds blocks for an event when it intersects the date.
     *
     * @param e The event task.
     * @param date The date to consider.
     * @param out The destination list.
     */
    private static void addBlocksForEvent(Event e, LocalDate date, List<Block> out) {
        LocalDateTime from = e.from;
        LocalDateTime to = e.to;

        if (from != null && to != null) {
            addPreciseEventBlock(e, date, out, from, to);
            return;
        }

        LocalDate fromDate = parseDateLenient(e.stringFrom);
        LocalDate toDate = parseDateLenient(e.stringTo);

        if (fromDate != null && toDate != null) {
            if (intersects(date, fromDate, toDate)) {
                out.add(new Block(
                        null,
                        null,
                        "[E] " + e.getDescription() + " (time unknown)" + (e.isDone ? " (done)" : "")
                ));
            }
        } else if (fromDate != null && fromDate.isEqual(date)) {
            out.add(new Block(
                    null,
                    null,
                    "[E] " + e.getDescription() + " (time unknown)" + (e.isDone ? " (done)" : "")
            ));
        }
    }

    /**
     * Adds a timed block for an event using precise start and end.
     *
     * @param e The event task.
     * @param date The date to consider.
     * @param out The destination list.
     * @param from The start datetime.
     * @param to The end datetime.
     */
    private static void addPreciseEventBlock(Event e, LocalDate date, List<Block> out,
                                             LocalDateTime from, LocalDateTime to) {
        LocalDate dayOfFrom = from.toLocalDate();
        LocalDate dayOfTo = to.toLocalDate();

        if (!intersects(date, dayOfFrom, dayOfTo)) {
            return;
        }

        LocalTime start = date.isEqual(dayOfFrom) ? from.toLocalTime() : LocalTime.MIN;
        LocalTime end = date.isEqual(dayOfTo) ? to.toLocalTime() : LocalTime.of(23, 59);

        out.add(new Block(
                start,
                end,
                "[E] " + e.getDescription() + (e.isDone ? " (done)" : "")
        ));
    }

    /**
     * Determines whether a date intersects a closed date range.
     *
     * @param date The date to test.
     * @param start Inclusive start date.
     * @param end Inclusive end date.
     * @return {@code true} if the date is within the range.
     */
    private static boolean intersects(LocalDate date, LocalDate start, LocalDate end) {
        return !(date.isBefore(start) || date.isAfter(end));
    }

    /**
     * Sorts blocks by start time (timed first), then end time.
     *
     * @param blocks The blocks to sort.
     */
    private static void sortBlocks(List<Block> blocks) {
        blocks.sort(Comparator
                .comparing((Block b) -> b.start, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(b -> b.end, Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    /**
     * Formats the schedule as text.
     *
     * @param date The date for the schedule.
     * @param blocks The blocks to render.
     * @return The formatted schedule.
     */
    private static String formatSchedule(LocalDate date, List<Block> blocks) {
        StringBuilder sb = new StringBuilder();
        sb.append("Schedule for ")
          .append(date.format(DATE_FMT))
          .append(" (").append(titleCase(date.getDayOfWeek())).append(")\n");
        sb.append("-".repeat(40)).append("\n");

        if (blocks.isEmpty()) {
            sb.append("No scheduled items for this date.");
            return sb.toString();
        }

        for (Block b : blocks) {
            sb.append(formatLine(b)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Formats a single line for a block.
     *
     * @param b The block to format.
     * @return The formatted line.
     */
    private static String formatLine(Block b) {
        String timeLabel;
        if (b.start == null && b.end == null) {
            timeLabel = "??:??";
        } else if (b.end == null) {
            timeLabel = b.start.format(TIME_FMT);
        } else {
            timeLabel = b.start.format(TIME_FMT) + "–" + b.end.format(TIME_FMT);
        }
        return timeLabel + "  " + b.label;
    }

    /**
     * Parses common date formats.
     *
     * @param s The input string.
     * @return The parsed date, or {@code null} if none matches.
     */
    private static LocalDate parseDateLenient(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        // Try ISO date first: yyyy-MM-dd
        try {
            return LocalDate.parse(trimmed);
        } catch (Exception ignored) { }
        // Try d/M/yyyy HHmm
        try {
            DateTimeFormatter DMY_TIME = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
            return LocalDateTime.parse(trimmed, DMY_TIME).toLocalDate();
        } catch (Exception ignored) { }
        // Try d/M/yyyy (date only)
        try {
            DateTimeFormatter DMY = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(trimmed, DMY);
        } catch (Exception ignored) { }
        return null;
    }

    /**
     * Returns the day-of-week in Title Case.
     *
     * @param day The day of week.
     * @return The title-cased day name.
     */
    private static String titleCase(DayOfWeek day) {
        String s = day.toString().toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

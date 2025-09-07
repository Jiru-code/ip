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
 *  - Deadlines that fall on the given date
 *  - Events that intersect the given date (multi-day supported)
 *  - Orders by time; "time unknown" items are listed last
 *  - Marks done items with " (done)"
 */
public final class ViewSchedules {

    private ViewSchedules() { }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final class Block {
        // Null start means unknown start which we will place after timed blocks.
        final LocalTime start;
        final LocalTime end;
        final String label;
        Block(LocalTime start, LocalTime end, String label) {
            this.start = start;
            this.end = end;
            this.label = label;
        }
    }

    /**
     * Forms a display of user's schedule for {date}.
     * Only Events that intersect the day and Deadlines that fall on the day are included.
     * ToDo items are not scheduled and are excluded here.
     * 
     * @return String representation of a schedule on a particular date.
     */
    public static String forDate(TaskList taskList, LocalDate date) {
        List<Block> blocks = new ArrayList<>();

        for (Task t : taskList.getTasks()) {
            if (t instanceof Deadline) {
                Deadline d = (Deadline) t;
                LocalDateTime when = d.duration;
                if (when != null) {
                    if (when.toLocalDate().isEqual(date)) {
                        blocks.add(new Block(
                            when.toLocalTime(),
                            null,
                            "[D] " + t.getDescription() + (t.isDone ? " (done)" : "")
                        ));
                    }
                } else {
                    LocalDate parsed = parseDateLenient(d.stringDuration);
                    if (parsed != null && parsed.isEqual(date)) {
                        blocks.add(new Block(
                            null, null,
                            "[D] " + t.getDescription() + " (time unknown)" + (t.isDone ? " (done)" : "")
                        ));
                    }
                }

            } else if (t instanceof Event) {
                Event e = (Event) t;
                LocalDateTime from = e.from;
                LocalDateTime to = e.to;

                if (from != null && to != null) {
                    LocalDate dayOfFrom = from.toLocalDate();
                    LocalDate dayOfTo = to.toLocalDate();
                    boolean intersects = !(date.isBefore(dayOfFrom) || date.isAfter(dayOfTo));
                    if (intersects) {
                        LocalTime start = date.isEqual(dayOfFrom) ? from.toLocalTime() : LocalTime.MIN;
                        LocalTime end = date.isEqual(dayOfTo) ? to.toLocalTime() : LocalTime.of(23, 59);
                        blocks.add(new Block(
                            start, end,
                            "[E] " + t.getDescription() + (t.isDone ? " (done)" : "")
                        ));
                    }
                } else {
                    // Best-effort string-based handling (when only strings were stored)
                    LocalDate fromDate = parseDateLenient(e.stringFrom);
                    LocalDate toDate = parseDateLenient(e.stringTo);
                    if (fromDate != null && toDate != null) {
                        boolean intersects = !(date.isBefore(fromDate) || date.isAfter(toDate));
                        if (intersects) {
                            blocks.add(new Block(
                                null, null,
                                "[E] " + t.getDescription() + " (time unknown)" + (t.isDone ? " (done)" : "")
                            ));
                        }
                    } else if (fromDate != null && fromDate.isEqual(date)) {
                        blocks.add(new Block(
                            null, null,
                            "[E] " + t.getDescription() + " (time unknown)" + (t.isDone ? " (done)" : "")
                        ));
                    }
                }
            } else {
                // Non-time-bound tasks (ToDo) are not included in a per-day schedule as of now
            }
        }

        // Sort blocks: timed blocks first by start then end; "unknown time" blocks last (start == null)
        blocks.sort(new Comparator<Block>() {
            @Override
            public int compare(Block a, Block b) {
                if (a.start == null && b.start == null) return 0;
                if (a.start == null) return 1;  
                if (b.start == null) return -1;
                int cmp = a.start.compareTo(b.start);
                if (cmp != 0) return cmp;
                if (a.end == null && b.end == null) return 0;
                if (a.end == null) return -1;  
                if (b.end == null) return 1;
                return a.end.compareTo(b.end);
            }
        });

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
            String timeLabel;
            if (b.start == null && b.end == null) {
                timeLabel = "??:??";
            } else if (b.end == null) {
                timeLabel = b.start.format(TIME_FMT);
            } else {
                timeLabel = b.start.format(TIME_FMT) + "–" + b.end.format(TIME_FMT);
            }
            sb.append(timeLabel).append("  ").append(b.label).append("\n");
        }
        return sb.toString().trim();
    }

    /** 
     * Parses common date formats
     *  
     * @return Formatted LocalDate but returns a null if none matches
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

    private static String titleCase(DayOfWeek day) {
        String s = day.toString().toLowerCase(); 
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

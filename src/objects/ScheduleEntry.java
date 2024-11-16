package objects;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.OffsetTime;

/**
 * A serializable class to represent one instance of section "meeting".
 */
public class ScheduleEntry implements Serializable {
    private String location;
    private boolean is_sync;
    private DayOfWeek day_of_week;
    private OffsetTime start_time;
    private OffsetTime end_time;

    /**
     * Construct a {@code ScheduleEntry}.
     * 
     * @param location    The location of this sestion.
     * @param is_sync     Whether this sestion is synchronous (true) or asynchronous
     *                    (false).
     * @param day_of_week The day of week for this session.
     * @param start_time  The start time of this session.
     * @param end_time    The end time of this session.
     * @throws NullPointerException     If any parameters are null.
     * @throws IllegalArgumentException If {@code end_time} is before or at
     *                                  {@code start_time}.
     */
    public ScheduleEntry(String location, boolean is_sync, DayOfWeek day_of_week, OffsetTime start_time,
            OffsetTime end_time) {
        if (location == null || day_of_week == null || start_time == null || end_time == null) {
            throw new NullPointerException("Arguments for constructor must not be null.");
        }

        this.location = location;
        this.is_sync = is_sync;
        this.day_of_week = day_of_week;

        if (end_time.isBefore(start_time) && end_time.isEqual(start_time)) {
            throw new IllegalArgumentException("'end_time' must be strictly after 'start_time'");
        }

        this.start_time = start_time;
        this.end_time = end_time;
    }

    /**
     * Return a 2-tuple for the start and end time.
     * 
     * @return A 2-tuple for the start and end time.
     */
    public synchronized Tuple getTime() {
        return new Tuple(start_time, end_time);
    }

    public synchronized String getLocation() {
        return location;
    }

    public synchronized DayOfWeek getDayOfWeek() {
        return day_of_week;
    }

    public synchronized boolean isSync() {
        return is_sync;
    }

    /**
     * Whether or not the two schedule entries has any time overlap.
     * 
     * @param other
     * @return true if one of the entry start before the other entry ends, false
     *         otherwise.
     */
    public synchronized boolean isOverlap(ScheduleEntry other) {
        // If one of them is async, it doesn't have conflict.
        if (!this.isSync() || !other.isSync()) {
            return false;
        }
        if (this.getDayOfWeek() != other.getDayOfWeek()) {
            return false;
        }
        if (this.getTime().getStart().isAfter(other.getTime().getEnd()) ||
                other.getTime().getStart().isAfter(this.getTime().getEnd())) {
            return false;
        }
        return true;
    }
}

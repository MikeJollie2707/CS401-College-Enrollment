package objects;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.OffsetTime;

public class ScheduleEntry implements Serializable {
    private String location;
    private boolean is_sync;
    private DayOfWeek day_of_week;
    private OffsetTime start_time;
    private OffsetTime end_time;

    public ScheduleEntry(String location, boolean is_sync, DayOfWeek day_of_week, OffsetTime start_time,
            OffsetTime end_time) {
        this.location = location;
        this.is_sync = is_sync;
        this.day_of_week = day_of_week;

        if (end_time.isBefore(start_time) && end_time.isEqual(start_time)) {
            throw new IllegalArgumentException("'end_time' must be strictly after 'start_time'");
        }

        this.start_time = start_time;
        this.end_time = end_time;
    }

    public Tuple getTime() {
        return new Tuple(start_time, end_time);
    }

    public String getLocation() {
        return location;
    }

    public DayOfWeek getDayOfWeek() {
        return day_of_week;
    }

    public boolean isSync() {
        return is_sync;
    }
}

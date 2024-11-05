package objects;

import java.time.DayOfWeek;
import java.time.OffsetTime;

public class ScheduleEntry {
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
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public OffsetTime[] getTime() {
        OffsetTime[] temp = new OffsetTime[2];
        temp[0] = start_time;
        temp[1] = end_time;
        return temp;
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

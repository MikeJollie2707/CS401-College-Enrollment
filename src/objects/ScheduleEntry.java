package objects;

import java.sql.Time;

public class ScheduleEntry {
    private String location;
    private boolean is_sync;
    private String day_of_week;
    private Time start_time;
    private Time end_time;

    public ScheduleEntry(String location, boolean is_sync, String day_of_week, Time start_time, Time end_time) {
        this.location = location;
        this.is_sync = is_sync;
        this.day_of_week = day_of_week;
        this.start_time = start_time;
        this.end_time = end_time;
    }
    public Tuple getTime() {
        return new Tuple(start_time, end_time);
    }
    public String getLocation() {
        return location;
    }
    public String getDayOfWeek() {
        return day_of_week;
    }
    public boolean isSync() {
        return is_sync;
    }
}

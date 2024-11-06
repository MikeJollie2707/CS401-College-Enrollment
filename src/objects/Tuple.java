package objects;

import java.time.OffsetTime;

public class Tuple {
    private final OffsetTime start;
    private final OffsetTime end;

    public Tuple(OffsetTime start, OffsetTime end) {
        this.start = start;
        this.end = end;
    }

    public OffsetTime getStart() {
        return start;
    }

    public OffsetTime getEnd() {
        return end;
    }

}
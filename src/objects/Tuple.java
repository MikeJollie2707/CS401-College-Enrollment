package objects;

import java.time.OffsetTime;

/**
 * A 2-tuple storing a time range.
 */
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
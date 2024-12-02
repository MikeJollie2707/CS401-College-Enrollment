package objects;

import java.util.List;
import java.util.Locale;
import java.io.Serializable;
import java.time.OffsetTime;
import java.time.format.TextStyle;
import java.util.ArrayList;

public class BodyReport implements Serializable {
    private List<Course> courses;

    public BodyReport(List<Course> courses) {
        this.courses = courses;
    }

    public String toString() {
        String out = String.format("Course count: %d\n", courses.size());
        Section highestEnrollmentRate = null;
        Section highestEnrollmentCount = null;
        Section lowestEnrollmentCount = null;

        ArrayList<String> row = new ArrayList<>();
        row.add("Active section count: 0");
        int activeSectionCount = 0;
        for (var course : courses) {
            row.add(String.format("%s - %s", course.getID(), course.getName()));
            String sectionPrefix = String.format("%s-%s-", course.getPrefix(), course.getNumber());
            for (var section : course.getSections()) {
                if (section.getStatus() == SectionStatus.ACTIVE) {
                    ++activeSectionCount;
                    row.set(0, String.format("Active section count: %d", activeSectionCount));

                    row.add(String.format("%s%s: %d/%d %d/%d (%s)",
                            sectionPrefix,
                            section.getNumber(),
                            section.getEnrolled().size(),
                            section.getMaxCapacity(),
                            section.getWaitlisted().size(),
                            section.getMaxWaitlistSize(),
                            section.getInstructor().getName()));
                    for (var entry : section.getSchedule()) {
                        var time = entry.getTime();
                        OffsetTime start = time.getStart();
                        OffsetTime end = time.getEnd();
                        row.add(String.format("    %s %s-%s (%s, %s)",
                                entry.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US),
                                start.toLocalTime(),
                                end.toLocalTime(),
                                entry.isSync() ? "sync" : "async",
                                entry.getLocation()));

                        if (highestEnrollmentCount == null) {
                            highestEnrollmentCount = section;
                        } else if (highestEnrollmentCount.getEnrolled().size() < section.getEnrolled().size()) {
                            highestEnrollmentCount = section;
                        }

                        if (highestEnrollmentRate == null) {
                            highestEnrollmentRate = section;
                        } else {
                            float currHighestRate = (float) highestEnrollmentRate.getEnrolled().size()
                                    / highestEnrollmentRate.getMaxCapacity();
                            float currRate = (float) section.getEnrolled().size() / section.getMaxCapacity();
                            if (Float.compare(currRate, currHighestRate) > 0) {
                                highestEnrollmentRate = section;
                            }
                        }

                        if (lowestEnrollmentCount == null) {
                            lowestEnrollmentCount = section;
                        } else if (lowestEnrollmentCount.getEnrolled().size() > section.getEnrolled().size()) {
                            lowestEnrollmentCount = section;
                        }
                    }
                }
            }
        }

        if (highestEnrollmentCount != null && highestEnrollmentRate != null && lowestEnrollmentCount != null) {
            row.add("");
            row.add(String.format("Highest section enroll count: %s-%s-%s",
                    highestEnrollmentCount.getCourse().getPrefix(),
                    highestEnrollmentCount.getCourse().getNumber(),
                    highestEnrollmentCount.getNumber()));
            row.add(String.format("Highest section enroll rate: %s-%s-%s",
                    highestEnrollmentRate.getCourse().getPrefix(),
                    highestEnrollmentRate.getCourse().getNumber(),
                    highestEnrollmentRate.getNumber()));
            row.add(String.format("Lowest section enroll count: %s-%s-%s",
                    lowestEnrollmentCount.getCourse().getPrefix(),
                    lowestEnrollmentCount.getCourse().getNumber(),
                    lowestEnrollmentCount.getNumber()));
        }
        return out + String.join("\n", row);
    }
}

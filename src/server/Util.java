package server;

import java.util.HashSet;

import objects.Course;
import objects.EnrollStatus;
import objects.ScheduleEntry;
import objects.Section;
import objects.ServerMsg;
import objects.Student;
import objects.University;

/**
 * Some utility functions for server.
 */
public class Util {
    /**
     * Enroll a student into the provided section.
     * 
     * @param clientSection This must be the object provided by the client, not from
     *                      the server.
     * @param student       The student enrolling. This must be the object from the
     *                      server.
     * @param university    The university. This must be the object from the server.
     * @return If success, an {@code OK ServerMsg} containing the
     *         {@code EnrollStatus}. If failed, an {@code ERR ServerMsg} containing
     *         the reason {@code String}.
     */
    public static ServerMsg enroll(Section clientSection, Student student, University university) {
        Course clientCourse = clientSection.getCourse();
        Course course = university.getCourseByID(clientCourse.getID());
        if (course == null) {
            return ServerMsg.asERR(String.format("Course ID '%s' not found.", clientCourse.getID()));
        }

        HashSet<String> unfulfilledPrereqs = new HashSet<>(course.getPrerequisites());
        /**
         * If unfulfilledPrereqs has something after this loop,
         * the student hasn't cleared all prereqs.
         * This only check one level of prerequisite, so it can false-alarm in rare
         * cases (but it's probably safe to ignore it).
         * ie. if CS 401 explicitly requires both CS 301 and CS 201
         * (and CS 301 requires CS 201) and the student only clear CS 301
         * (maybe they get CS 201 equivalent from a different course)
         * then the check will fail with CS 201 as an unfulfilled prereq.
         */
        for (var pastSection : student.getPastEnrollments()) {
            // Just to make sure it's the latest object.
            Course pastCourse = university.getCourseByID(pastSection.getCourse().getID());
            if (pastCourse == null) {
                // We pretend the course exist cuz it's possible it's an old course
                // that is used to clear some prerequisites.
                pastCourse = pastSection.getCourse();
            }

            var iter = course.getPrerequisites().iterator();
            while (iter.hasNext()) {
                String prereqID = iter.next();
                Course prereqCourse = university.getCourseByID(prereqID);
                // Non-existent prerequisite.
                if (prereqCourse == null) {
                    unfulfilledPrereqs.remove(prereqID);
                    iter.remove();
                } else if (pastCourse.getID().equals(prereqCourse.getID())) {
                    unfulfilledPrereqs.remove(prereqID);
                }
            }
        }
        if (unfulfilledPrereqs.size() != 0) {
            return ServerMsg.asERR(
                    String.format("Unable to enroll: Prerequisite '%s' not met.",
                            String.join(", ", unfulfilledPrereqs)));
        }

        Section section = null;
        for (var s : course.getSections()) {
            if (clientSection.getID().equals(s.getID())) {
                section = s;
                break;
            }
        }
        if (section == null) {
            return ServerMsg.asERR(String.format("Section ID '%s' not found.", clientSection.getID()));
        }

        // Check schedule conflict.
        for (var otherSection : student.getCurrentSchedule()) {
            ScheduleEntry[] schedule = otherSection.getSchedule();
            for (var otherEntry : schedule) {
                for (var entry : section.getSchedule()) {
                    if (entry.isOverlap(otherEntry)) {
                        return ServerMsg.asERR(String.format("Unable to enroll: Conflict with section ID '%s'.",
                                otherSection.getID()));
                    }
                }
            }
        }

        EnrollStatus status = section.enrollStudent(student);
        if (status == EnrollStatus.UNSUCCESSFUL) {
            return ServerMsg.asERR("Unable to enroll: Section is full.");
        }

        return ServerMsg.asOK(status);
    }

    public static ServerMsg drop(Section clientSection, Student student, University university) {
        Course clientCourse = clientSection.getCourse();
        Course course = university.getCourseByID(clientCourse.getID());
        if (course == null) {
            return ServerMsg.asERR(String.format("Course ID '%s' not found.", clientCourse.getID()));
        }

        Section section = null;
        for (var s : course.getSections()) {
            if (clientSection.getID().equals(s.getID())) {
                section = s;
                break;
            }
        }
        if (section == null) {
            return ServerMsg.asERR(String.format("Section ID '%s' not found.", clientSection.getID()));
        }

        section.dropStudent(student.getID());
        return ServerMsg.asOK("");
    }
}
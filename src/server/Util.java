package server;

import java.util.HashSet;
import java.util.function.Predicate;

import objects.BodyCourseSearch;
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
         * If unfulfilledPrereqs has something after this loop, the student hasn't
         * cleared all prereqs. This only check one level of prerequisite, so it can
         * false-alarm in rare cases (but it's probably safe to ignore it). ie. if CS
         * 401 explicitly requires both CS 301 and CS 201 (and CS 301 requires CS 201)
         * and the student only clear CS 301 (maybe they get CS 201 equivalent from a
         * different course) then the check will fail with CS 201 as an unfulfilled
         * prereq.
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
            return ServerMsg.asERR(String.format("Unable to enroll: Prerequisite '%s' not met.",
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
            // Skip check if schedule is null
            if (schedule == null || otherSection == null) {
                continue;
            } else {
                for (var otherEntry : schedule) {
                    for (var entry : section.getSchedule()) {
                        if (entry.isOverlap(otherEntry)) {
                            return ServerMsg.asERR(String.format("Unable to enroll: Conflict with section ID '%s'.",
                                    otherSection.getID()));
                        }
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

    /**
     * Drop a student from the provided section.
     * 
     * @param clientSection This must be the object provided by the client, not from
     *                      the server.
     * @param student       The student dropping. This must be the object from the
     *                      server.
     * @param university    The university. This must be the object from the server.
     * @return If success, an {@code OK ServerMsg}. If failed, an
     *         {@code ERR ServerMsg} containing the reason {@code String}.
     */
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

    /**
     * Return courses based on the provided body.
     * 
     * @param body       The body of the client's request.
     * @param university The university. This must be the object from the server.
     * @return A {@code OK ServerMsg} containing 0 or more courses that fits the
     *         filter.
     */
    public static ServerMsg searchCourses(BodyCourseSearch body, University university) {
        final Predicate<Course> prefix_pred = (Course c) -> {
            if (body.getCoursePrefix() != null && !body.getCoursePrefix().isBlank()) {
                return c.getPrefix().toLowerCase().contains(body.getCoursePrefix());
            }
            return true;
        };
        final Predicate<Course> name_pred = (Course c) -> {
            if (body.getCourseName() != null && !body.getCourseName().isBlank()) {
                // NOTE: Consider adding a name field for Course
                return c.getDescription().toLowerCase().contains(body.getCourseName());
            }
            return true;
        };
        final Predicate<Course> number_pred = (Course c) -> {
            if (body.getCourseNumber() != null && !body.getCourseNumber().isBlank()) {
                return c.getNumber().toLowerCase().contains(body.getCourseNumber());
            }
            return true;
        };
        final Predicate<Course> instructor_pred = (Course c) -> {
            if (body.getInstructorName() != null && !body.getInstructorName().isBlank()) {
                for (var section : c.getSections()) {
                    if (section.getInstructor().getName().toLowerCase().contains(body.getInstructorName())) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        };

        var res = university.getCoursesByFilter((Course c) -> {
            return prefix_pred.test(c) && name_pred.test(c) && number_pred.test(c) && instructor_pred.test(c);
        });
        return ServerMsg.asOK(res.toArray(new Course[0]));
    }
}

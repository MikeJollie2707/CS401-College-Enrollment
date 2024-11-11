package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;

import objects.*;

public class StudentSessionHandler extends SessionHandler {
    private Student student;

    public StudentSessionHandler(Socket socket, ObjectInputStream istream, ObjectOutputStream ostream,
            University university, Student student) {
        super(socket, istream, ostream, university);
        this.student = student;
    }

    @Override
    public void run() {
        try {
            ostream.writeObject(ServerMsg.asOK(student.getID()));
            while (true) {
                ClientMsg req = null;
                ServerMsg resp = null;

                try {
                    req = (ClientMsg) istream.readObject();
                } catch (ClassNotFoundException err) {
                    ostream.writeObject(ServerMsg.asERR("Conversion failed, 'ClientMsg' expected."));
                    continue;
                }

                if (req.isEndpoint("CREATE", "logout")) {
                    resp = logout(req);
                    ostream.writeObject(resp);
                    // Just in case it fails to logout somehow.
                    if (resp.isOk()) {
                        break;
                    }
                }

                if (req.isEndpoint("CREATE", "message")) {
                    resp = capitalizeMessage(req);
                } else if (req.isEndpoint("GET", "courses")) {
                    resp = fetchCourses(req);
                } else if (req.isEndpoint("GET", "schedule")) {
                    resp = fetchSchedule(req);
                } else if (req.isEndpoint("CREATE", "enroll")) {
                    resp = enroll(req);
                } else if (req.isEndpoint("CREATE", "drop")) {
                    resp = drop(req);
                } else if (req.isEndpoint("GET", "past-enroll")) {
                    resp = fetchPastEnrollment(req);
                }

                if (resp != null) {
                    ostream.writeObject(resp);
                } else {
                    ostream.writeObject(ServerMsg.asERR(
                            String.format("Endpoint '%s %s' is not available.", req.getMethod(), req.getResource())));
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    // NOTE: Mock service, will delete later.
    private ServerMsg capitalizeMessage(ClientMsg req) {
        String message = (String) req.getBody();
        return ServerMsg.asOK(message.toUpperCase());
    }

    private ServerMsg logout(ClientMsg req) {
        return ServerMsg.asOK("logout");
    }

    private ServerMsg fetchCourses(ClientMsg req) {
        return null;
    }

    private ServerMsg fetchSchedule(ClientMsg req) {
        // Maybe return ArrayList instead? Idk it's currently easier this way.
        var enrolling = student.getCurrentSchedule().toArray(new Section[0]);
        return ServerMsg.asOK(enrolling);
    }

    private ServerMsg enroll(ClientMsg req) {
        try {
            Section clientSection = (Section) req.getBody();
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
                        if (ScheduleEntry.isOverlap(entry, otherEntry)) {
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
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    private ServerMsg drop(ClientMsg req) {
        try {
            Section clientSection = (Section) req.getBody();
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
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    private ServerMsg fetchPastEnrollment(ClientMsg req) {
        var pastEnrollments = student.getPastEnrollments().toArray(new Section[0]);
        return ServerMsg.asOK(pastEnrollments);
    }
}

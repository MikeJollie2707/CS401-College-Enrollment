package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

                if (req.isEndpoint("GET", "courses")) {
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

    private ServerMsg logout(ClientMsg req) {
        return ServerMsg.asOK("logout");
    }

    /**
     * The handler for {@code GET courses} requests.
     * 
     * @param req The client's request. The body MUST be of type
     *            {@code BodyCourseSearch}.
     * @return If success (even if there are no matching results), an
     *         {@code OK ServerMsg} containing {@code Course[]} that matches the
     *         filter. If failed, an {@code ERR ServerMsg} containing a reason
     *         {@code String.}
     */
    private synchronized ServerMsg fetchCourses(ClientMsg req) {
        try {
            var body = (BodyCourseSearch) req.getBody();
<<<<<<< Updated upstream
            return Util.searchCourses(body, university);
=======
            final Predicate<Course> prefix_pred = (Course c) -> {
                if (body.getCoursePrefix() != null && !body.getCoursePrefix().isBlank()) {
                    return c.getPrefix().toLowerCase().contains(body.getCoursePrefix());
                }
                return true;
            };
            final Predicate<Course> name_pred = (Course c) -> {
                if (body.getCourseNumber() != null && !body.getCourseNumber().isBlank()) {
                    return c.getNumber().toLowerCase().contains(body.getCourseNumber());
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
                return prefix_pred.test(c) &&
                        name_pred.test(c) &&
                        number_pred.test(c) && 
                        instructor_pred.test(c);
            });
            return ServerMsg.asOK(res.toArray(new Course[0]));
>>>>>>> Stashed changes
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    /**
     * The handler for {@code GET schedule} requests.
     * 
     * @param req The client's request. The body is not required.
     * @return An {@code OK ServerMsg} containing {@code Section[]}.
     */
    private synchronized ServerMsg fetchSchedule(ClientMsg req) {
        // Maybe return ArrayList instead? Idk it's currently easier this way.
        var enrolling = student.getCurrentSchedule().toArray(new Section[0]);
        return ServerMsg.asOK(enrolling);
    }

    /**
     * The handler for {@code CREATE enroll} requests.
     * 
     * @param req The client's request. The body MUST be of type {@code Section}.
     * @return If success, an {@code OK ServerMsg} containing {@code EnrollStatus}.
     *         If failed, an {@code ERR ServerMsg} containing the reason
     *         {@code String}.
     */
    private synchronized ServerMsg enroll(ClientMsg req) {
        try {
            Section clientSection = (Section) req.getBody();
            return Util.enroll(clientSection, student, university);
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    /**
     * The handler for {@code CREATE drop} requests.
     * 
     * @param req The client's request. The body MUST be of type {@code Section}.
     * @return If success, an {@code OK ServerMsg}. If failed, an
     *         {@code ERR ServerMsg} containing the reason {@code String}.
     */
    private synchronized ServerMsg drop(ClientMsg req) {
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

    /**
     * The handler for {@code GET past-enroll} requests.
     * 
     * @param req The client's request. The body is not required.
     * @return An {@code OK ServerMsg} containing {@code Section[]}.
     */
    private synchronized ServerMsg fetchPastEnrollment(ClientMsg req) {
        var pastEnrollments = student.getPastEnrollments().toArray(new Section[0]);
        return ServerMsg.asOK(pastEnrollments);
    }
}

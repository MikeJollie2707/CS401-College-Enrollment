package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import objects.*;

public class AdminSessionHandler extends SessionHandler {
    private Administrator admin;

    public AdminSessionHandler(Socket socket, ObjectInputStream istream, ObjectOutputStream ostream,
            University university, Administrator admin) {
        super(socket, istream, ostream, university);
        this.admin = admin;
    }

    @Override
    public void run() {
        try {
            // NOTE: Temporary, may return some more info like admin name and stuff.
            // Probably will just send the entire Admin object tbh but will see how it goes.
            ostream.writeObject(ServerMsg.asOK(admin.getID()));
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
                } else if (req.isEndpoint("GET", "report")) {
                    resp = fetchReport(req);
                } else if (req.isEndpoint("CREATE", "student")) {
                    resp = createStudent(req);
                } else if (req.isEndpoint("CREATE", "section")) {
                    resp = addSection(req);
                } else if (req.isEndpoint("EDIT", "section")) {
                    resp = editSection(req);
                } else if (req.isEndpoint("DELETE", "section")) {
                    resp = delSection(req);
                } else if (req.isEndpoint("CREATE", "course")) {
                    resp = addCourse(req);
                } else if (req.isEndpoint("EDIT", "course")) {
                    resp = editCourse(req);
                } else if (req.isEndpoint("DELETE", "course")) {
                    resp = delCourse(req);
                } else if (req.isEndpoint("CREATE", "enroll-student")) {
                    resp = enrollStudent(req);
                } else if (req.isEndpoint("CREATE", "drop-student")) {
                    resp = dropStudent(req);
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

    private synchronized ServerMsg fetchCourses(ClientMsg req) {
        return null;
    }

    private synchronized ServerMsg fetchReport(ClientMsg req) {
        return null;
    }

    /**
     * The handler for {@code CREATE student} requests.
     * 
     * @param req The client's request. The body MUST be of type {@code Student}.
     * @return If success, an {@code OK ServerMsg} containing the added
     *         {@code Student}. If failed, an {@code ERR ServerMsg} containing the
     *         reason {@code String}.
     */
    private synchronized ServerMsg createStudent(ClientMsg req) {
        try {
            Student clientStudent = (Student) req.getBody();
            var studentMapping = university.getStudents();
            if (studentMapping.containsKey(clientStudent.getID())) {
                return ServerMsg.asERR(String.format("Student with ID '%s' already existed.", clientStudent.getID()));
            }
            Student student = new Student(clientStudent.getName(), clientStudent.getAccount());
            university.addStudent(student);
            return ServerMsg.asOK(student);
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    /**
     * The handler for {@code CREATE section} requests.
     * 
     * @param req The client's request. The body MUST be of type {@code Section}.
     * @return If success, an {@code OK ServerMsg} containing the added
     *         {@code Section}. If failed, an {@code ERR ServerMsg} containing the
     *         reason {@code String}.
     */
    private synchronized ServerMsg addSection(ClientMsg req) {
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
            if (section != null) {
                return ServerMsg.asERR(String.format("Section ID '%s' already existed.", clientSection.getID()));
            }

            Instructor clientInstructor = clientSection.getInstructor();
            var instructorMapping = university.getInstructors();
            if (!instructorMapping.containsKey(clientInstructor.getID())) {
                return ServerMsg.asERR(String.format("Instructor ID '%s' not found.", clientInstructor.getID()));
            }

            Instructor instructor = instructorMapping.get(clientInstructor.getID());
            // TODO: Check for instructor availability for this section.

            section = new Section(
                    course,
                    clientSection.getNumber(),
                    clientSection.getMaxCapacity(),
                    clientSection.getMaxWaitlistSize(),
                    instructor);
            course.insertSection(section);
            return ServerMsg.asOK(section);
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    /**
     * The handler for {@code EDIT section} requests.
     * 
     * @param req The client's request. The body MUST be of type {@code Section}.
     * @return If success, an {@code OK ServerMsg} containing the edited
     *         {@code Section}. If failed, an {@code ERR ServerMsg} containing the
     *         reason {@code String}.
     */
    private synchronized ServerMsg editSection(ClientMsg req) {
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

            section.setActiveState(clientSection.isActive());
            section.setMaxCapacity(clientSection.getMaxCapacity());
            section.setMaxWaitSize(clientSection.getMaxWaitlistSize());
            section.setNumber(clientSection.getNumber());
            section.setSchedule(clientSection.getSchedule());

            // TODO: Update instructor

            return ServerMsg.asOK(section);
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    private synchronized ServerMsg delSection(ClientMsg req) {
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

            course.delSection(section.getID());
            return ServerMsg.asOK("");
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    /**
     * The handler for {@code CREATE course} requests.
     * 
     * @param req The client's request. The body MUST be of type {@code Course}.
     * @return If success, an {@code OK ServerMsg}. If failed, an
     *         {@code ERR ServerMsg} containing the reason {@code String}.
     */
    private synchronized ServerMsg addCourse(ClientMsg req) {
        try {
            Course clientCourse = (Course) req.getBody();
            Course course = university.getCourseByID(clientCourse.getID());
            if (course != null) {
                return ServerMsg.asERR(String.format("Course ID '%s' already existed.", course.getID()));
            }

            // Don't copy over the sections.
            course = new Course(clientCourse.getPrefix(), clientCourse.getNumber(), clientCourse.getDescription());
            for (var prereq : clientCourse.getPrerequisites()) {
                Course prereqCourse = university.getCourseByID(prereq);
                if (prereqCourse == null) {
                    // NOTE: Two options: Either skip, or return error.
                }
                course.insertPrereq(prereqCourse);
            }
            try {
                university.addCourse(course);
            } catch (RuntimeException err) {
                // Existence check is done earlier so this is most likely prereq issue.
                return ServerMsg.asERR(err.getMessage());
            }

            return ServerMsg.asOK(course);
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    private synchronized ServerMsg editCourse(ClientMsg req) {
        return null;
    }

    private synchronized ServerMsg delCourse(ClientMsg req) {
        try {
            Course clientCourse = (Course) req.getBody();
            Course course = university.getCourseByID(clientCourse.getID());
            if (course == null) {
                return ServerMsg.asERR(String.format("Course ID '%s' not found.", clientCourse.getID()));
            }

            university.delCourse(course.getID());
            return ServerMsg.asOK("");
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    private ServerMsg enrollStudent(ClientMsg req) {
        try {
            var body = (BodyEnrollOrDropAs) req.getBody();
            String studentID = body.getStudentID();

            if (!university.getStudents().containsKey(studentID)) {
                return ServerMsg.asERR(String.format("Student ID '%s' not found.", studentID));
            }

            return ServerMsg.asOK("");
        } catch (ClassCastException err) {
            return ServerMsg.asERR(String.format("%s", err.getMessage()));
        }
    }

    private ServerMsg dropStudent(ClientMsg req) {
        return null;
    }
}

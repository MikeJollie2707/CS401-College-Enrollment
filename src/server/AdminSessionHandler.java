package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.stream.Collectors;

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
            ostream.writeObject(ServerMsg.asOK(new BodyLoginSuccess("admin", admin)));
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

                try {
                    if (req.isEndpoint("GET", "courses")) {
                        resp = fetchCourses(req);
                    } else if (req.isEndpoint("GET", "report")) {
                        resp = fetchReport(req);
                    } else if (req.isEndpoint("GET", "students")) {
                        resp = fetchStudents(req);
                    } else if (req.isEndpoint("GET", "instructors")) {
                        resp = fetchInstructors(req);
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
                } catch (Exception err) {
                    err.printStackTrace();
                    resp = ServerMsg.asERR(String.format("Internal error: %s", err.getMessage()));
                }

                if (resp != null) {
                    ostream.writeUnshared(resp);
                    ostream.reset();
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

    private synchronized ServerMsg fetchCourses(ClientMsg req) {
        var body = (BodyCourseSearch) req.getBody();
        return Util.searchCourses(body, university);
    }

    private synchronized ServerMsg fetchReport(ClientMsg req) {
        /**
         * - Course count
         * - Active section count
         * - Entries with {sectionID, enrollCount}
         * - (Maybe?) Top 3 highest+lowest enroll count
         * - (Maybe?) Avg enroll per section.
         * 
         * The report feature feels like it's just client getting all courses in the
         * uni,
         * then do all the calc on their own.
         * Cuz for each section entry the client want to know what course is it, who's
         * teaching,
         * what's the time and format. That's basically a simplified view of a section.
         */
        return null;
    }

    private synchronized ServerMsg fetchStudents(ClientMsg req) {
        var students = university.getStudents().keySet().toArray(new Student[0]);
        return ServerMsg.asOK(students);
    }

    private synchronized ServerMsg fetchInstructors(ClientMsg req) {
        var instructors = university.getInstructors().keySet().toArray(new Instructor[0]);
        return ServerMsg.asOK(instructors);
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
        Student clientStudent = (Student) req.getBody();
        Student student = new Student(clientStudent.getName(), clientStudent.getAccount());
        try {
            university.addStudent(student);
        } catch (IllegalArgumentException err) {
            return ServerMsg.asERR(err.getMessage());
        }
        return ServerMsg.asOK(student);
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
        Section clientSection = (Section) req.getBody();
        Course clientCourse = clientSection.getCourse();
        Course course = university.getCourseByID(clientCourse.getID());
        if (course == null) {
            return ServerMsg.asERR(String.format("Course ID '%s' not found.", clientCourse.getID()));
        }

        // We don't check for section ID for 2 reasons:
        // 1. Client doesn't have the correct section ID anyway cuz it's autoincrement.
        // 2. It makes more sense to check for instructor availability.

        Instructor clientInstructor = clientSection.getInstructor();
        var instructors = university.getInstructors().values();
        var found = instructors.stream()
                .filter((i) -> i.getName().equals(clientInstructor.getName()))
                .collect(Collectors.toList())
                .toArray(new Instructor[0]);
        Instructor instructor = null;
        if (found.length < 1) {
            // return ServerMsg.asERR(String.format("Instructor '%s' not found.",
            // clientInstructor.getName()));
            instructor = new Instructor(clientInstructor.getName(), null);
        } else {
            instructor = found[0];
        }

        Section conflictedSection = Util.findOverlap(clientSection, instructor.getTeaching());
        if (conflictedSection != null) {
            return ServerMsg
                    .asERR(String.format("Unable to create section: Instructor has conflict with section ID '%s'.",
                            conflictedSection.getID()));
        }

        Section section = new Section(
                course,
                clientSection.getNumber(),
                clientSection.getMaxCapacity(),
                clientSection.getMaxWaitlistSize(),
                instructor);
        section.setSchedule(clientSection.getSchedule());
        course.insertSection(section);
        instructor.teachSection(section);
        return ServerMsg.asOK(section);
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
        Section clientSection = (Section) req.getBody();
        Course clientCourse = clientSection.getCourse();
        Course course = university.getCourseByID(clientCourse.getID());
        if (course == null) {
            return ServerMsg.asERR(String.format("Course ID '%s' not found.", clientCourse.getID()));
        }

        Section section = null;
        for (var s : course.getSections()) {
            if (clientSection.getNumber().equals(s.getNumber())) {
                section = s;
                break;
            }
        }
        if (section == null) {
            return ServerMsg.asERR(String.format("Section ID '%s' not found.", clientSection.getID()));
        }

        if (clientSection.getStatus() == SectionStatus.INACTIVE) {
            return ServerMsg.asERR("Operation not permitted.");
        }

        Instructor clientInstructor = clientSection.getInstructor();
        var instructors = university.getInstructors().values();
        var found = instructors.stream()
                .filter((i) -> i.getName().equals(clientInstructor.getName()))
                .collect(Collectors.toList())
                .toArray(new Instructor[0]);
        if (found.length < 1) {
            return ServerMsg.asERR(String.format("Instructor '%s' not found.", clientInstructor.getName()));
        }

        Instructor instructor = found[0];

        var conflictedSection = Util.findOverlap(section, instructor.getTeaching());
        if (conflictedSection != null) {
            return ServerMsg.asERR(
                    String.format("The new instructor is not available for this section due to section '%s'.",
                            conflictedSection.getID()));
        }

        section.setNumber(clientSection.getNumber());
        section.setSchedule(clientSection.getSchedule());

        // Order of setters for this part matters.

        // Waitlist first, then capacity, to avoid dropping students prematurely.
        section.setMaxWaitSize(clientSection.getMaxWaitlistSize());
        section.setMaxCapacity(clientSection.getMaxCapacity());

        Instructor oldInstructor = section.getInstructor();
        oldInstructor.dropSection(section);
        section.setInstructor(instructor);

        // Status last to finalize stuff.
        section.setStatus(clientSection.getStatus());

        return ServerMsg.asOK(section);
    }

    /**
     * The handler for {@code DELETE section} requests.
     * 
     * @param req The client's request. The body MUST be of type {@code Section}.
     * @return If success, an {@code OK ServerMsg}. If failed, an
     *         {@code ERR ServerMsg} containing the reason {@code String}.
     */
    private synchronized ServerMsg delSection(ClientMsg req) {
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

        section.setStatus(SectionStatus.INACTIVE);
        course.delSection(section.getID());
        return ServerMsg.asOK("");
    }

    /**
     * The handler for {@code CREATE course} requests.
     * 
     * @param req The client's request. The body MUST be of type {@code Course}.
     * @return If success, an {@code OK ServerMsg}. If failed, an
     *         {@code ERR ServerMsg} containing the reason {@code String}.
     */
    private synchronized ServerMsg addCourse(ClientMsg req) {
        Course clientCourse = (Course) req.getBody();
        Course course = university.getCourseByID(clientCourse.getID());
        if (course != null) {
            return ServerMsg.asERR(String.format("Course ID '%s' already existed.", course.getID()));
        }

        // Don't copy over the sections.
        course = new Course(clientCourse.getPrefix(), clientCourse.getNumber(), clientCourse.getName(),
                clientCourse.getDescription());
        for (var prereq : clientCourse.getPrerequisites()) {
            Course prereqCourse = university.getCourseByID(prereq);
            if (prereqCourse == null) {
                // Skip is a safe option.
                continue;
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
    }

    /**
     * The handler for {@code EDIT course} requests.
     * <p>
     * This endpoint is meant to only edit metadata and prerequisites. It is NOT
     * meant to be used to edit course prefix, course number, or sections. To edit
     * course prefix or course number, use {@code DELETE course}. To edit sections,
     * use {@code EDIT section}.
     * 
     * @param req The client's request. The body MUST be of type {@code Course}.
     * @return If success, an {@code OK ServerMsg} containing the edited course. If
     *         failed, an {@code ERR ServerMsg} containing the reason
     *         {@code String}.
     */
    private synchronized ServerMsg editCourse(ClientMsg req) {
        Course clientCourse = (Course) req.getBody();
        Course course = university.getCourseByID(clientCourse.getID());
        if (course == null) {
            return ServerMsg.asERR(String.format("Course ID '%s' not found.", clientCourse.getID()));
        }

        Course newCourse = new Course(clientCourse.getPrefix(), clientCourse.getNumber(), clientCourse.getName(),
                clientCourse.getDescription());
        for (var prereq : clientCourse.getPrerequisites()) {
            Course prereqCourse = university.getCourseByID(prereq);
            if (prereqCourse == null) {
                // Skip is a safe option.
                continue;
            }
            newCourse.insertPrereq(prereqCourse);
        }
        // Move sections from old to new course.
        for (var section : course.getSections()) {
            section.setCourse(newCourse);
            newCourse.insertSection(section);
        }

        try {
            university.editCourse(newCourse);
        } catch (RuntimeException err) {
            // Existence check is done earlier so this is most likely prereq issue.
            return ServerMsg.asERR(err.getMessage());
        }

        return ServerMsg.asOK(newCourse);
    }

    /**
     * The handler for {@code DELETE course} requests.
     * 
     * @param req The client's request. The body MUST be of type {@code Course}.
     * @return If success, an {@code OK ServerMsg}. If failed, an
     *         {@code ERR ServerMsg} containing the reason {@code String}.
     */
    private synchronized ServerMsg delCourse(ClientMsg req) {
        Course clientCourse = (Course) req.getBody();
        Course course = university.getCourseByID(clientCourse.getID());
        if (course == null) {
            return ServerMsg.asERR(String.format("Course ID '%s' not found.", clientCourse.getID()));
        }

        for (var section : course.getSections()) {
            // Don't touch the completed ones because they're "archived".
            if (section.getStatus() == SectionStatus.ACTIVE) {
                section.setStatus(SectionStatus.INACTIVE);
            }
        }
        university.delCourse(course.getID());
        return ServerMsg.asOK("");
    }

    /**
     * The handler for {@code CREATE enroll-student} requests.
     * 
     * @param req The client's request. The body MUST be of type
     *            {@code BodyEnrollOrDropAs}.
     * @return If success, an {@code OK ServerMsg} containing the
     *         {@code EnrollStatus}. If failed, an {@code ERR ServerMsg} containing
     *         the reason {@code String}.
     */
    private synchronized ServerMsg enrollStudent(ClientMsg req) {
        var body = (BodyEnrollOrDropAs) req.getBody();
        String studentID = body.getStudentID();

        if (!university.getStudents().containsKey(studentID)) {
            return ServerMsg.asERR(String.format("Student ID '%s' not found.", studentID));
        }
        Student student = university.getStudents().get(studentID);

        Section clientSection = body.getSection();
        return Util.enroll(clientSection, student, university);
    }

    /**
     * The handler for {@code CREATE drop-student} requests.
     * 
     * @param req The client's request. The body MUST be of type
     *            {@code BodyEnrollOrDropAs}.
     * @return If success, an {@code OK ServerMsg}. If failed, an
     *         {@code ERR ServerMsg} containing the reason {@code String}.
     */
    private ServerMsg dropStudent(ClientMsg req) {
        var body = (BodyEnrollOrDropAs) req.getBody();
        String studentID = body.getStudentID();

        if (!university.getStudents().containsKey(studentID)) {
            return ServerMsg.asERR(String.format("Student ID '%s' not found.", studentID));
        }
        Student student = university.getStudents().get(studentID);

        Section clientSection = body.getSection();
        return Util.drop(clientSection, student, university);
    }
}

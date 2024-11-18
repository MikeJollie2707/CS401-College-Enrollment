package objects;

public class BodyEnrollOrDropAs {
    private String studentID;
    private Course course;

    public BodyEnrollOrDropAs(String studentID, Course course) {
        if (studentID == null || course == null) {
            throw new NullPointerException("Arguments for constructor must not be null.");
        }

        this.studentID = studentID;
        this.course = course;
    }

    public String getStudentID() {
        return studentID;
    }

    public Course getCourse() {
        return course;
    }
}

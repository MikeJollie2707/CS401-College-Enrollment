package objects;

public class BodyEnrollOrDropAs {
    private String studentID;
    private Section section;

    public BodyEnrollOrDropAs(String studentID, Section section) {
        if (studentID == null || section == null) {
            throw new NullPointerException("Arguments for constructor must not be null.");
        }

        this.studentID = studentID;
        this.section = section;
    }

    public String getStudentID() {
        return studentID;
    }

    public Section getSection() {
        return section;
    }
}

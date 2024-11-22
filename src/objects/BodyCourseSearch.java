package objects;
import java.io.Serializable;

import java.io.Serializable;


public class BodyCourseSearch implements Serializable {
    private String course_name;
    private String course_prefix;
    private String course_number;
    private String instructor_name;

    public BodyCourseSearch() {
        course_name = "";
        course_prefix = "";
        course_number = "";
        instructor_name = "";
    }

    public String getCourseName() {
        return course_name;
    }

    public void setCourseName(String course_name) {
        this.course_name = course_name;
    }

    public String getCoursePrefix() {
        return course_prefix;
    }

    public void setCoursePrefix(String course_prefix) {
        this.course_prefix = course_prefix;
    }

    public String getCourseNumber() {
        return course_number;
    }

    public void setCourseNumber(String course_number) {
        this.course_number = course_number;
    }

    public String getInstructorName() {
        return instructor_name;
    }

    public void setInstructorName(String instructor) {
        this.instructor_name = instructor;
    }

    
}

package client;

import java.util.HashMap;
import java.util.Map;
import objects.Course;
import objects.Section;
import objects.Student;

public class CourseState {
    private Course course;
    private Student student;
    private Map<String, SectionState> sectionStates;
    
    public CourseState(Course course, Student student) {
        this.course = course;
        this.student = student;
        this.sectionStates = new HashMap<>();
        
        // Initialize section states based on current enrollments
        for (Section section : course.getSections()) {
            SectionState sectionState = new SectionState(section, student);
            sectionStates.put(section.getID(), sectionState);
        }
    }
    
    public Course getCourse() {
        return course;
    }
    
    public SectionState getSectionState(Section section) {
        return sectionStates.get(section.getID());
    }
    
    public Map<String, SectionState> getSectionStates() {
        return sectionStates;
    }
}
package client;

import objects.Section;
import objects.Student;

public class SectionState {
    private Section section;
    private Student student;
    private boolean isEnrolled;
    private boolean isWaitlisted;
    
    public SectionState(Section section, Student student) {
        this.section = section;
        this.student = student;
        // enrolled and waitlisted are the only states, otherwise it just reverts to default enroll button
        this.isEnrolled = section.getEnrolled().contains(student);
        this.isWaitlisted = section.getWaitlisted().contains(student);
    }
    
    public boolean isEnrolled() {
        return isEnrolled;
    }
    
    public boolean isWaitlisted() {
        return isWaitlisted;
    }
    
    public void setEnrolled(boolean enrolled) {
        this.isEnrolled = enrolled;
    }
    
    public void setWaitlisted(boolean waitlisted) {
        this.isWaitlisted = waitlisted;
    }
    
    public Section getSection() {
        return section;
    }
}
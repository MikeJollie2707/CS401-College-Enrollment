import java.util.List;

public class Section {
    private String id;
    private Course course;
    private String number;
    private Boolean active;
    private int max_capacity;
    private int max_wait;
    private Instructor instructor;
    private List<Student> enrolled;
    private List<Student> waitlisted;
    private ScheduleEntry[] schedule;

    public Section(Course course, String number, int max_capacity, int max_wait, Instructor instructor) {
        this.course = course;
        this.number = number;
        this.max_capacity = max_capacity;
        this.max_wait = max_wait;
        this.instructor = instructor;
    }

    public EnrollStatus enrollStudent(Student student) {
        if (enrolled.size() < max_capacity) {
            enrolled.add(student);
            return EnrollStatus.ENROLLED;
        } else if (waitlisted.size() < max_wait) {
            waitlisted.add(student);
            return EnrollStatus.WAITLISTED;
        }
        return EnrollStatus.UNSUCCESSFUL;
    }
    public void dropStudent(String studentID) {
        for (int i = 0; i < enrolled.size(); i++) {
            if (enrolled.get(i).getID().equals(studentID)) {
                enrolled.remove(i);
                return;
            }
        }

        for (int i = 0; i < waitlisted.size(); i++) {
            if (waitlisted.get(i).getID().equals(studentID)) {
                waitlisted.remove(i);
                return;
            }
        }
    }
    public boolean isFull() {
        return (max_capacity + max_wait) == enrolled.size() + waitlisted.size();
    }

    public boolean isActive() {
        return active;
    }

    public String getID() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public String getNumber() {
        return number;
    }

    public int getMaxCapacity() {
        return max_capacity;
    }

    public int getMaxWaitlistSize() {
        return max_wait;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public List<Student> getEnrolled() {
        return enrolled;
    }

    public List<Student> getWaitlisted() {
        return waitlisted;
    }

    public ScheduleEntry[] getSchedule() {
        return schedule;
    }

    public void setNumber(String num) {
        this.number = num;
    }

    public void setActiveState(Boolean state) {
        this.active = state;
    }

    public void setMaxCapacity(int cap) {
        this.max_capacity = cap;
    }

    public void setMaxWaitSize(int wait) {
        this.max_wait = wait;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public void setSchedule(ScheduleEntry[] schedule) {
        this.schedule = schedule;
    }
}

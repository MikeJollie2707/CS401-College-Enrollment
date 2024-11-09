
package objects;

import java.io.Serializable;
import java.util.List;

public class Section implements Serializable {
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
        /**
         * Check if the student already enrolled/waitlisted.
         * It's more convenient to return non-error value cuz if there's error,
         * we'd need to guess what is the error, so make it consistent.
         */
        for (var enrolledStudent : enrolled) {
            if (student.getID().equals(enrolledStudent.getID())) {
                return EnrollStatus.ENROLLED;
            }
        }
        for (var waitlistedStudent : waitlisted) {
            if (student.getID().equals(waitlistedStudent.getID())) {
                return EnrollStatus.WAITLISTED;
            }
        }

        if (enrolled.size() < max_capacity) {
            enrolled.add(student);
            student.enroll(this);
            return EnrollStatus.ENROLLED;
        } else if (waitlisted.size() < max_wait) {
            waitlisted.add(student);
            student.enroll(this);
            return EnrollStatus.WAITLISTED;
        }
        return EnrollStatus.UNSUCCESSFUL;
    }

    public void dropStudent(String studentID) {
        for (int i = 0; i < enrolled.size(); i++) {
            if (enrolled.get(i).getID().equals(studentID)) {
                enrolled.remove(i);
                enrolled.get(i).drop(getID());
                moveToEnroll();
                return;
            }
        }

        for (int i = 0; i < waitlisted.size(); i++) {
            if (waitlisted.get(i).getID().equals(studentID)) {
                waitlisted.remove(i);
                waitlisted.get(i).drop(getID());
                return;
            }
        }
    }

    public boolean isFull() {
        return (enrolled.size() + waitlisted.size()) >= (max_capacity + max_wait);
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

    public void setActiveState(boolean state) {
        this.active = state;
    }

    /**
     * Set the max capacity of this course to the specified value.
     * <p>
     * If the provided value is smaller than the current enroll list, the method
     * will drop as many students as possible (starting from last place) to reach
     * the new value.
     * <p>
     * If the provided value is larger than the current enroll list, the method will
     * move as many students as possible from the waitlist (starting from the first
     * place) to reach the new value.
     * 
     * @param cap The new course capacity.
     */
    public void setMaxCapacity(int cap) {
        if (cap <= 0) {

            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }

        this.max_capacity = cap;

        // If the new capacity is less than the current number of enrolled students,
        // move excess students to waitlist
        while (enrolled.size() > max_capacity) {
            Student removedStudent = enrolled.removeLast();
            waitlisted.addFirst(removedStudent);
        }
        // Force remove trailing students.
        // After the above loop, it's possible some of the last enrolled students
        // moved to the top of the waitlist, so we drop the students that are last on
        // the waitlist.
        setMaxWaitSize(max_wait);

        // If there is space in the enrollment list, move students from waitlist to
        // enrolled
        while (enrolled.size() < max_capacity && !waitlisted.isEmpty()) {
            moveToEnroll();
        }
    }

    /**
     * Set the max waitlist size to the specified value and drop students that
     * exceed this value.
     * <p>
     * If wait < waitlisted.size(), this method will drop students from the last
     * student to the most current.
     * 
     * @param wait A positive value indicating the max size of the waitlist.
     */
    public void setMaxWaitSize(int wait) {
        if (wait <= 0) {
            throw new IllegalArgumentException("Waitlist size must be greater than zero.");
        }

        this.max_wait = wait;

        while (wait < waitlisted.size()) {
            waitlisted.removeLast();
        }

    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public void setSchedule(ScheduleEntry[] schedule) {
        this.schedule = schedule;
    }

    /**
     * Attempt to shift a student from the first place in waitlist to the enrollment
     * list.
     * <p>
     * This method does nothing if there's no one in the waitlist or if the
     * enrollment list is already full.
     */
    private void moveToEnroll() {
        if (!waitlisted.isEmpty() && enrolled.size() < max_capacity) {
            Student student = waitlisted.removeFirst();
            enrolled.add(student);
        }
    }
}

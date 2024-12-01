
package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A serializable class that represents a course's section.
 */
public class Section implements Serializable {
    private static int _id = 0;
    private String id;
    private Course course;
    private String number;
    private SectionStatus status;
    private int max_capacity;
    private int max_wait;
    private Instructor instructor;
    private List<Student> enrolled;
    private List<Student> waitlisted;
    private ScheduleEntry[] schedule;

    /**
     * Construct a {@code Section}
     * 
     * @param course       The course this section belongs to.
     * @param number       The section's number.
     * @param max_capacity The max capacity of this section.
     * @param max_wait     The max waitlist size of this section.
     * @param instructor   The instructor for this section.
     * @throws NullPointerException     If any parameters are null.
     * @throws IllegalArgumentException If {@code max_capacity} is not positive or
     *                                  if {@code max_wait} is negative.
     */
    public Section(Course course, String number, int max_capacity, int max_wait, Instructor instructor) {
        if (course == null || number == null || instructor == null) {
            throw new NullPointerException("Arguments for constructor must not be null.");
        }
        if (max_capacity <= 0 || max_wait < 0) {
            throw new IllegalArgumentException("'max_capacity' must be positive and 'max_wait' must be non-negative.");
        }

        id = String.format("section_%d", _id);
        ++_id;

        this.course = course;
        this.number = number;
        this.max_capacity = max_capacity;
        this.max_wait = max_wait;
        this.instructor = instructor;
        
        this.enrolled = new ArrayList<>();
        this.waitlisted = new ArrayList<>();
        status = SectionStatus.ACTIVE;
    }

    /**
     * Enroll a student into this section. If the student is already in the section,
     * returns their status.
     * 
     * @param student The student to enroll.
     * @return {@code ENROLLED} if the student is enrolled, {@code WAITLISTED} if
     *         the student is put on the waitlist, or {@code UNSUCCESSFUL} if the
     *         section is full or is not active.
     * @throws NullPointerException If {@code student} is null.
     */
    public synchronized EnrollStatus enrollStudent(Student student) {
        /**
         * Check if the student already enrolled/waitlisted.
         * It's more convenient to return non-error value cuz if there's error,
         * we'd need to guess what is the error, so make it consistent.
         */

        if (status != SectionStatus.ACTIVE) {
            return EnrollStatus.UNSUCCESSFUL;
        }

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

    /**
     * Drop a student from this section. If the drop is successful, automatically
     * update the waitlist and capacity if needed.
     * 
     * @param studentID The student's ID to drop.
     * @throws NullPointerException If {@code studentID} is null.
     */
    public synchronized void dropStudent(String studentID) {
        if (studentID == null) {
            throw new NullPointerException();
        }

        for (int i = 0; i < enrolled.size(); i++) {
            if (enrolled.get(i).getID().equals(studentID)) {
                enrolled.get(i).drop(getID());
                enrolled.remove(i);
                moveToEnroll();
                return;
            }
        }

        for (int i = 0; i < waitlisted.size(); i++) {
            if (waitlisted.get(i).getID().equals(studentID)) {
                waitlisted.get(i).drop(getID());
                waitlisted.remove(i);
                return;
            }
        }
    }

    /**
     * Return whether the section is absolutely full.
     * 
     * @return Whether the capacity and waitlist size is reached.
     */
    public synchronized boolean isFull() {
        return (enrolled.size() + waitlisted.size()) >= (max_capacity + max_wait);
    }

    public synchronized SectionStatus getStatus() {
        return status;
    }

    public synchronized String getID() {
        return id;
    }

    public synchronized Course getCourse() {
        return course;
    }

    public synchronized String getNumber() {
        return number;
    }

    public synchronized int getMaxCapacity() {
        return max_capacity;
    }

    public synchronized int getMaxWaitlistSize() {
        return max_wait;
    }

    public synchronized Instructor getInstructor() {
        return instructor;
    }

    public synchronized List<Student> getEnrolled() {
        return enrolled;
    }

    public synchronized List<Student> getWaitlisted() {
        return waitlisted;
    }

    public synchronized ScheduleEntry[] getSchedule() {
        return schedule;
    }

    public synchronized void setNumber(String num) {
        this.number = num;
    }

    /**
     * Set the status of this section.
     * <p>
     * If {@code state} is not {@code ACTIVE}, it'll drop instructors and all
     * students. If {@code state} is {@code COMPLETED}, this section will be
     * considered as finished and aside from dropping all people, also make sure
     * each enrolled student will have this section as their past enrollment.
     * 
     * @param state The state of this section. See above for details.
     */
    public synchronized void setStatus(SectionStatus state) {
        this.status = state;

        if (status != SectionStatus.ACTIVE) {
            instructor.dropSection(this);
            // Need to move these students into a separate array
            // because dropStudent() messes with enrolled.
            Student[] enrolledOnly = enrolled.toArray(new Student[0]);
            for (int i = 0; i < enrolledOnly.length; ++i) {
                Student student = enrolledOnly[i];
                dropStudent(student.getID());

                if (status == SectionStatus.COMPLETED) {
                    var pastSections = student.getPastEnrollments();
                    pastSections.addLast(this);

                    // This is probably not needed.
                    student.setPastEnrollments(pastSections);
                }
            }

            // Clear any previously waitlisted students.
            while (enrolled.size() > 0) {
                dropStudent(enrolled.get(0).getID());
            }

            course.delSection(id);
        }
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
     * @throws IllegalArgumentException If {@code cap} is not positive.
     */
    public synchronized void setMaxCapacity(int cap) {
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
     * If {@code wait < waitlisted.size()}, this method will drop students from the
     * last student to the most current.
     * 
     * @param wait A non-negative value indicating the max size of the waitlist.
     * @throws IllegalArgumentException If {@code wait} is negative.
     */
    public synchronized void setMaxWaitSize(int wait) {
        if (wait < 0) {
            throw new IllegalArgumentException("Waitlist size must be non-negative.");
        }

        this.max_wait = wait;

        while (wait < waitlisted.size()) {
            Student droppedStudent = waitlisted.removeLast();
            droppedStudent.drop(id);
        }

    }

    public synchronized void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public synchronized void setSchedule(ScheduleEntry[] schedule) {
        this.schedule = schedule;
    }

    /**
     * Attempt to shift a student from the first place in waitlist to the enrollment
     * list.
     * <p>
     * This method does nothing if there's no one in the waitlist or if the
     * enrollment list is already full.
     */
    private synchronized void moveToEnroll() {
        if (!waitlisted.isEmpty() && enrolled.size() < max_capacity) {
            Student student = waitlisted.removeFirst();
            enrolled.add(student);
        }
    }
}

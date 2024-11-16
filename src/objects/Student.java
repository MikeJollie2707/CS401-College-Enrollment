package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A serializable class to represent the university student(s) that use the
 * software.
 */
public class Student implements Serializable {
    private static int _id = 0;

    private String id;
    private Account account;
    private String name;
    private List<Section> past_enrollments;
    private List<Section> enrolling;

    /**
     * Construct an {@code Student} with the provided name and account.
     * 
     * @param name    The name (not null).
     * @param account The account (not null).
     * @throws NullPointerException If any parameters are null.
     */
    public Student(String name, Account account) {
        if (name == null || account == null) {
            throw new NullPointerException("Arguments for constructor must not be null.");
        }

        id = String.format("student_%d", _id);
        ++_id;

        this.name = name;
        this.account = account;

        past_enrollments = new ArrayList<>();
        enrolling = new ArrayList<>();
    }

    public synchronized String getID() {
        return id;
    }

    public synchronized Account getAccount() {
        return account;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized List<Section> getPastEnrollments() {
        return past_enrollments;
    }

    public synchronized List<Section> getCurrentSchedule() {
        return enrolling;
    }

    public synchronized void setPastEnrollments(List<Section> p) {
        this.past_enrollments = p;
    }

    /**
     * Naively enroll a section. Always use {@code Section.enrollStudent()} instead.
     * 
     * @param section The section to enroll.
     */
    public synchronized void enroll(Section section) {
        enrolling.addLast(section);
    }

    /**
     * Naively drop a section. Always use {@code Section.dropStudent()} instead.
     * 
     * @param sectionID The section ID to drop.
     */
    public synchronized void drop(String sectionID) {
        for (int i = 0; i < enrolling.size(); ++i) {
            if (enrolling.get(i).getID().equals(sectionID)) {
                enrolling.remove(i);
                return;
            }
        }
    }
}

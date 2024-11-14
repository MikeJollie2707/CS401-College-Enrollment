package objects;

import java.io.Serializable;
import java.util.*;

/**
 * A serializable class that represents a course.
 */
public class Course implements Serializable {
    private static int _id = 0;
    private String id;
    private String prefix;
    private String number;
    private String description;
    private Set<String> prerequisites;
    private List<Section> sections;

    /**
     * Construct a {@code Course}.
     * 
     * @param prefix      The course prefix.
     * @param number      The course number.
     * @param description The description of this course.
     * @throws NullPointerException If any parameters are null.
     */
    public Course(String prefix, String number, String description) {
        id = String.format("course_%d", _id);
        ++_id;

        this.prefix = prefix;
        this.number = number;
        this.description = description;

        prerequisites = new HashSet<>();
        sections = new ArrayList<>();
    }

    /**
     * Naively insert a prerequisite into this course.
     * 
     * @param course The prerequisite course for this course.
     * @throws NullPointerException If {@code course} is null.
     */
    public synchronized void insertPrereq(Course course) {
        prerequisites.add(course.getID());
    }

    /**
     * Remove a prerequisite from this course.
     * 
     * @param courseID The course ID to remove.
     * @throws NullPointerException If {@code courseID} is null.
     */
    public synchronized void delPrereq(String courseID) {
        prerequisites.remove(courseID);
    }

    /**
     * Insert a section into this course.
     * 
     * @param section The section to add.
     * @throws NullPointerException     If {@code section} is null.
     * @throws IllegalArgumentException If {@code section.getCourse()} doesn't refer
     *                                  to this course.
     */
    public synchronized void insertSection(Section section) {
        sections.add(section);
    }

    /**
     * Remove a section from this course. The removed section will still refer to
     * this course.
     * 
     * @param sectionID The section's ID to remove.
     * @throws NullPointerException If {@code sectionID} is null.
     */
    public synchronized void delSection(String sectionID) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getID().equals(sectionID)) {
                sections.remove(i);
                return;
            }
        }
    }

    public synchronized String getID() {
        return id;
    }

    public synchronized String getPrefix() {
        return prefix;
    }

    public synchronized String getNumber() {
        return number;
    }

    public synchronized String getDescription() {
        return description;
    }

    public synchronized Set<String> getPrerequisites() {
        return prerequisites;
    }

    public synchronized List<Section> getSections() {
        return sections;
    }

    public synchronized void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public synchronized void setNumber(String number) {
        this.number = number;
    }

    public synchronized void setDescription(String description) {
        this.description = description;
    }
}

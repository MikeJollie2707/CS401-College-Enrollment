package objects;

import java.io.Serializable;
import java.util.*;

/**
 * A serializable class that represents a course.
 */
public class Course implements Serializable {
    private String prefix;
    private String number;
    private String name;
    private String description;
    private Set<String> prerequisites;
    private List<Section> sections;

    /**
     * Construct a {@code Course}.
     * 
     * @param prefix      The course prefix.
     * @param number      The course number.
     * @param name        The course name.
     * @param description The description of this course.
     * @throws NullPointerException If any parameters are null.
     */
    public Course(String prefix, String number, String name, String description) {
        setPrefix(prefix);
        setNumber(number);
        setName(name);
        setDescription(description);

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
        if (courseID == null) {
            throw new NullPointerException("'courseID' must not be null.");
        }
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
        if (section == null) {
            throw new NullPointerException("'section' must not be null.");
        }
        if (section.getCourse() != this) {
            throw new IllegalArgumentException("The section to be added doesn't refer to this course.");
        }

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
        return String.format("%s %s", prefix, number);
    }

    public synchronized String getPrefix() {
        return prefix;
    }

    public synchronized String getNumber() {
        return number;
    }

    public synchronized String getName() {
        return name;
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

    /**
     * Set the prefix.
     * 
     * @param prefix The prefix to set.
     * @throws NullPointerException If {@code prefix} is null.
     */
    public synchronized void setPrefix(String prefix) {
        if (prefix == null) {
            throw new NullPointerException("'prefix' must not be null.");
        }
        this.prefix = prefix;
    }

    /**
     * Set the course number.
     * 
     * @param number The number to set.
     * @throws NullPointerException If {@code number} is null.
     */
    public synchronized void setNumber(String number) {
        if (number == null) {
            throw new NullPointerException("'number' must not be null.");
        }
        this.number = number;
    }

    public synchronized void setName(String name) {
        if (name == null) {
            throw new NullPointerException("'name' must not be null.");
        }
        this.name = name;
    }

    /**
     * Set the course description.
     * 
     * @param description The description to set.
     * @throws NullPointerException If {@code description} is null.
     */
    public synchronized void setDescription(String description) {
        if (description == null) {
            throw new NullPointerException("'description' must not be null.");
        }
        this.description = description;
    }
}

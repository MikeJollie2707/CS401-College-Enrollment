package objects;

import java.io.Serializable;
import java.util.*;

public class Course implements Serializable {
    private static int _id = 0;
    private String id;
    private String prefix;
    private String number;
    private String description;
    private Set<String> prerequisites;
    private List<Section> sections;

    public Course(String prefix, String number, String description) {
        id = String.format("course_%d", _id);
        ++_id;

        this.prefix = prefix;
        this.number = number;
        this.description = description;

        prerequisites = new HashSet<>();
        sections = new ArrayList<>();
    }

    public synchronized void insertPrereq(Course course) {
        prerequisites.add(course.getID());
    }

    public synchronized void delPrereq(String courseID) {
        for (var prereq: prerequisites) {
            if (prereq.equals(courseID)) {
                prerequisites.remove(prereq);
                return;
            }
        }
    }

    public synchronized void insertSection(Section section) {
        sections.add(section);
    }

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

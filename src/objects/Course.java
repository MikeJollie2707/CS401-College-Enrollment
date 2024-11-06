package objects;

import java.util.*;

public class Course {
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

    public void insertPrereq(Course course) {
        prerequisites.add(course.getID());
    }

    public void delPrereq(String courseID) {
        for (var prereq: prerequisites) {
            if (prereq.equals(courseID)) {
                prerequisites.remove(prereq);
                return;
            }
        }
    }

    public void insertSection(Section section) {
        sections.add(section);
    }

    public void delSection(String sectionID) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getID().equals(sectionID)) {
                sections.remove(i);
                return;
            }
        }
    }

    public String getID() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getPrerequisites() {
        return prerequisites;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

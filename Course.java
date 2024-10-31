import java.util.*;

public class Course {
    private String id;
    private String prefix;
    private String number;
    private String description;
    private List<Course> prerequisites;
    private List<Section> sections;

    public Course(String prefix, String number, String description) {
        this.prefix = prefix;
        this.number = number;
        this.description = description;
    }

    public void insertPrereq(Course course) {
        prerequisites.add(course);
    }
    public void delPrereq(String courseID) {
        for (int i = 0; i < prerequisites.size(); i++) {
            if (prerequisites.get(i).getID().equals(courseID)) {
                prerequisites.remove(i);
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

    public List<Course> getPrerequisites() {
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

package objects;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class University {
    private String id;
    private String name;
    private String location;
    private List<Administrator> admins;
    private Map<String, Course> catalog;
    private Map<String, Student> students;
    private Map<String, Instructor> instructors;

    public University(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<Course>(catalog.values());
        return courses;
    }

    public List<Course> getCoursesByFilter(Predicate<Course> filter) {
        List<Course> courses = catalog.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
        return courses;
    }

    public Course getCourseByID(String courseID) {
        if (catalog.isEmpty()) {
            return null;
        }
        return catalog.get(courseID);
    }

    public void addCourse(Course course) {
        catalog.put(course.getID(), course);
    }

    public void delCourse(String courseID) {
        catalog.remove(getCourseByID(courseID));
    }

    public void editCourse(Course course) {
        Course newCourse = getCourseByID(course.getID());
        newCourse.setDescription(course.getDescription());
        newCourse.setNumber(course.getNumber());
        newCourse.setPrefix(course.getPrefix());
    }

    public void addAdmin(Administrator admin) {
        admins.add(admin);
    }

    public void addStudent(Student student) {
        students.put(student.getID(), student);
    }

    public void addInstructor(Instructor instructor) {
        instructors.put(instructor.getID(), instructor);
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public List<Administrator> getAdmins() {
        return admins;
    }

    public Map<String, Student> getStudents() {
        return students;
    }

    public Map<String, Instructor> getInstructors() {
        return instructors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

package objects;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class University {
    private static int _id = 0;
    private String id;
    private String name;
    private String location;
    private List<Administrator> admins;
    private Map<String, Course> catalog;
    private Map<String, Student> students;
    private Map<String, Instructor> instructors;

    public University(String name, String location) {
        id = String.format("uni_%d", _id);
        ++_id;

        this.name = name;
        this.location = location;

        admins = new ArrayList<>();
        catalog = new HashMap<>();
        students = new HashMap<>();
        instructors = new HashMap<>();
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>(catalog.values());
        return courses;
    }

    public List<Course> getCoursesByFilter(Predicate<Course> filter) {
        List<Course> courses = catalog.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
        return courses;
    }

    /**
     * Return a course in the catalog by its ID.
     * 
     * @param courseID The course's ID to search.
     * @return The requested Course or null if not found.
     */
    public Course getCourseByID(String courseID) {
        if (catalog.isEmpty()) {
            return null;
        }
        return catalog.get(courseID);
    }

    public void addCourse(Course course) {
        if (catalog.containsKey(course.getID())) {
            // TODO: Raise error.
            return;
        }

        if (!isCycle(course, course.getPrerequisites())) {
            catalog.put(course.getID(), course);
        }
    }

    public void delCourse(String courseID) {
        catalog.remove(getCourseByID(courseID).getID());
    }

    public void editCourse(Course newCourse) {
        Course oldCourse = getCourseByID(newCourse.getID());
        if (oldCourse == null) {
            // TODO: Raise error
            return;
        }

        Set<String> newPrereqs = newCourse.getPrerequisites();
        // Check if new prereqs cause cycle.
        if (!isCycle(oldCourse, newPrereqs)) {
            // NOTE: Check for existing prefix+number?
            catalog.put(oldCourse.getID(), newCourse);
        }

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

    /**
     * Check whether the given course would create a cycle for a given list of
     * prerequisites.
     * 
     * @param course  The course to check.
     * @param prereqs The list of course ID. This is usually
     *                {@code course.getPrerequisites()}.
     * @return true if there is a cycle, false otherwise.
     */
    private boolean isCycle(Course course, Set<String> prereqs) {
        if (prereqs == null || prereqs.size() == 0) {
            return false;
        }

        // bfs cuz a course typically doesn't have a lot of prereq
        // and a prereq may span unexpectedly deep.
        Set<String> nextPrereqs = new HashSet<>();
        for (var prereqID : prereqs) {
            /**
             * This is before course existent check bcuz of sth like
             * course0 prereq is course1 and course1 is prereq of course0
             * Then you add course0 and course1 to catalog in that order.
             * If course existent check is before this check, it'll skip and add
             * course1 to catalog, which shouldn't happen (only course0 is allowed).
             */
            if (course.getID().equals(prereqID)) {
                return true;
            }

            Course prereqCourse = getCourseByID(prereqID);
            if (prereqCourse == null) {
                // Remove prereqID from the prereqs somehow?
                // Use a different loop type to do the removal.
                continue;
            }

            for (var nextPrereq : prereqCourse.getPrerequisites()) {
                nextPrereqs.add(nextPrereq);
            }
        }

        return isCycle(course, nextPrereqs);
    }
}

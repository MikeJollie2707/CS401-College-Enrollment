
package objects;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class University implements Serializable {
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

    public synchronized List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>(catalog.values());
        return courses;
    }

    public synchronized List<Course> getCoursesByFilter(Predicate<Course> filter) {
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
    public synchronized Course getCourseByID(String courseID) {
        if (catalog.isEmpty()) {
            return null;
        }
        return catalog.get(courseID);
    }

    public synchronized void addCourse(Course course) {
        if (catalog.containsKey(course.getID())) {
            System.err.println("Error: Course with this ID already exists.");
            return;
        }

        if (!isCycle(course, course.getPrerequisites())) {
            catalog.put(course.getID(), course);
        } else {
            System.err.println("Error: Adding The Course WILL Create a Prerequisite Cycle.");
        }
    }

    public synchronized void delCourse(String courseID) {
        catalog.remove(getCourseByID(courseID).getID());
    }

    public synchronized void editCourse(Course newCourse) {
        Course oldCourse = getCourseByID(newCourse.getID());
        if (oldCourse == null) {

            System.err.println("Error: Course is not found in the catalog.");
            return;
        }

        Set<String> newPrereqs = newCourse.getPrerequisites();
        // Check if new prereqs cause cycle.
        if (!isCycle(oldCourse, newPrereqs)) {
            // NOTE: Check for existing prefix+number
            catalog.put(oldCourse.getID(), newCourse);
        } else {
            System.err.println("Error: Updating the Course will create a Prerequisite Cycle.");
        }

    }

    public synchronized void addAdmin(Administrator admin) {
        admins.add(admin);
    }

    public synchronized void addStudent(Student student) {
        students.put(student.getID(), student);
    }

    public synchronized void addInstructor(Instructor instructor) {
        instructors.put(instructor.getID(), instructor);
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized String getLocation() {
        return location;
    }

    public synchronized List<Administrator> getAdmins() {
        return admins;
    }

    public synchronized Map<String, Student> getStudents() {
        return students;
    }

    public synchronized Map<String, Instructor> getInstructors() {
        return instructors;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized void setLocation(String location) {
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
    private synchronized boolean isCycle(Course course, Set<String> prereqs) {
        if (prereqs == null || prereqs.size() == 0) {
            return false;
        }

        // bfs cuz a course typically doesn't have a lot of prereq
        // and a prereq may span unexpectedly deep.
        Set<String> nextPrereqs = new HashSet<>();
        Queue<String> queue = new LinkedList<>(prereqs);

        while (!queue.isEmpty()) {
            String prereqID = queue.poll();
            if (prereqID.equals(course.getID())) {
                return true;
            }

            if (!nextPrereqs.contains(prereqID)) {
                nextPrereqs.add(prereqID);
                Course prereqCourse = getCourseByID(prereqID);

                if (prereqCourse != null) {
                    queue.addAll(prereqCourse.getPrerequisites());
                }
            }
        }

        return false;

    }
}

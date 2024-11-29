
package objects;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A serializable class storing every info about the university.
 */
public class University implements Serializable {
    private static int _id = 0;
    private String id;
    private String name;
    private String location;
    private List<Administrator> admins;
    private Map<String, Course> catalog;
    private Map<String, Student> students;
    private Map<String, Instructor> instructors;

    /**
     * Construct a {@code University}.
     * 
     * @param name     The name of the university.
     * @param location The address of the university.
     * @throws NullPointerException If any parameters are null.
     */
    public University(String name, String location) {
        id = String.format("uni_%d", _id);

        setName(name);
        setLocation(location);
        ++_id;

        admins = new ArrayList<>();
        catalog = new HashMap<>();
        students = new HashMap<>();
        instructors = new HashMap<>();

    }

    /**
     * Get all courses in the university catalog.
     * 
     * @return All courses in the university catalog.
     */
    public synchronized List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>(catalog.values());
        return courses;
    }

    /**
     * Get all courses in the university catalog that passes the provided filter.
     * 
     * @param filter A function of the form {@code (c: Course) -> boolean}.
     * @return All matching courses in the university catalog.
     * @throws NullPointerException If {@code filter} is null.
     */
    public synchronized List<Course> getCoursesByFilter(Predicate<Course> filter) {
        if (filter == null) {
            throw new NullPointerException("'filter' must not be null.");
        }

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
     * @return The requested {@code Course} or null if not found.
     * @throws NullPointerException If {@code courseID} is null.
     */
    public synchronized Course getCourseByID(String courseID) {
        if (courseID == null) {
            throw new NullPointerException("'courseID' must not be null.");
        }

        if (catalog.isEmpty()) {
            return null;
        }
        return catalog.get(courseID);
    }

    /**
     * Add a course to the university.
     * 
     * @param course The course to add.
     * @throws NullPointerException If {@code course} is null.
     * @throws RuntimeException     If the course already existed OR if the course
     *                              would cause a prerequisite cycle if added.
     */
    public synchronized void addCourse(Course course) {
        if (catalog.containsKey(course.getID())) {
            throw new RuntimeException("Error: Course with this ID already exists.");
        }

        if (!isCycle(course, course.getPrerequisites())) {
            catalog.put(course.getID(), course);
        } else {
            throw new RuntimeException(
                    String.format("Error: Adding course '%s' will create a prerequisite cycle.", course.getID()));
        }
    }

    /**
     * Remove a course from the university.
     * 
     * @param courseID The course's ID to remove.
     * @throws NullPointerException If {@code courseID} is null.
     */
    public synchronized void delCourse(String courseID) {
        catalog.remove(getCourseByID(courseID).getID());
    }

    /**
     * Update an existing course with the new provided course.
     * <p>
     * Aside from course ID (which is used to look up), all remaining values
     * will be updated.
     * 
     * @param newCourse The new course to update.
     * @throws NullPointerException If {@code newCourse} is null.
     * @throws RuntimeException     If the course doesn't exist OR if the new course
     *                              would cause a prerequisite cycle.
     */
    public synchronized void editCourse(Course newCourse) {
        Course oldCourse = getCourseByID(newCourse.getID());
        if (oldCourse == null) {
            throw new RuntimeException("Error: Course is not found in the catalog.");
        }

        Set<String> newPrereqs = newCourse.getPrerequisites();
        // Check if new prereqs cause cycle.
        if (!isCycle(oldCourse, newPrereqs)) {
            catalog.put(oldCourse.getID(), newCourse);
        } else {
            throw new RuntimeException(
                    String.format("Error: Updating course '%s' will create a prerequisite cycle.", newCourse.getID()));
        }

    }

    /**
     * Add an admin.
     * 
     * @param admin
     * @throws NullPointerException     If {@code admin} is null.
     * @throws IllegalArgumentException If the provided student has an account
     *                                  that's too similar to an account within the
     *                                  university.
     */
    public synchronized void addAdmin(Administrator admin) {
        if (isAccountExisted(admin.getAccount())) {
            throw new IllegalArgumentException("An account with the same name already existed.");
        }
        admins.add(admin);
    }

    /**
     * Add a student.
     * 
     * @param student
     * @throws NullPointerException     If {@code student} is null.
     * @throws IllegalArgumentException If the provided student has an account
     *                                  that's too similar to an account within the
     *                                  university.
     */
    public synchronized void addStudent(Student student) {
        if (isAccountExisted(student.getAccount())) {
            throw new IllegalArgumentException("An account with the same name already existed.");
        }
        students.put(student.getID(), student);
    }

    /**
     * Add an instructor.
     * 
     * @param instructor
     * @throws NullPointerException     If {@code instructor} is null.
     * @throws IllegalArgumentException If the provided student has an account
     *                                  that's too similar to an account within the
     *                                  university.
     */
    public synchronized void addInstructor(Instructor instructor) {
        if (instructor.getAccount() != null && isAccountExisted(instructor.getAccount())) {
            throw new IllegalArgumentException("An account with the same name already existed.");
        }
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

    /**
     * Set the name of university.
     * 
     * @param name The name.
     * @throws NullPointerException If {@code name} is null.
     */
    public synchronized void setName(String name) {
        if (name == null) {
            throw new NullPointerException("'name' must not be null.");
        }
        this.name = name;
    }

    /**
     * Set the location of university.
     * 
     * @param location The location/physical address.
     * @throws NullPointerException If {@code location} is null.
     */
    public synchronized void setLocation(String location) {
        if (location == null) {
            throw new NullPointerException("'location' must not be null.");
        }
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
        Set<String> checkedPrereqs = new HashSet<>();
        Queue<String> queue = new LinkedList<>(prereqs);

        while (!queue.isEmpty()) {
            String prereqID = queue.poll();
            if (prereqID.equals(course.getID())) {
                return true;
            }

            if (!checkedPrereqs.contains(prereqID)) {
                checkedPrereqs.add(prereqID);
                Course prereqCourse = getCourseByID(prereqID);

                if (prereqCourse != null) {
                    queue.addAll(prereqCourse.getPrerequisites());
                }
            }
        }

        return false;

    }

    /**
     * Return whether or not this account already existed in the university.
     * 
     * @param account An account.
     * @return
     */
    private synchronized boolean isAccountExisted(Account account) {
        for (var existingStudent : students.values()) {
            Account existingAccount = existingStudent.getAccount();
            if (account.isSimilar(existingAccount)) {
                return true;
            }
        }
        for (var existingInstructor : instructors.values()) {
            Account existingAccount = existingInstructor.getAccount();
            if (existingAccount != null && account.isSimilar(existingAccount)) {
                return true;
            }
        }
        for (var existingAdmin : admins) {
            Account existingAccount = existingAdmin.getAccount();
            if (account.isSimilar(existingAccount)) {
                return true;
            }
        }
        return false;
    }
}

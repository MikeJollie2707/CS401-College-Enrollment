package objects;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        University uni = new University("Foo", "loc");
        Course c1 = new Course("CS", "101", "101");
        Course c2 = new Course("CS", "201", "201");
        Course c3 = new Course("CS", "301", "301");

        c2.insertPrereq(c1);
        c3.insertPrereq(c2);

        uni.addCourse(c1);
        uni.addCourse(c2);
        uni.addCourse(c3);

        print(uni.getAllCourses());

        Course c4 = new Course("CS", "151", "151");
        Course c5 = new Course("CS", "404", "404");
        
        c4.insertPrereq(c2);
        c4.insertPrereq(c1);
        c4.insertPrereq(c5);
        c5.insertPrereq(c1);
        c5.insertPrereq(c4);
        
        uni.addCourse(c4);
        uni.addCourse(c5);

        print(uni.getAllCourses());
    }

    private static void print(List<Course> courses) {
        System.out.println("Courses: ");
        for (var c : courses) {
            System.out.println(c.getDescription());
        }
    }
}

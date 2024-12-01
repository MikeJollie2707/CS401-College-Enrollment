package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import objects.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

class UniversityTest {
	
	private University university;
	private Course course1;
	private Course course2;
	private Course courseWithCycle;
	private Administrator admin;
	private Student student;
	private Instructor instructor; 
	
	@BeforeEach
	void setUp() {
		university = new University("CSU East Bay", "Somewhere on Earth");
		course1 = new Course("CS", "101", "Intro to CS");
		course2 = new Course("CS", "201", "CS but C++");
		university.addCourse(course1);
		
		courseWithCycle = new Course("CS", "301", "Data Structures");
		courseWithCycle.insertPrereq(course1);
		
		admin = new Administrator("Admin1", new Account("admin1", "pass1"));
		student = new Student("Leon", new Account("Kennedy", "pass2"));
		instructor = new Instructor("Smith", new Account("chris", "pass3"));
	}

	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			new University(null, null);
		});
		assertEquals("'name' must not be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorValid() {
		University uc = new University("UC Davis", "Davis");
		assertNotNull(uc);
		assertEquals("UC Davis", uc.getName());
		assertEquals("Davis", uc.getLocation());
	}
	
	@Test
	void testGetAllCourses() {
		List<Course> courses = university.getAllCourses();
		assertNotNull(courses);
		assertEquals(1, courses.size());
		assertTrue(courses.contains(course1));
	}
	
	@Test
	void testGetCoursesByFilterNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			university.getCoursesByFilter(null);
		});
		assertEquals("'filter' must not be null.", exception.getMessage());
	}
	
	@Test
	void testGetCoursesByFilter() {
		Predicate<Course> filter = course -> course.getPrefix().equals("CS");
		List<Course> filteredCourses = university.getCoursesByFilter(filter);
		assertNotNull(filteredCourses);
		assertEquals(1, filteredCourses.size());
		assertTrue(filteredCourses.contains(course1));
	}
	
	@Test
	void testGetCourseByIDNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			university.getCourseByID(null);
		});
		assertEquals("'courseID' must not be null.", exception.getMessage());
	}
	
	@Test
	void testGetCourseByID() {
		Course theCourse = university.getCourseByID(course1.getID());
		assertNotNull(theCourse);
		assertEquals(course1, theCourse);
	}
	
	@Test
	void testAddCourse() {
		university.addCourse(course2);
		Course theCourse = university.getCourseByID(course2.getID());
		assertNotNull(theCourse);
		assertEquals(course2, theCourse);
	}
	
	@Test
	void testAddCourseExisted() {
		Exception exception = assertThrows(RuntimeException.class, () -> {
			university.addCourse(course1);
		});
		assertEquals("Error: Course with this ID already exists.", exception.getMessage());
	}
	
	@Test
	void testDelCourse() {
		university.delCourse(course1.getID());
		assertNull(university.getCourseByID(course1.getID()));
	}
	
	@Test
	void testEditCourse() {
		Course updatedCourse = new Course("CS", "101", "Intro to Programming");
		university.editCourse(updatedCourse);
		
		Course editedCourse = university.getCourseByID(updatedCourse.getID());
		assertNotNull(editedCourse);
		assertEquals("Intro to Programming", editedCourse.getDescription());
	}
		
	@Test
	void testEditCourseNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			university.editCourse(null);
		});
		assertEquals("Error: Course is not found in the catalog.", exception.getMessage());
	}
	
	
	@Test
	void testAddAdmin() {
		university.addAdmin(admin);
		assertTrue(university.getAdmins().contains(admin));
	}
	
	@Test
	void testAddAdminNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			university.addAdmin(null);
		});
		assertEquals("'admin' must not be null.", exception.getMessage());
	}
	
	@Test
	void testAddStudent() {
		university.addStudent(student);
		assertTrue(university.getStudents().containsKey(student.getID()));
		assertEquals(student, university.getStudents().get(student.getID()));
	}
	
	@Test
	void testAddInstructor() {
		university.addInstructor(instructor);
		assertTrue(university.getInstructors().containsKey(instructor.getID()));
		assertEquals(instructor, university.getInstructors().get(instructor.getID()));
	}
	
	@Test
	void testGetName() {
		assertEquals("CSU East Bay", university.getName());
	}
	
	@Test
	void testGetLocation() {
		assertEquals("Somewhere on Earth", university.getLocation());
	}
	
	@Test
	void testGetAdmins() {
		university.addAdmin(admin);
		assertNotNull(university.getAdmins());
		assertEquals(1, university.getAdmins().size());
		assertTrue(university.getAdmins().contains(admin));
	}
	
	@Test
	void testGetStudents() {
		university.addStudent(student);
		Map<String, Student> students = university.getStudents();
		assertNotNull(students);
		assertTrue(students.containsKey(student.getID()));
		assertEquals(student, students.get(student.getID()));
	}
	
	@Test
	void testGetInstructors() {
		university.addInstructor(instructor);
		Map<String, Instructor> instructors = university.getInstructors();
		assertNotNull(instructors);
		assertTrue(instructors.containsKey(instructor.getID()));
		assertEquals(instructor, instructors.get(instructor.getID()));
	}
	
	@Test
	void testSetNameNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			university.setName(null);
		});
		assertEquals("'name' must not be null.", exception.getMessage());
	}
	
	@Test
	void testSetNameValid() {
		university.setName("UC Berkeley");
		assertEquals("UC Berkeley", university.getName());
	}
	
	@Test
	void testSetLocationNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			university.setLocation(null);
		});
		assertEquals("'location' must not be null.", exception.getMessage());
	}
	
	@Test
	void testSetLocationValid() {
		university.setLocation("Berkeley");
		assertEquals("Berkeley", university.getLocation());
	}
	
	@Test
	void testIsCycleNull() {
		assertThrows(NullPointerException.class, () -> {
			university.addCourse(null);
		});
	}
	
	@Test
	void testIsCycleValid() {
		course1.insertPrereq(courseWithCycle);
		
		Exception exception = assertThrows(RuntimeException.class, () -> {
			university.addCourse(courseWithCycle);
		});
		
		assertEquals(
				String.format("Error: Adding course '%s' will create a prerequisite cycle.", courseWithCycle.getID()),
				exception.getMessage());
		
	}
}

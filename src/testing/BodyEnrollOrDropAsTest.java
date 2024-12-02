package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import objects.*;

class BodyEnrollOrDropAsTest {
	
	private BodyEnrollOrDropAs body;
	private Section section;
	private String studentID;
	
	@BeforeEach
	void setUp() {
		studentID = "student1";
		Course course = new Course("CS", "401", "Software Engineering", "Description");
		Instructor instructor = new Instructor("Dr. Smith", new Account("smith", "pass123"));
		section = new Section(course, "1", 30, 10, instructor);
		body = new BodyEnrollOrDropAs(studentID, section);
	}
	
	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			new BodyEnrollOrDropAs(null, section);
		});
		assertEquals("Arguments for constructor must not be null.", exception.getMessage());
		
		exception = assertThrows(NullPointerException.class, () -> {
			new BodyEnrollOrDropAs(studentID, null);
		});
		assertEquals("Arguments for constructor must not be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorValid() {
		assertNotNull(body);
		assertEquals("student1", body.getStudentID());
		assertEquals(section, body.getSection());
	}
	
	@Test
	void testGetStudentID() {
		assertEquals("student1", body.getStudentID());
	}
	
	@Test
	void testGetSection() {
		assertEquals(section, body.getSection());
	}

}

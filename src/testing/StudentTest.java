package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import objects.*;
import java.util.ArrayList;
import java.util.List;

class StudentTest {
	
	private Student student;
	private Account account;
	private Section section1;
	private Section section2;
	private Course course;
	private Instructor instructor; 
	
	@BeforeEach
	void setUp() {
		account = new Account("antioni", "123123");
		student = new Student("Anthony", account);
		instructor = new Instructor("Smith", null);
		course = new Course("CS", "401", "Software Engineering", "Description");
		section1 = new Section(course, "1", 30, 10, instructor);
		section2 = new Section(course, "2", 30, 10, instructor);
	}
	
	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> { 
			new Student(null, null);
		});
		assertEquals("Arguments for constructor must not be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorValid() {
		assertNotNull(student);
		assertEquals("Anthony", student.getName());
		assertEquals(account, student.getAccount());
	}
	
	@Test
	void testGetID() {
		assertNotNull(student.getID());
		assertTrue(student.getID().startsWith("student_"));
	}
	
	@Test
	void testGetAccount() {
		assertEquals(account, student.getAccount());
		// assertEquals("antioni", student.getAccount().getEmail());
	}
	
	@Test
	void testGetName() {
		assertEquals("Anthony", student.getName());
	}
	
	@Test
	void testGetPastEnrollments() {
		List<Section> pastEnrollments = student.getPastEnrollments();
		assertNotNull(pastEnrollments);
		assertTrue(pastEnrollments.isEmpty());
		
	}
	
	@Test
	void testGetCurrentSchedule() {
		List<Section> currentSchedule = student.getCurrentSchedule();
		assertNotNull(currentSchedule);
		assertTrue(currentSchedule.isEmpty());
	}
	
	@Test
	void testSetPastEnrollments() {
		List<Section> pastEnrollments = new ArrayList<>();
		pastEnrollments.add(section1);
		pastEnrollments.add(section2);
		
		student.setPastEnrollments(pastEnrollments);
		
		List<Section> thePastEnrollments = student.getPastEnrollments();
		assertNotNull(thePastEnrollments);
		assertEquals(2, thePastEnrollments.size());
		assertTrue(thePastEnrollments.contains(section1));
		assertTrue(thePastEnrollments.contains(section2));
	}
	
	@Test
	void testEnroll() {
		student.enroll(section1);
		
		List<Section> currentSchedule = student.getCurrentSchedule();
		assertNotNull(currentSchedule);
		assertEquals(1, currentSchedule.size());
		assertTrue(currentSchedule.contains(section1));
	}
	
	@Test
	void testDrop() {
		student.enroll(section1);
		student.enroll(section2);
		
		student.drop(section1.getID());
		
		List<Section> currentSchedule = student.getCurrentSchedule();
		assertEquals(1, currentSchedule.size());
		assertFalse(currentSchedule.contains(section1));
		assertTrue(currentSchedule.contains(section2));
	}
}

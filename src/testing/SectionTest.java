package testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import objects.*;
import java.util.ArrayList;
import java.util.List;

class SectionTest {
	
	private Section section;
	private Course course;
	private Instructor instructor;
	private Student student1;
	private Student student2;
	private ScheduleEntry[] schedule;
	
	@BeforeEach
	void setUp() {
		course = new Course("CS", "401", "Software Engineering");
		instructor = new Instructor("Smith", null);
		section = new Section(course, "1", 30, 10, instructor);
		
		course.insertSection(section);
		schedule = new ScheduleEntry[] {
				new ScheduleEntry("location5", true, java.time.DayOfWeek.MONDAY, 
								java.time.OffsetTime.parse("09:00+00:00"),
								java.time.OffsetTime.parse("10:15+00:00"))
			
		};
		
		section.setSchedule(schedule);
		student1 = new Student("Colin", new Account("coli", "pass1"));
		student2 = new Student("Edwin", new Account("win", "pass2"));
	}
	
	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			new Section(null, null, 30, 10, null);
		});
		assertEquals("Arguments for constructor must not be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorValid() {
		Section validSection = new Section(course, "2", 33, 10, instructor);
		assertNotNull(validSection);
		assertEquals("2", validSection.getNumber());
		assertEquals(course, validSection.getCourse());
		assertEquals(instructor, validSection.getInstructor());
		assertEquals(33, validSection.getMaxCapacity());
		assertEquals(10, validSection.getMaxWaitlistSize());
	}
	
	@Test
	void testConstructorInValid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			new Section(course, "1", -1, -1, instructor);
		});
		assertEquals("'max_capacity' must be positive and 'max_wait' must be non-negative.", exception.getMessage());
	}
	
	@Test
	void testEnrollStudentEnrolled() {
		section.enrollStudent(student1);
		EnrollStatus status = section.enrollStudent(student1);
		assertEquals(EnrollStatus.ENROLLED, status);
	}
	
	@Test
	void testEnrollStudentWaitlisted() {
		Section fullSection = new Section(course, "2", 1, 1, instructor);
		fullSection.enrollStudent(student1);
		fullSection.enrollStudent(student2);
		EnrollStatus status = fullSection.enrollStudent(student2);
		assertEquals(EnrollStatus.WAITLISTED, status);
	}
	
	@Test
	void testEnrollStudentValid() {
		EnrollStatus status = section.enrollStudent(student1);
		assertEquals(EnrollStatus.ENROLLED, status);
		assertTrue(section.getEnrolled().contains(student1));
	}
	
	@Test
	void testEnrollStudentMaxCapacity() {
		Section fullSection = new Section(course, "2", 1, 10, instructor);
		fullSection.enrollStudent(student1);
		EnrollStatus status = fullSection.enrollStudent(student2);
		assertEquals(EnrollStatus.WAITLISTED, status);
		assertTrue(fullSection.getWaitlisted().contains(student2));
	}
	
	@Test
	void testEnrollMaxWaitlist() {
		Section fullSection = new Section(course, "2", 1, 1, instructor);
		fullSection.enrollStudent(student1);
		fullSection.enrollStudent(student2);
		Student student3 = new Student("Kyle", new Account("pork", "pass3"));
		EnrollStatus status = fullSection.enrollStudent(student3);
		assertEquals(EnrollStatus.UNSUCCESSFUL, status);
	}
	
	@Test
	void testDropStudentNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			section.dropStudent(null);
		});
		assertEquals(null, exception.getMessage());
	}
	
	@Test
	void testDropStudentValid() {
		section.enrollStudent(student1);
		assertTrue(section.getEnrolled().contains(student1));
		
		section.dropStudent(student1.getID());
		assertFalse(section.getEnrolled().contains(student1));
	}
	
	@Test
	void testIsFull() {
		for (int i = 0; i < 30; i++)
		{
			section.enrollStudent(new Student("Student" + i, new Account("student" + i + "@gmail.com", "pass" + i)));
		}
		
		for (int i = 0; i < 10; i++)
		{
			section.enrollStudent(new Student("Waitlist" + i, new Account("student" + i + "@gmail.com", "pass" + i)));
		}
		
		assertTrue(section.isFull());
	}
	
	@Test
	void testIsActive() {
		section.setActiveState(true);
		assertTrue(section.isActive());
	}
	
	@Test
	void testGetID() {
		assertEquals("section_7", section.getID());
	}
	
	@Test 
	void testGetCourse() {
		assertEquals(course, section.getCourse());
	}
	
	@Test
	void testGetNumber() {
		assertEquals("1", section.getNumber());
	}
	
	@Test
	void testGetMaxCapacity() {
		assertEquals(30, section.getMaxCapacity());
	}
	
	@Test
	void testGetMaxWaitlistSize() {
		assertEquals(10, section.getMaxWaitlistSize());
	}
	
	@Test
	void testGetInstructor() {
		assertEquals(instructor, section.getInstructor());
	}
	
	@Test
	void testGetEnrolled() {
		section.enrollStudent(student1);
		List<Student> enrolled = section.getEnrolled();
		assertNotNull(enrolled);
		assertTrue(enrolled.contains(student1));
	}
	
	@Test
	void testGetWaitlisted() {
		Section fullSection = new Section(course, "2", 1, 1, instructor);
		fullSection.enrollStudent(student1);
		fullSection.enrollStudent(student2);
		List<Student> waitlisted = fullSection.getWaitlisted();
		
		assertNotNull(waitlisted);
		assertTrue(waitlisted.contains(student2));
	}
	
	@Test
	void testGetSchedule() {
		ScheduleEntry[] retrievedSchedule = section.getSchedule();
		assertNotNull(retrievedSchedule);
		assertArrayEquals(schedule, retrievedSchedule);
	}
}

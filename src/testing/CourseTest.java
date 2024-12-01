package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

class CourseTest {
	
	private Course course;
	private Course prereqCourse;
	private Section section; 
	
	@BeforeEach
	void setUp() {
		course = new Course("CS", "401", "Software Engineering");
		prereqCourse = new Course("CS", "101", "Intro to CS");
		Instructor instructor = new Instructor("Smith", null);
		section = new Section(course, "1", 30, 10, instructor);
		
		course.insertSection(section);
	}
	
	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			new Course(null, "101", "Intro to CS");
		});
		assertEquals("'prefix' must not be null.", exception.getMessage());
		
		exception = assertThrows(NullPointerException.class, () -> {
			new Course("CS", null, "Intro to CS");
		});
		assertEquals("'number' must not be null.", exception.getMessage());
		
		exception = assertThrows(NullPointerException.class, () -> {
			new Course("CS", "101", null);
		});
		assertEquals("'description' must not be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorValid() {
		Course course = new Course("CS", "101", "Intro to CS");
		assertNotNull(course);
		assertEquals("CS", course.getPrefix());
        assertEquals("101", course.getNumber());
        assertEquals("Intro to CS", course.getDescription());
        assertTrue(course.getPrerequisites().isEmpty());
        assertTrue(course.getSections().isEmpty());
	}
	
	@Test
	void testInsertPreReq() {
		course.insertPrereq(prereqCourse);
		Set<String> preReqs = course.getPrerequisites();
		assertTrue(preReqs.contains(prereqCourse.getID()));
	}
	
	@Test
	void testDelPreReqNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			course.delPrereq(null);
		});
	}
	
	@Test
	void testDelPreReqValid() {
		course.insertPrereq(prereqCourse);
		assertTrue(course.getPrerequisites().contains(prereqCourse.getID()));
		course.delPrereq(prereqCourse.getID());
		assertFalse(course.getPrerequisites().contains(prereqCourse.getID()));
	}
	
	@Test
	void testInsertSectionNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			course.insertSection(null);
		});
		assertEquals("'section' must not be null.", exception.getMessage());
	}
	
	@Test
	void testInsertSectionValid() {
		course.insertSection(section);
		List<Section> sections = course.getSections();
		assertTrue(sections.contains(section));
	}
	
	
	//Test Failing 
	@Test
	void testInsertSectionInvalid() {
		Course CSCourse = new Course("CS", "102", "Intro Part 2");
		Section invalidSection = new Section(CSCourse, "1", 30, 10, new Instructor("Teacher1", null));
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.insertSection(invalidSection);
        });
		assertEquals("The section to be added doesn't refer to this course.", exception.getMessage());
	}
	
	
	@Test
	void testDelSection() {
		assertTrue(course.getSections().contains(section));
		course.delSection(section.getID());
		assertFalse(course.getSections().contains(section));
	}
	
	
	@Test
	void testGetID() {
		assertEquals("CS 401", course.getID());
	}
	
	@Test
	void testGetPrefix() {
		assertEquals("CS", course.getPrefix());
	}
	
	@Test
	void testGetNumber() {
		assertEquals("401", course.getNumber());
	}
	
	@Test
	void testGetDescription() {
		assertEquals("Software Engineering", course.getDescription());
	}
	
	@Test
	void testGetPrerequisites() {
		Course prereq = new Course("CS", "301", "Data Structures");
		course.insertPrereq(prereq);
		Set<String> prereqs = course.getPrerequisites();
		assertTrue(prereqs.contains(prereq.getID()));
	}
	
	@Test
	void testGetSections() {
		List<Section> sections = course.getSections();
		assertNotNull(sections);
		assertTrue(sections.contains(section));
	}
	
	@Test
	void testSetPrefixNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
            course.setPrefix(null);
        });
		assertEquals("'prefix' must not be null.", exception.getMessage());
	}
	
	@Test
	void testSetPrefixValid() {
		course.setPrefix("CS");
		assertEquals("CS", course.getPrefix());
	}
	
	@Test
	void testSetNumberNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
            course.setNumber(null);
        });
		assertEquals("'number' must not be null.", exception.getMessage());
	}
	
	@Test
	void testSetNumberValid() {
		course.setNumber("301");
		assertEquals("301", course.getNumber());
	}
	
	@Test
	void testSetDescriptionNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
            course.setDescription(null);
        });
		assertEquals("'description' must not be null.", exception.getMessage());
	}
	
	@Test
	void testSetDescriptionValid() {
		course.setDescription("Data Structures");
		assertEquals("Data Structures", course.getDescription());
	}
}

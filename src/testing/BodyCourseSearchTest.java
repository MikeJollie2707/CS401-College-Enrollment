package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import objects.*;

class BodyCourseSearchTest {
	
	private BodyCourseSearch bodyCourseSearch;
	
	@BeforeEach
	void setUp() {
		bodyCourseSearch = new BodyCourseSearch();
	}
	
	@Test
	void testConstructor() {
		assertNotNull(bodyCourseSearch);
		assertEquals("", bodyCourseSearch.getCourseName());
		assertEquals("", bodyCourseSearch.getCoursePrefix());
		assertEquals("", bodyCourseSearch.getCourseNumber());
		assertEquals("", bodyCourseSearch.getInstructorName());
	}
	
	@Test
	void testGetAndSetCourseName() {
		bodyCourseSearch.setCourseName("Software Engineering");
		assertEquals("Software Engineering", bodyCourseSearch.getCourseName());
	}
	
	@Test
	void testGetAndSetCoursePrefix() {
		bodyCourseSearch.setCoursePrefix("CS");
		assertEquals("CS", bodyCourseSearch.getCoursePrefix());
	}
	
	@Test
	void testGetAndSetCourseNumber() {
		bodyCourseSearch.setCourseNumber("401");
		assertEquals("401", bodyCourseSearch.getCourseNumber());
	}
	
	@Test
	void testGetAndSetInstructorName() {
		bodyCourseSearch.setInstructorName("Dr. Smith");
		assertEquals("Dr. Smith", bodyCourseSearch.getInstructorName());
	}
}

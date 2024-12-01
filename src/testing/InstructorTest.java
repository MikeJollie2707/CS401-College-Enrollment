package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import objects.*;
import org.junit.jupiter.api.BeforeEach;

class InstructorTest {
	
	private Account account; 
	
	@BeforeEach
	void setup() {
		account = new Account("teacher1", "coolone");
	}
	
	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> { 
			new Instructor(null, account);
		});
		assertEquals("'name' must not be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorValid() {
		Instructor instructor = new Instructor("teacher2", account);
		assertNotNull(instructor);
		assertEquals("teacher2", instructor.getName());
		assertEquals(account, instructor.getAccount());
	}
	
	@Test
	void testGetID() {
		Instructor instructor = new Instructor("teacher2", account);
		assertTrue(instructor.getID().startsWith("instructor_"));
	}
	
	@Test
	void testGetAccount() {
		Instructor instructor = new Instructor("teacher3", account);
		Account instructorAccount = instructor.getAccount();
		assertNotNull(instructorAccount);
		assertEquals("teacher1", instructorAccount.getEmail());
		assertTrue(instructorAccount.verify("teacher1", "coolone"));
	}
	
	@Test
	void testGetName() {
		Instructor instructor = new Instructor("teacher3", account);
		assertEquals("teacher3", instructor.getName());
	}
	
}

package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import objects.*;

class BodyLoginTest {

	@Test
	void testConstructorValid() {
		BodyLogin bodyLogin = new BodyLogin("CSU East Bay", "user1", "pass1");
		assertNotNull(bodyLogin);
		assertEquals("CSU East Bay", bodyLogin.getUniName());
		assertEquals("user1", bodyLogin.getLoginID());
		assertEquals("pass1", bodyLogin.getPassword());
	}
	
	@Test
	void testConstructorNullUniName() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			new BodyLogin(null, "user1", "pass1");
		});
		assertEquals("University Name can't be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorNullLoginID() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			new BodyLogin("CSU East Bay", null, "pass1");
		});
		assertEquals("Login ID can't be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorNullPassword() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			new BodyLogin("CSU East Bay", "user1", null);
		});
		assertEquals("Password can't be null.", exception.getMessage());
	}
	
	@Test
	void testGetUniName() {
		BodyLogin bodyLogin = new BodyLogin("CSU East Bay", "user1", "pass1");
		assertEquals("CSU East Bay", bodyLogin.getUniName());
	}
	
	@Test
	void testGetLoginID() {
		BodyLogin bodyLogin = new BodyLogin("CSU East Bay", "user1", "pass1");
		assertEquals("user1", bodyLogin.getLoginID());
	}
	
	@Test
	void testPassword() {
		BodyLogin bodyLogin = new BodyLogin("CSU East Bay", "user1", "pass1");
		assertEquals("pass1", bodyLogin.getPassword());
	}

}

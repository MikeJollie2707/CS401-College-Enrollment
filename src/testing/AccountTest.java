package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import objects.*;

class AccountTest {

	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			new Account(null, "AccountPassword");
		});
		assertEquals("Arguments for constructor must not be null.", exception.getMessage());
		exception = assertThrows(NullPointerException.class, () -> {
			new Account("AccountEmail", null);
		});
		assertEquals("Arguments for constructor must not be null.", exception.getMessage());
	}

	@Test
	void testConstructorValid() {
		Account account = assertDoesNotThrow(() -> new Account("AccountEmail", "AccountPassword"));
		assertNotNull(account);
	}

	@Test
	void testVerify() {
		Account account = new Account("AccountEmail", "AccountPassword");
		assertTrue(account.verify("AccountEmail", "AccountPassword"));
	}

	// @Test
	// void testGetEmail() {
	// 	Account account = new Account("AccountEmail", "AccountPassword");
	// 	assertEquals("AccountEmail", account.getEmail());
	// }
}

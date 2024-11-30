package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import objects.*;
import org.junit.jupiter.api.BeforeEach;

class AdministratorTest {
	
	private Account account; 
	
	@BeforeEach
	void setup() {
		account = new Account("admin1", "secret");
	}
	
	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> { 
			new Administrator(null, account);
		});
		assertEquals("Arguments for constructor must not be null.", exception.getMessage());
		exception = assertThrows(NullPointerException.class, () -> {
			new Account("Admin", null);
		});
		assertEquals("Arguments for constructor must not be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorValid() {
		Administrator admin = new Administrator("Admin", account);
		assertNotNull(admin);
		assertEquals("Admin", admin.getName());
		assertEquals(account, admin.getAccount());
	}
	
	@Test
	void testGetID() {
		Administrator admin = new Administrator("Admin", account);
		assertTrue(admin.getID().startsWith("admin_"));
	}
	
	@Test
	void testGetAccount() {
		Administrator admin = new Administrator("Admin", account);
		Account adminAccount = admin.getAccount();
		assertNotNull(adminAccount);
		assertEquals("admin1", adminAccount.getEmail());
		assertTrue(adminAccount.verify("admin1", "secret"));
	}
	
	@Test
	void testGetName() {
		Administrator admin = new Administrator("Admin", account);
		assertEquals("Admin", admin.getName());
	}
	
}

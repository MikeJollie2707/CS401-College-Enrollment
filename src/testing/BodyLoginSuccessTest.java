package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import objects.*;
import java.io.Serializable;

class BodyLoginSuccessTest {

	@Test
	void testConstructor() {
		Serializable client = "NewClient";
		BodyLoginSuccess bodyLoginSuccess = new BodyLoginSuccess("Admin", client);
		assertNotNull(bodyLoginSuccess);
		assertEquals("Admin", bodyLoginSuccess.getRole());
		assertEquals(client, bodyLoginSuccess.getClient());
	}
	
	@Test 
	void testGetRole() {
		Serializable client = "NewClient";
		BodyLoginSuccess bodyLoginSuccess = new BodyLoginSuccess("Admin", client);
		assertEquals("Admin", bodyLoginSuccess.getRole());
	}
	
	@Test 
	void testGetClient() {
		Serializable client = "NewClient";
		BodyLoginSuccess bodyLoginSuccess = new BodyLoginSuccess("Admin", client);
		assertEquals(client, bodyLoginSuccess.getClient());
	}

}

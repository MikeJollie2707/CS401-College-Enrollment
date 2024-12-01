package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import objects.ClientMsg;
import objects.Course;

import java.io.Serializable;

class ClientmsgTest {

	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			new ClientMsg(null, null, null);
		});
		assertEquals("'method' and 'resource' must not be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorInvalidMethod() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			new ClientMsg("Invalid", "Hello", null);
		});
		assertEquals("'method' must be one of these values: GET, CREATE, DELETE, EDIT.", exception.getMessage());
	}
	
	@Test
	void testConstructorValid() {
		ClientMsg msg = new ClientMsg("GET", "Resources", "Professor");
		assertNotNull(msg);
		assertEquals("GET", msg.getMethod());
		assertEquals("Resources", msg.getResource());
		assertEquals("Professor", msg.getBody());
	}
	
	@Test
	void testGetBody() {
		ClientMsg msg = new ClientMsg("GET", "Resources", "Professor");
		Serializable body = msg.getBody();
		assertNotNull(body);
		assertEquals("Professor", body);
	}
	
	@Test
	void testIsEndpointTrue() {
		ClientMsg msg = new ClientMsg("GET", "Resources", null);
		assertTrue(msg.isEndpoint("GET", "Resources"));
	}
	
	@Test
	void testIsEndpointFalse() {
		ClientMsg msg = new ClientMsg("GET", "Resources", null);
		assertFalse(msg.isEndpoint("CREATE", "Resources"));
		assertFalse(msg.isEndpoint("Get", "newResources"));
	}
	
	@Test
	void testGetMethod() {
		ClientMsg msg = new ClientMsg("GET", "Resources", null);
		assertEquals("GET", msg.getMethod());
	}
	
	@Test
	void testGetResource() {
		ClientMsg msg = new ClientMsg("GET", "Resources", null);
		assertEquals("Resources", msg.getResource());
	}

}

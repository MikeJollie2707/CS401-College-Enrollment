package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import objects.ServerMsg;

class ServermsgTest {

	@Test
	void testConstructor() {
		ServerMsg msg = ServerMsg.asOK("Test Body");
		assertNotNull(msg);
		assertTrue(msg.isOk());
		assertEquals("Test Body", msg.getBody());
	}
	
	@Test
	void testAsOk() {
		ServerMsg msg = ServerMsg.asOK("Success Message");
		assertNotNull(msg);
		assertTrue(msg.isOk());
		assertEquals("Success Message", msg.getBody());
	}
	
	@Test
	void testAsERR() {
		ServerMsg msg = ServerMsg.asERR("Error Message");
		assertNotNull(msg);
		assertFalse(msg.isOk());
		assertEquals("Error Message", msg.getBody());
	}
	
	@Test
	void testIsOk() {
		ServerMsg okMsg = ServerMsg.asOK("This is OK Msg");
		ServerMsg errMsg = ServerMsg.asERR("This is Error Msg");
		assertTrue(okMsg.isOk());
		assertFalse(errMsg.isOk());
	}
	
	@Test
	void testGetBody() {
		ServerMsg msg = ServerMsg.asOK("Message Body");
		assertNotNull(msg.getBody());
		assertEquals("Message Body", msg.getBody());
	}
}

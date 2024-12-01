package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import objects.*;
import java.time.OffsetTime;

class TupleTest {

	@Test
	void testConstructorInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			new Tuple(OffsetTime.parse("10:00+00:00"), OffsetTime.parse("09:00+00:00"));
		});
		assertEquals("Start Time can't be after End Time.", exception.getMessage());
		
	}
	
	@Test
	void testConstructor() {
		Tuple tuple = new Tuple(OffsetTime.parse("09:00+00:00"), OffsetTime.parse("10:00+00:00"));
		assertNotNull(tuple);
		assertEquals(OffsetTime.parse("09:00+00:00"), tuple.getStart());
		assertEquals(OffsetTime.parse("10:00+00:00"), tuple.getEnd());
	}
	
	@Test
	void testGetStart() {
		Tuple tuple = new Tuple(OffsetTime.parse("09:00+00:00"), OffsetTime.parse("10:00+00:00"));
		assertEquals(OffsetTime.parse("09:00+00:00"), tuple.getStart());
	}
	
	@Test
	void testGetEnd() {
		Tuple tuple = new Tuple(OffsetTime.parse("09:00+00:00"), OffsetTime.parse("10:00+00:00"));
		assertEquals(OffsetTime.parse("10:00+00:00"), tuple.getEnd());
	}
}

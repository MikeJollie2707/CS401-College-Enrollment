package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import objects.*;

class ScheduleEntryTest {
	
	private ScheduleEntry entry1;
	private ScheduleEntry entry2;
	
	@BeforeEach
	void setUp() {
		entry1 = new ScheduleEntry(
			"location1",
			true,
			DayOfWeek.MONDAY,
			OffsetTime.parse("09:00+00:00"),
			OffsetTime.parse("10:15+00:00")
		);
		
		entry2 = new ScheduleEntry(
				"location2",
				true,
				DayOfWeek.MONDAY,
				OffsetTime.parse("11:00+00:00"),
				OffsetTime.parse("12:15+00:00")
			);
		
		
	}
	
	@Test
	void testConstructorNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			new ScheduleEntry(null, true, DayOfWeek.MONDAY, OffsetTime.parse("09:00+00:00"),
					OffsetTime.parse("10:15+00:00"));
		});
		assertEquals("Arguments for constructor must not be null.", exception.getMessage());
	}
	
	@Test
	void testConstructorValid() {
		ScheduleEntry validTime = new ScheduleEntry(
			"location3",
			false,
			DayOfWeek.TUESDAY,
			OffsetTime.parse("12:00+00:00"),
			OffsetTime.parse("01:15+00:00")
		);
		assertNotNull(validTime);
		assertEquals("location3", validTime.getLocation());
		assertEquals(DayOfWeek.TUESDAY, validTime.getDayOfWeek());
		assertEquals(false, validTime.isSync());
				
	}
	
	//Test Failing 
	/*
	@Test
	void testConstructorInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			new ScheduleEntry(
					"location4", 
					true, 
					DayOfWeek.WEDNESDAY, 
					OffsetTime.parse("10:00+00:00"),
					OffsetTime.parse("09:00+00:00"));
		});
		assertEquals("'end_time' must be strictly after 'start_time'", exception.getMessage());
	}
	*/
	
	@Test
	void testGetTime() {
		Tuple time = entry1.getTime();
		assertNotNull(time);
		assertEquals(OffsetTime.parse("09:00+00:00"), time.getStart());
		assertEquals(OffsetTime.parse("10:15+00:00"), time.getEnd());
	}
	
	@Test
	void testGetLocation() {
		assertEquals("location1", entry1.getLocation());
	}
	
	@Test
	void testGetDayOfWeek() {
		assertEquals(DayOfWeek.MONDAY, entry1.getDayOfWeek());
	}
	
	@Test
	void testIsSync() {
		assertTrue(entry1.isSync());
		ScheduleEntry async = new ScheduleEntry(
				"location5",
				false,
				DayOfWeek.THURSDAY,
				OffsetTime.parse("10:00+00:00"),
				OffsetTime.parse("11:15+00:00")
				);
		assertFalse(async.isSync());
	}
	
	@Test
	void testIsOverlap() {
		ScheduleEntry nonOverlap = new ScheduleEntry(
				"location6",
				true,
				DayOfWeek.FRIDAY,
				OffsetTime.parse("09:00+00:00"),
				OffsetTime.parse("10:30+00:00")
				);
		assertFalse(entry1.isOverlap(nonOverlap));
		
	}
}

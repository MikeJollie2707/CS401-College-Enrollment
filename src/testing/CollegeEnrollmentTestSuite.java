package testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.jupiter.api.Test;

@Suite
@SelectClasses({
	AccountTest.class,
	AdministratorTest.class,
	BodyCourseSearchTest.class,
	BodyEnrollOrDropAsTest.class,
	BodyLoginSuccessTest.class,
	BodyLoginTest.class,
	ClientmsgTest.class,
	CourseTest.class,
	InstructorTest.class,
	ScheduleEntryTest.class,
	SectionTest.class,
	ServermsgTest.class,
	StudentTest.class,
	TupleTest.class,
	UniversityTest.class
})

public class CollegeEnrollmentTestSuite {
}

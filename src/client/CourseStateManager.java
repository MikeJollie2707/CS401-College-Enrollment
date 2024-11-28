package client;

import java.util.HashMap;
import java.util.Map;

import objects.BodyCourseSearch;
import objects.Course;
import objects.Student;

public class CourseStateManager {
    private static CourseStateManager instance;
    private Map<String, CourseState> courseStates;
    
    private CourseStateManager() {
        courseStates = new HashMap<>();
    }
    
    public static synchronized CourseStateManager getInstance() {
        if (instance == null) {
            instance = new CourseStateManager();
        }
        return instance;
    }
    
    public CourseState getOrCreateState(Course course, Student student) {
        String courseKey = course.getPrefix() + course.getNumber();
        if (!courseStates.containsKey(courseKey)) {
            courseStates.put(courseKey, new CourseState(course, student));
        }
        return courseStates.get(courseKey);
    }
    
    public Map<String, CourseState> getCourseStates() {
        return new HashMap<>(courseStates);
    }
    
    public Map<String, CourseState> filterStatesByCriteria(
        BodyCourseSearch searchBody, 
        Map<String, CourseState> existingStates
    ) {
        Map<String, CourseState> filteredStates = new HashMap<>();
        
        for (CourseState state : existingStates.values()) {
            Course course = state.getCourse();
            boolean matchesPrefix = isMatchOrEmpty(searchBody.getCoursePrefix(), course.getPrefix());
            boolean matchesNumber = isMatchOrEmpty(searchBody.getCourseNumber(), course.getNumber());
            boolean matchesName = isMatchOrEmpty(searchBody.getCourseName(), course.getDescription());
            boolean matchesInstructor = isInstructorMatch(searchBody, course);
            
            if (matchesPrefix && matchesNumber && matchesName && matchesInstructor) {
                filteredStates.put(course.getPrefix() + course.getNumber(), state);
            }
        }
        
        return filteredStates;
    }
    
    private boolean isMatchOrEmpty(String searchTerm, String value) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return true;
        }
        
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }
    
    private boolean isInstructorMatch(BodyCourseSearch searchBody, Course course) {
        String instructorSearch = searchBody.getInstructorName();
        if (instructorSearch == null || instructorSearch.trim().isEmpty()) {
            return true;
        }
        
        return course.getSections().stream().anyMatch(section -> section.getInstructor().getName().toLowerCase().contains(instructorSearch.toLowerCase()));
    }
    
    public void clearStates() {
        courseStates.clear();
    }
}
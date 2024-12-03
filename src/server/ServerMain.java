package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

import objects.*;

public class ServerMain {
    static final int port = 7777;
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
            InetAddress localhost = InetAddress.getLocalHost();
            System.out.println(String.format("Listening on %s:%d", localhost.getHostAddress(), port));

            University[] universities = loadInfoFromFile("unis.txt");

            while (true) {
                Socket socket = ss.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                ClientHandler handler = new ClientHandler(socket, universities);
                new Thread(handler).start();
            }

        } catch (IOException err) {
            System.out.println("No connection between server and client");
            err.printStackTrace();
        }
    }

    private static University[] loadInfoFromFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        try (Scanner scanner = new Scanner(file)) {
            int num = Integer.parseInt(scanner.nextLine());
            HashMap<String, University> uniMap = new HashMap<>();

            for (int i = 0; i < num; ++i) {
                String row = scanner.nextLine();
                String[] entry = row.split(",");

                // University Information
                String universityName = entry[0];
                String location = entry[1];
                University university = new University(universityName, location);
                // Loading more information relating to Admins
                if (entry.length > 2) {
                    String adminsFile = entry[2];
                    loadAdminsFromFile(adminsFile, university);
                }
                // Loading more information relating to Students
                if (entry.length > 3) {
                    String studentsFile = entry[3];
                    loadStudentsFromFile(studentsFile, university);
                }
                if (entry.length > 4) {
                    String coursesFile = entry[4];
                    String sectionsFile = entry[5];
                    loadCoursesFromFile(coursesFile, university);
                    loadSectionsFromFile(sectionsFile, university);
                }

                uniMap.put(universityName, university);
            }

            return uniMap.values().stream()
                    .collect(Collectors.toList())
                    .toArray(new University[0]);

        } catch (IOException err) {
            err.printStackTrace();
        }
        return new University[0];
    }

    // Helper function to load Admins from file and add to University
    private static void loadAdminsFromFile(String adminsFile, University university) {
        File file = new File(adminsFile);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String adminName = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    Administrator admin = new Administrator(adminName, new Account(username, password));
                    university.addAdmin(admin);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Admins file not found: " + adminsFile);
        }
    }

    // Helper function to load Students from file and add to University
    private static void loadStudentsFromFile(String studentsFile, University university) {
        File file = new File(studentsFile);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String studentName = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    Student student = new Student(studentName, new Account(username, password));
                    university.addStudent(student);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Students file not found: " + studentsFile);
        }
    }

    private static void loadCoursesFromFile(String coursesFile, University university) {
        File file = new File(coursesFile);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String coursePrefix = parts[0];
                String courseNumber = parts[1];
                String courseName = parts[2];
                String courseDesc = parts[3];
                Course course = new Course(coursePrefix, courseNumber, courseName, courseDesc);
                if (!parts[4].equals("none")) {
                    String[] prerequisites = parts[4].split(" ");
                    for (int i = 0; i < prerequisites.length; i += 2) {
                        String prereqPrefix = prerequisites[i];
                        String prereqNumber = (i + 1 < prerequisites.length) ? prerequisites[i + 1] : "";
                        var found = university.getCoursesByFilter((Course c) -> {
                            return c.getPrefix().equals(prereqPrefix) && c.getNumber().equals(prereqNumber);
                        });
                        if (found.size() > 0) {
                            course.insertPrereq(found.get(0));
                        }
                    }
                }
                university.addCourse(course);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Students file not found: " + coursesFile);
        }
    }

    private static void loadSectionsFromFile(String sectionsFile, University university) {
        File file = new File(sectionsFile);
        try (Scanner scanner = new Scanner(file)) {
            HashMap<String, ArrayList<Section>> mapper = new HashMap<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                try {
                    // courseID,sectionNum,capacity,waitlist,instructor,dayofweek,starttime,endtime,location,Sync/Async,...
                    String courseID = parts[0];
                    Course course = university.getCourseByID(courseID);
                    if (course == null) {
                        System.out.println("Course not found. Skipping...");
                        continue;
                    }
                    if (!mapper.containsKey(course.getID())) {
                        mapper.put(course.getID(), new ArrayList<>());
                    }
                    String sectionNum = parts[1];
                    String capacity = parts[2];
                    String waitlist = parts[3];
                    String instructorName = parts[4];

                    ArrayList<ScheduleEntry> entries = new ArrayList<>();

                    for (int i = 5; i < parts.length; i += 5) {
                        String dayofweek = parts[i];
                        String starttime = parts[i + 1];
                        String endtime = parts[i + 2];
                        String location = parts[i + 3];
                        String isSync = parts[i + 4];

                        DayOfWeek day = DayOfWeek.of(Integer.valueOf(dayofweek));
                        String startHour = starttime.split(":")[0];
                        String startMin = starttime.split(":")[1];
                        String endHour = endtime.split(":")[0];
                        String endMin = endtime.split(":")[1];
                        var now = OffsetTime.now(ZoneId.of("America/Los_Angeles"));
                        OffsetTime start = OffsetTime.of(Integer.valueOf(startHour), Integer.valueOf(startMin), 0, 0,
                                now.getOffset());
                        OffsetTime end = OffsetTime.of(Integer.valueOf(endHour), Integer.valueOf(endMin), 0, 0,
                                now.getOffset());
                        ScheduleEntry entry = new ScheduleEntry(location, isSync.equals("s"), day, start, end);
                        entries.add(entry);
                    }

                    var sections = mapper.get(course.getID());
                    var found = university.getInstructors().values().stream()
                            .filter(i -> i.getName().equals(instructorName)).collect(Collectors.toList())
                            .toArray(new Instructor[0]);
                    
                    Instructor instructor = null;
                    if (found.length > 0) {
                        instructor = found[0];
                    }
                    else {
                        instructor = new Instructor(instructorName, null);
                        university.addInstructor(instructor);
                    }
                    Section section = new Section(course, sectionNum, Integer.valueOf(capacity),
                            Integer.valueOf(waitlist), instructor);
                    section.setSchedule(entries.toArray(new ScheduleEntry[0]));
                    sections.add(section);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Invalid entry. Skipping...");
                }
            }
            for (var courseID : mapper.keySet()) {
                Course course = university.getCourseByID(courseID);
                for (var section : mapper.get(courseID)) {
                    course.insertSection(section);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Sections file not found.");
        }
    }
}
package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

import objects.Account;
import objects.Administrator;
import objects.Student;
import objects.University;

public class ServerMain {
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(7777)) {
            ss.setReuseAddress(true);
            InetAddress localhost = InetAddress.getLocalHost();
            System.out.println(String.format("Listening on %s:%d", localhost.getHostAddress(), 7777));

            University[] universities = loadInfoFromFile("unis.txt");

            while (true) {
                Socket socket = ss.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                ClientHandler handler = new ClientHandler(socket, universities);
                new Thread(handler).start();
            }

        } catch (IOException err) {
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
}

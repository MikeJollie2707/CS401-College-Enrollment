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

            // Fake setup
            // TODO: Remove later.
            for (int i = 0; i < universities.length; ++i) {
                universities[i].addAdmin(new Administrator("Admin", new Account("admin", "123456")));
                universities[i].addStudent(new Student("Steve", new Account("steve", "iamsteve")));
            }

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
            // nextInt() doesn't move the scanner, yay...
            int num = Integer.valueOf(scanner.nextLine());
            HashMap<String, University> uniMap = new HashMap<>();
            for (int i = 0; i < num; ++i) {
                String row = scanner.nextLine();
                String[] entry = row.split(",");
                uniMap.put(entry[0], new University(entry[0], entry[1]));

                // TODO: Additional info for universities can be provided in further columns.
                // For now it only loads the bare minimum for the constructor.
                // If for example, we also want to load admin+student info from file as well,
                // one of the column can specify the filename in which it can get that kind of
                // info.
                // So something like "CSUEB,Carlos Bee,csueb_admins.txt,csueb_students.txt"
            }

            University[] unis = uniMap.values().stream()
                    .collect(Collectors.toList())
                    .toArray(new University[0]); // Weird syntax but it works, check the docs

            return unis;
        } catch (IOException err) {
            err.printStackTrace();
        }
        return new University[0];
    }
}

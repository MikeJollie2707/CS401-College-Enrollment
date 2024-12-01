package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import objects.Account;
import objects.BodyLogin;
import objects.ClientMsg;
import objects.ServerMsg;
import objects.University;

public class ClientHandler implements Runnable {
    final private Socket socket;
    final private University[] universities;

    public ClientHandler(Socket socket, University[] universities) {
        this.socket = socket;
        this.universities = universities;
    }

    @Override
    public void run() {
        try (InputStream raw_istream = socket.getInputStream();
                OutputStream raw_ostream = socket.getOutputStream()) {
            ObjectOutputStream ostream = new ObjectOutputStream(raw_ostream);
            try {
                boolean isSessionExist = false;
                ObjectInputStream istream = new ObjectInputStream(raw_istream);
                
                String[] uniNames = new String[universities.length];
                for (int i = 0; i < uniNames.length; ++i) {
                    uniNames[i] = universities[i].getName();
                }
                // sends list of uniNames to the GUI to create a JComboBox instead of typing uni name manually
                ostream.writeObject(uniNames);
                // Loop until the socket itself closes (gracefully or abruptly).
                while (true) {
                    isSessionExist = false;
                    var req = (ClientMsg) istream.readObject();
                    if (!req.isEndpoint("CREATE", "login")) {
                        ostream.writeObject(ServerMsg.asERR(String.format("'CREATE login' expected, received '%s %s'",
                                req.getMethod(), req.getResource())));
                        continue;
                    }

                    var body = (BodyLogin) req.getBody();
                    for (var uni : universities) {
                        if (uni.getName().equals(body.getUniName())) {
                            for (var student : uni.getStudents().values()) {
                                Account acc = student.getAccount();
                                if (acc.verify(body.getLoginID(), body.getPassword())) {
                                    isSessionExist = true;
                                    new StudentSessionHandler(socket, istream, ostream, uni, student).run();
                                    break;
                                }
                            }
                            if (!isSessionExist) {
                                for (var instructor : uni.getInstructors().values()) {
                                    Account acc = instructor.getAccount();
                                    // Instructor may not have account.
                                    if (acc != null && acc.verify(body.getLoginID(), body.getPassword())) {
                                        isSessionExist = true;
                                        new InstructorSessionHandler(socket, istream, ostream, uni, instructor).run();
                                        break;
                                    }
                                }
                            }
                            if (!isSessionExist) {
                                for (var admin : uni.getAdmins()) {
                                    Account acc = admin.getAccount();
                                    if (acc.verify(body.getLoginID(), body.getPassword())) {
                                        isSessionExist = true;
                                        new AdminSessionHandler(socket, istream, ostream, uni, admin).run();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (!isSessionExist) {
                        ostream.writeObject(ServerMsg.asERR(String.format("Incorrect credential for entity (%s @ %s).",
                                body.getLoginID(), body.getUniName())));
                    }
                }
            } catch (ClassNotFoundException err) {
                System.err.println("Casting failed.");
                err.printStackTrace();
            } catch (Exception err) {
                err.printStackTrace();
            }
        } 
        catch (EOFException err) {
            System.out.println("EOFException: Most likely the client closed their socket.");
        }
        catch (IOException err) {
            err.printStackTrace();
        } finally {
            System.out.println("Socket closed.");
        }
    }
}

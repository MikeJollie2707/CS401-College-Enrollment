package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import objects.ClientMsg;
import objects.ServerMsg;
import objects.Student;
import objects.University;

public class StudentSessionHandler extends SessionHandler {
    private Student student;

    public StudentSessionHandler(Socket socket, ObjectInputStream istream, ObjectOutputStream ostream,
            University university, Student student) {
        super(socket, istream, ostream, university);
        this.student = student;
    }

    @Override
    public void run() {
        try {
            ostream.writeObject(ServerMsg.asOK(student.getID()));
            while (true) {
                ClientMsg req = null;
                ServerMsg resp = null;

                try {
                    req = (ClientMsg) istream.readObject();
                } catch (ClassNotFoundException err) {
                    ostream.writeObject(ServerMsg.asERR("Conversion failed, 'ClientMsg' expected."));
                    continue;
                }

                if (req.isEndpoint("CREATE", "logout")) {
                    resp = logout(req);
                    ostream.writeObject(resp);
                    // Just in case it fails to logout somehow.
                    if (resp.isOk()) {
                        break;
                    }
                }

                if (req.isEndpoint("CREATE", "message")) {
                    resp = capitalizeMessage(req);
                } else if (req.isEndpoint("GET", "courses")) {
                    resp = fetchCourses(req);
                } else if (req.isEndpoint("GET", "schedule")) {
                    resp = fetchSchedule(req);
                } else if (req.isEndpoint("CREATE", "enroll")) {
                    resp = enroll(req);
                } else if (req.isEndpoint("CREATE", "drop")) {
                    resp = drop(req);
                } else if (req.isEndpoint("GET", "past-enroll")) {
                    resp = fetchPastEnrollment(req);
                }

                if (resp != null) {
                    ostream.writeObject(resp);
                } else {
                    ostream.writeObject(ServerMsg.asERR(
                            String.format("Endpoint '%s %s' is not available.", req.getMethod(), req.getResource())));
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    // NOTE: Mock service, will delete later.
    private ServerMsg capitalizeMessage(ClientMsg req) {
        String message = (String) req.getBody();
        return ServerMsg.asOK(message.toUpperCase());
    }

    private ServerMsg logout(ClientMsg req) {
        return ServerMsg.asOK("logout");
    }

    private ServerMsg fetchCourses(ClientMsg req) {
        return null;
    }

    private ServerMsg fetchSchedule(ClientMsg req) {
        return null;
    }

    private ServerMsg enroll(ClientMsg req) {
        return null;
    }

    private ServerMsg drop(ClientMsg req) {
        return null;
    }

    private ServerMsg fetchPastEnrollment(ClientMsg req) {
        return null;
    }
}

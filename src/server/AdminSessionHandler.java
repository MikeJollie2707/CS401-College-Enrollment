package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import objects.Administrator;
import objects.ClientMsg;
import objects.ServerMsg;
import objects.University;

public class AdminSessionHandler extends SessionHandler {
    private Administrator admin;

    public AdminSessionHandler(Socket socket, ObjectInputStream istream, ObjectOutputStream ostream,
            University university, Administrator admin) {
        super(socket, istream, ostream, university);
        this.admin = admin;
    }

    @Override
    public void run() {
        try {
            // NOTE: Temporary, may return some more info like admin name and stuff.
            // Probably will just send the entire Admin object tbh but will see how it goes.
            ostream.writeObject(ServerMsg.asOK(admin.getID()));
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
                } else if (req.isEndpoint("GET", "report")) {
                    resp = fetchReport(req);
                } else if (req.isEndpoint("CREATE", "student")) {
                    resp = createStudent(req);
                } else if (req.isEndpoint("CREATE", "section")) {
                    resp = addSection(req);
                } else if (req.isEndpoint("EDIT", "section")) {
                    resp = editSection(req);
                } else if (req.isEndpoint("DELETE", "section")) {
                    resp = delSection(req);
                } else if (req.isEndpoint("CREATE", "course")) {
                    resp = addCourse(req);
                } else if (req.isEndpoint("EDIT", "course")) {
                    resp = editCourse(req);
                } else if (req.isEndpoint("DELETE", "course")) {
                    resp = delCourse(req);
                } else if (req.isEndpoint("CREATE", "enroll-student")) {
                    resp = enrollStudent(req);
                } else if (req.isEndpoint("CREATE", "drop-student")) {
                    resp = dropStudent(req);
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

    private ServerMsg fetchReport(ClientMsg req) {
        return null;
    }

    private ServerMsg createStudent(ClientMsg req) {
        return null;
    }

    private ServerMsg addSection(ClientMsg req) {
        return null;
    }

    private ServerMsg editSection(ClientMsg req) {
        return null;
    }

    private ServerMsg delSection(ClientMsg req) {
        return null;
    }

    private ServerMsg addCourse(ClientMsg req) {
        return null;
    }

    private ServerMsg editCourse(ClientMsg req) {
        return null;
    }

    private ServerMsg delCourse(ClientMsg req) {
        return null;
    }

    private ServerMsg enrollStudent(ClientMsg req) {
        return null;
    }

    private ServerMsg dropStudent(ClientMsg req) {
        return null;
    }
}

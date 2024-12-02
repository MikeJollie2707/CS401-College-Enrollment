package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import objects.*;

public class InstructorSessionHandler extends SessionHandler {
    private Instructor instructor;

    public InstructorSessionHandler(Socket socket, ObjectInputStream istream, ObjectOutputStream ostream,
            University university, Instructor instructor) {
        super(socket, istream, ostream, university);
        this.instructor = instructor;
    }

    @Override
    public void run() {
        try {
            ostream.writeObject(ServerMsg.asOK(instructor.getID()));
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

                try {
                    if (req.isEndpoint("GET", "sections")) {
                        resp = viewSections(req);
                    }
                }
                catch (Exception err) {
                    err.printStackTrace();
                    resp = ServerMsg.asERR(String.format("Internal error: %s", err.getMessage()));
                }

                if (resp != null) {
                    ostream.writeUnshared(resp);
                    ostream.reset();
                } else {
                    ostream.writeObject(ServerMsg.asERR(
                            String.format("Endpoint '%s %s' is not available.", req.getMethod(), req.getResource())));
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    private ServerMsg logout(ClientMsg req) {
        return ServerMsg.asOK("logout");
    }

    private synchronized ServerMsg viewSections(ClientMsg req) {
        var teaching = instructor.getTeaching().toArray(new Section[0]);
        return ServerMsg.asOK(teaching);
    }
}

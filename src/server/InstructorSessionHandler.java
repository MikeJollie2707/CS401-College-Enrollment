package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import objects.ClientMsg;
import objects.Instructor;
import objects.ServerMsg;
import objects.University;

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

                if (req.isEndpoint("GET", "sections")) {
                    resp = viewSections(req);
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

    private ServerMsg logout(ClientMsg req) {
        return ServerMsg.asOK("logout");
    }

    private ServerMsg viewSections(ClientMsg req) {
        return null;
    }
}

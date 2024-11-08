package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import objects.University;

public abstract class SessionHandler {
    protected final Socket socket;
    protected final ObjectInputStream istream;
    protected final ObjectOutputStream ostream;
    protected final University university;

    protected SessionHandler(Socket socket, ObjectInputStream istream, ObjectOutputStream ostream,
            University university) {
        this.socket = socket;
        this.istream = istream;
        this.ostream = ostream;
        this.university = university;
    }

    public abstract void run();
}

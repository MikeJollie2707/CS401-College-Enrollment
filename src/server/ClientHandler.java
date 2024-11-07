package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import objects.Account;
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
                // NOTE: Whether or not the client initiate the request
                // or the server automatically send this info, need more
                // discussion with whoever is maintaining the client code.
                String[] uniNames = new String[universities.length];
                for (int i = 0; i < uniNames.length; ++i) {
                    uniNames[i] = universities[i].getName();
                }
                ostream.writeObject(ServerMsg.asOK(uniNames));

                ObjectInputStream istream = new ObjectInputStream(raw_istream);
                ClientMsg req = (ClientMsg) istream.readObject();
                if (req.isEndpoint("GET", "foo")) {
                    ostream.writeObject(ServerMsg.asOK("bar"));
                } else {
                    ostream.writeObject(ServerMsg.asERR("baz"));
                }
            } catch (ClassNotFoundException err) {
                System.err.println("Casting failed.");
                err.printStackTrace();
            } catch (Exception err) {
                err.printStackTrace();
            }
        } catch (IOException err) {
            err.printStackTrace();
        } finally {
            System.out.println("Socket closed.");
        }
    }
}

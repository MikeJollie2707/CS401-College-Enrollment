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

public class ClientHandler implements Runnable {
    final private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream raw_istream = socket.getInputStream();
                OutputStream raw_ostream = socket.getOutputStream()) {
            ObjectOutputStream ostream = new ObjectOutputStream(raw_ostream);
            try {
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

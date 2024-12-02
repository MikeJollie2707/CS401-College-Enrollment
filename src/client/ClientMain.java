package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.SwingUtilities;

public class ClientMain {
    public static void main(String[] args) {
        try {
            // Socket socket = new Socket("4.tcp.us-cal-1.ngrok.io", 15808);
            Socket socket = new Socket("localhost", 7777);
            ObjectOutputStream ostream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream istream = new ObjectInputStream(socket.getInputStream());

            // Put the JFrame on EDT.
            SwingUtilities.invokeLater(() -> {
                new MainFrame(socket, ostream, istream);
            });
        } catch (IOException err) {
            System.err.println("Failed to create socket.");
            err.printStackTrace();
        }
    }
}

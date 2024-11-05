package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(7777)) {
            ss.setReuseAddress(true);
            InetAddress localhost = InetAddress.getLocalHost();
            System.out.println(String.format("Listening on %s:%d", localhost.getHostAddress(), 7777));

            // TODO: Setup universities

            while (true) {
                Socket socket = ss.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }

        } catch (IOException err) {
            err.printStackTrace();
        }
    }
}

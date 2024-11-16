package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;

import objects.ClientMsg;
import objects.ServerMsg;

import java.awt.Color;
import java.awt.event.*;

public class MainFrame {
    final private Socket socket;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;
    final private JFrame window;

    public MainFrame(Socket socket, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.socket = socket;
        this.ostream = ostream;
        this.istream = istream;

        window = new JFrame("CES");
        window.setSize(600, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    socket.close();
                } catch (IOException err) {
                    System.err.println("Failed to close socket.");
                    err.printStackTrace();
                }
            };
        });

        foo();

        window.setVisible(true);
    }

    // Use SwingWorker to do connection stuff
    // A common occurrence is hitting a button -> make a request
    // which is implemented as btn.addActionListener()
    // Inside the action listener, create a SwingWorker and execute it.

    // SwingWorker has 2 methods of interest to override: doInBackground() and
    // done()
    // doInBackground() is exclusively used for networking, done() is exclusively
    // used for lightweight stuff like updating UI (mixing them up will freeze the
    // GUI).

    // Example (remove in Implementation phase):
    private void foo() {
        JPanel panel = new JPanel();
        JButton success_btn = new JButton("Success Click");
        JButton fail_btn = new JButton("Fail Click");

        success_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                success_btn.setEnabled(false);
                fail_btn.setEnabled(false);

                SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
                    @Override
                    protected ServerMsg doInBackground() throws Exception {
                        ostream.writeObject(new ClientMsg("GET", "foo", null));
                        return (ServerMsg) istream.readObject();
                    };

                    @Override
                    protected void done() {
                        try {
                            ServerMsg resp = get();
                            if (resp.isOk()) {
                                success_btn.setBackground(Color.GREEN);
                            } else {
                                success_btn.setBackground(Color.YELLOW);
                            }

                            success_btn.setText((String) resp.getBody());
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    };
                };
                worker.execute();
            };
        });

        // Same as above but send an "incorrect" request.
        fail_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                success_btn.setEnabled(false);
                fail_btn.setEnabled(false);

                SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
                    @Override
                    protected ServerMsg doInBackground() throws Exception {
                        ostream.writeObject(new ClientMsg("GET", "bar", null));
                        // Artificial delay
                        // Even if there's something blocking on this function,
                        // the button still "lives" (if the button is enabled, it still react to being
                        // hovered on).
                        Thread.sleep(2000);
                        return (ServerMsg) istream.readObject();
                    };

                    @Override
                    protected void done() {
                        try {
                            ServerMsg resp = get();
                            if (resp.isOk()) {
                                fail_btn.setBackground(Color.GREEN);
                            } else {
                                fail_btn.setBackground(Color.YELLOW);
                            }

                            fail_btn.setText((String) resp.getBody());
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    };
                };
                worker.execute();
            };
        });

        panel.add(success_btn);
        panel.add(fail_btn);
        window.add(panel);
    }
}

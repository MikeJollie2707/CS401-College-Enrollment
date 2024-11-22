package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;

import objects.ClientMsg;
import objects.ServerMsg;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.*;

public class MainFrame {
    final private Socket socket;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;
    final private JFrame window;

    private CardLayout cl;
    private JPanel viewer;

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

        cl = new CardLayout();
        viewer = new JPanel(cl);

        String[] uniNames = null;
        try {
            var names = (String[]) istream.readObject();
            uniNames = names;
        }
        catch (ClassNotFoundException err) {
            err.printStackTrace();
        }
        catch (IOException err) {
            err.printStackTrace();
        }

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<ServerMsg, Void> logoutWorker = new SwingWorker<ServerMsg,Void>() {
                    @Override
                    protected ServerMsg doInBackground() throws Exception {
                        ostream.writeObject(new ClientMsg("CREATE", "logout", null));
                        return (ServerMsg) istream.readObject();
                    }
                    @Override
                    protected void done() {
                        render("login");
                    }
                };
                logoutWorker.execute();
            }
        });
        
        viewer.add(new Login(ostream, istream, this, uniNames), "login");
        viewer.add(new StudentGUI(this, ostream, istream, logoutBtn), "student");

        window.add(viewer);
        
        window.setVisible(true);
    }

    public void render(String mode) {
        // window.getContentPane().removeAll();
        // window.repaint();
        // window.add(panels[scene]);
        // window.validate();
        cl.show(viewer, mode);
    }

    public JFrame getFrame() {
        return window;
    }
}

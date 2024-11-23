package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import javax.swing.*;

import objects.ClientMsg;
import objects.ServerMsg;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;

public class MainFrame {
    // final private Socket socket;
    // final private ObjectOutputStream ostream;
    // final private ObjectInputStream istream;
    final private JFrame window;

    private CardLayout cl;
    private JPanel viewer;
    private JDialog loadingDialog;
    private Serializable whoami;

    public MainFrame(Socket socket, ObjectOutputStream ostream, ObjectInputStream istream) {
        // this.socket = socket;
        // this.ostream = ostream;
        // this.istream = istream;

        window = new JFrame("CES");
        window.setSize(1000, 750);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

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

        // TODO: Decorate this thing so it doesn't just display a white window.
        loadingDialog = new JDialog(window, "Loading...", ModalityType.DOCUMENT_MODAL);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loadingDialog.setSize(300, 300);
        loadingDialog.setLocationRelativeTo(null);

        cl = new CardLayout();
        viewer = new JPanel(cl);

        String[] uniNames = null;
        try {
            var names = (String[]) istream.readObject();
            uniNames = names;
        } catch (ClassNotFoundException err) {
            err.printStackTrace();
        } catch (IOException err) {
            err.printStackTrace();
        }

        // Can't use one button because a panel will "consume" that button.
        JButton[] logoutBtns = new JButton[3];
        for (int i = 0; i < 3; ++i) {
            logoutBtns[i] = new JButton("Logout");
            logoutBtns[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingWorker<ServerMsg, Void> logoutWorker = new SwingWorker<ServerMsg, Void>() {
                        @Override
                        protected ServerMsg doInBackground() throws Exception {
                            ostream.writeObject(new ClientMsg("CREATE", "logout", null));
                            return (ServerMsg) istream.readObject();
                        }
    
                        @Override
                        protected void done() {
                            render("login");
                            stopLoading();
                        }
                    };
                    logoutWorker.execute();
                    showLoading();
                }
            });
        }

        viewer.add(new GUILogin(this, ostream, istream, uniNames), "login");
        viewer.add(new GUIAdmin(this, ostream, istream, logoutBtns[0]), "admin");
        viewer.add(new GUIStudent(this, ostream, istream, logoutBtns[1]), "student");

        window.add(viewer);
        window.setVisible(true);
    }

    public void render(String mode) {
        cl.show(viewer, mode);
    }

    public void showLoading() {
        loadingDialog.setVisible(true);
    }

    public void stopLoading() {
        // Without this, sometimes the dialog closes too fast and
        // doesn't close, so...
        try {
            Thread.sleep(100);
        } catch (InterruptedException err) {

        }
        loadingDialog.setVisible(false);
    }

    public Serializable getMe() {
        return whoami;
    }

    public void setMe(Serializable me) {
        whoami = me;
    }
}

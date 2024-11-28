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
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;

public class MainFrame {
    final private JFrame window;

    private CardLayout cl;
    private JPanel viewer;
    private JDialog loadingDialog;
    private Serializable whoami;

    public MainFrame(Socket socket, ObjectOutputStream ostream, ObjectInputStream istream) {
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
        loadingDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
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
            logoutBtns[i].setBackground(Color.RED);
            logoutBtns[i].setForeground(Color.BLACK);
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
                            setMe(null);
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

    /**
     * Tells the frame to render a different panel.
     * 
     * @param sceneName Valid values: {@code login}, {@code student}, {@code admin}.
     */
    public void render(String sceneName) {
        cl.show(viewer, sceneName);
    }

    /**
     * Show a loading dialog over the current frame.
     * <p>
     * The main purpose is to prevent the user from interacting with the rest of the
     * GUI while some expensive operations are running.
     */
    public void showLoading() {
        loadingDialog.setVisible(true);
    }

    /**
     * Remove the loading dialog over the current frame.
     */
    public void stopLoading() {
        /**
         * When this method is used in other panels, oftentimes it'll not close
         * the dialog, but the rest of the GUI still works and it doesn't block
         * anything.
         * 
         * I think this is bcuz it opens and closes too fast, so it just freaks out
         * and not closes the thing, but internally it is considered closed.
         * So the hack here is to add this tiny delay here. And voila, it closes every
         * time. You won't even notice it's there!
         */
        try {
            Thread.sleep(10);
        } catch (InterruptedException err) {

        }
        loadingDialog.setVisible(false);
    }

    /**
     * Return the client. When used inside one of the {@code GUI...}, it is safe to
     * cast
     * to one of {@code Administrator}, {@code Student}, or {@code Instructor}.
     * 
     * @return The object that identifies the user that's logged in.
     */
    public Serializable getMe() {
        return whoami;
    }

    /**
     * Set the client.
     * 
     * @param me The object that identifies the user that's logged in. When the user
     *           is logged out, this should be null.
     */
    public void setMe(Serializable me) {
        whoami = me;
    }
}

package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;
import java.awt.event.*;

import objects.BodyLogin;
import objects.ClientMsg;
import objects.ServerMsg;

public class Login extends JPanel {
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;
    final private MainFrame frame;
    private String[] uniNames;

    public Login(ObjectOutputStream ostream, ObjectInputStream istream, MainFrame frame, String[] uniNames) {
        this.ostream = ostream;
        this.istream = istream;
        this.frame = frame;
        this.uniNames = uniNames;

        JPanel self = this;

        setupForm();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                self.removeAll();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                setupForm();
                self.revalidate();
                self.repaint();
            }
        });
    }

    void setupForm() {
        Form form = new Form(this);
        JComboBox<String> uniDropdown = new JComboBox<>(uniNames);
        JTextField loginID = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        password.setEchoChar('*');

        form.addEntry(new JLabel("Choose a university:"), uniDropdown, () -> (String) uniDropdown.getSelectedItem())
                .addEntry(new JLabel("Login ID:"), loginID, () -> loginID.getText())
                .addEntry(new JLabel("Password:"), password, () -> new String(password.getPassword()));

        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                var results = form.getResults();
                String selectedUni = results.get(0);
                String id = results.get(1);
                String pw = results.get(2);
                SwingWorker<ServerMsg, Void> loginWorker = new SwingWorker<ServerMsg, Void>() {
                    protected ServerMsg doInBackground() throws Exception {
                        ostream.writeObject(new ClientMsg("CREATE", "login", new BodyLogin(selectedUni, id, pw)));
                        return (ServerMsg) istream.readObject();
                    };

                    protected void done() {
                        try {
                            var resp = get();
                            if (resp.isOk()) {
                                System.out.println("LOGIN SUCCESS");
                                // frame.render(1);
                                frame.render("student");
                            } else {
                                JOptionPane.showMessageDialog(null, "Login failed.");
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    };
                };
                loginWorker.execute();
            };
        });
        this.add(submitBtn);
    }
}

package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.*;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;

import objects.BodyLogin;
import objects.BodyLoginSuccess;
import objects.ClientMsg;
import objects.ServerMsg;

public class GUILogin extends JPanel {
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;
    final private MainFrame frame;
    private String[] uniNames;

    public GUILogin(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream, String[] uniNames) {
        this.ostream = ostream;
        this.istream = istream;
        this.frame = frame;
        this.uniNames = uniNames;

        setupForm();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                removeAll();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                setupForm();
                revalidate();
                repaint();
            }
        });
    }

    void setupForm() {
        setLayout(new FlowLayout());
        BuilderForm form = new BuilderForm(null);
        JPanel formPanel = form.getPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel loginMessage = new JLabel("Please login first!");
        JComboBox<String> uniDropdown = new JComboBox<>(uniNames);
        JTextField loginID = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        password.setEchoChar('*');
        JButton submitBtn = new JButton("Submit");

        loginMessage.setFont(new Font("Arial", Font.BOLD, 30));
        loginMessage.setForeground(Color.BLUE);
        formPanel.add(loginMessage);
        formPanel.add(Box.createVerticalStrut(100));

        form.addEntry(new JLabel("Choose a university:"), uniDropdown, () -> (String) uniDropdown.getSelectedItem());
        formPanel.add(Box.createVerticalStrut(10));

        form.addEntry(new JLabel("Login ID:"), loginID, () -> loginID.getText());
        formPanel.add(Box.createVerticalStrut(10));

        form.addEntry(new JLabel("Password:"), password, () -> new String(password.getPassword()));
        formPanel.add(Box.createVerticalStrut(10));

        submitBtn.setBackground(Color.GREEN);
        submitBtn.setForeground(Color.BLACK);
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
                                var body = (BodyLoginSuccess) resp.getBody();
                                String role = body.getRole();
                                Serializable client = body.getClient();
                                frame.setMe(client);
                                if (role.equals("student")) {
                                    frame.render("student");
                                } else if (role.equals("admin")) {
                                    frame.render("admin");
                                } else if (role.equals("instructor")) {
                                    frame.render("instructor");
                                }
                                frame.stopLoading();
                            } else {
                                frame.stopLoading();
                                JOptionPane.showMessageDialog(null, "Login failed.");
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    };
                };
                loginWorker.execute();
                frame.showLoading();
            };
        });
        formPanel.add(submitBtn);

        add(formPanel);
    }
}

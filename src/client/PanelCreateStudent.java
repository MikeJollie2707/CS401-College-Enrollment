package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.*;

import objects.*;

public class PanelCreateStudent extends PanelBase {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;

    private BuilderForm createForm;

    public PanelCreateStudent(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;

        createForm = new BuilderForm(null);
    }

    void initForm() {
        JTextField nameField = new JTextField(10);
        JTextField loginField = new JTextField(10);
        JTextField pwField = new JTextField(10);

        createForm.addEntry(new JLabel("Name:"), nameField, () -> nameField.getText());
        createForm.addEntry(new JLabel("Login ID:"), loginField, () -> loginField.getText());
        createForm.addEntry(new JLabel("Password:"), pwField, () -> pwField.getText());

        JButton submitBtn = new JButton("Create");
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var results = createForm.getResults();

                String name = results.get(0);
                String loginID = results.get(1);
                String password = results.get(2);

                if (name.isBlank() || loginID.isBlank() || password.isBlank()) {
                    return;
                }

                SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
                    @Override
                    protected ServerMsg doInBackground() throws Exception {
                        ostream.writeObject(
                                new ClientMsg("CREATE", "student",
                                        new Student(name, new Account(loginID, password))));
                        return (ServerMsg) istream.readObject();
                    };

                    @Override
                    protected void done() {
                        try {
                            var resp = get(3, TimeUnit.SECONDS);
                            frame.stopLoading();
                            if (resp.isOk()) {
                                JOptionPane.showMessageDialog(null, "Created student.");
                            }
                            else {
                                JOptionPane.showMessageDialog(null, (String) resp.getBody());
                            }
                        } catch (TimeoutException err) {
                            frame.showTimeoutDialog();
                        } catch (Exception err) {
                            err.printStackTrace();
                            JOptionPane.showMessageDialog(null, err);
                        }
                    };
                };
                worker.execute();
                // frame.showLoading();
            }
        });
        
        JPanel panel = createForm.getPanel();
        panel.add(submitBtn, BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    }

    @Override
    void onLoad() {
        initForm();
        add(createForm.getPanel());
        refreshPanel();
    }

    @Override
    void onUnload() {
        createForm.removeAll();
        removeAll();
    }
}

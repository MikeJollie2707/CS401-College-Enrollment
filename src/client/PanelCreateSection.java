package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.*;

import objects.*;

public class PanelCreateSection extends PanelBase {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;

    private JComboBox<String> courseDropdown; // Store Course objects directly in the dropdown
    private JPanel self;
    private Course selectedCourse;
    private SectionCreateForm form;
    private HashMap<String, Course> courseMap = new HashMap<>();

    public PanelCreateSection(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.self = this;
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;
        courseDropdown = new JComboBox<>();

        form = null;
    }

    SwingWorker<ServerMsg, Void> createSectionWorker(Section body) {
        SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
            @Override
            protected ServerMsg doInBackground() throws Exception {
                ostream.writeObject(new ClientMsg("CREATE", "section", body));
                return (ServerMsg) istream.readObject();
            }

            @Override
            protected void done() {
                try {
                    var resp = get(3, TimeUnit.SECONDS);
                    frame.stopLoading();
                    if (resp.isOk()) {
                        JOptionPane.showMessageDialog(self, "Section created successfully.");
                    } else {
                        JOptionPane.showMessageDialog(self, (String) resp.getBody());
                    }
                } catch (TimeoutException err) {
                    frame.showTimeoutDialog();
                } catch (Exception err) {
                    err.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An internal error occurred.");
                }
            }
        };
        return worker;
    }

    SwingWorker<ServerMsg, Void> getCoursesWorker() {
        SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
            @Override
            protected ServerMsg doInBackground() throws Exception {
                ostream.writeObject(new ClientMsg("GET", "courses", new BodyCourseSearch()));
                return (ServerMsg) istream.readObject();
            }

            @Override
            protected void done() {
                try {
                    var resp = get(3, TimeUnit.SECONDS);
                    if (resp.isOk()) {
                        var cs = (Course[]) resp.getBody();
                        // courseDropdown = new JComboBox<>();
                        courseDropdown.removeAll();
                        for (Course c : cs) {
                            String courseInfo = c.getID() + ", " + c.getName();
                            courseDropdown.addItem(courseInfo);
                            courseMap.put(courseInfo, c);
                        }
                        refreshPanel();
                        // SwingUtilities.invokeLater(() -> {
                        // courseForm.addEntry(new JLabel("Select Course:"), courseDropdown, () -> {
                        // String selectedCourseDescription = (String) courseDropdown.getSelectedItem();
                        // Course selectedCourse = courseMap.get(selectedCourseDescription);
                        // return selectedCourse != null ? selectedCourse.getID() : "";
                        // });
                        // // initForm();
                        // refreshPanel();
                        // });
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: Unable to fetch courses");
                    }
                    frame.stopLoading();
                } catch (TimeoutException err) {
                    frame.showTimeoutDialog();
                } catch (Exception err) {
                    err.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An internal error occurred.");
                }
            }
        };
        return worker;
    }

    void initForm() {
        form = new SectionCreateForm();

        JPanel formPanel = form.getPanel();
        JButton submitBtn = new JButton("Create");
        submitBtn.setBackground(Color.GREEN);
        submitBtn.setForeground(Color.BLACK);
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCourse = courseMap.get(courseDropdown.getSelectedItem());
                Section newSection = form.getNewSection(selectedCourse);
                SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
                    @Override
                    protected ServerMsg doInBackground() throws Exception {
                        ostream.writeObject(new ClientMsg("CREATE", "section", newSection));
                        return (ServerMsg) istream.readObject();
                    }

                    @Override
                    protected void done() {
                        try {
                            var resp = get(3, TimeUnit.SECONDS);
                            frame.stopLoading();
                            if (resp.isOk()) {
                                JOptionPane.showMessageDialog(self, "Section created successfully.");
                            } else {
                                JOptionPane.showMessageDialog(self, (String) resp.getBody());
                            }
                        } catch (TimeoutException err) {
                            frame.showTimeoutDialog();
                        } catch (Exception err) {
                            err.printStackTrace();
                            JOptionPane.showMessageDialog(null, "An internal error occurred.");
                        }
                    }
                };
                worker.execute();
                // frame.showLoading();
            }
        });
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        formPanel.add(submitBtn);
    }

    @Override
    void onLoad() {
        var worker = getCoursesWorker();
        worker.execute();
        add(courseDropdown);
        initForm();
        add(form.getPanel());
        // frame.showLoading();
    }

    @Override
    void onUnload() {
        courseDropdown.removeAllItems();
        if (form != null) {
            form.getPanel().removeAll();
        }
        removeAll();
    }
}

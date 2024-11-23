package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.swing.*;

import objects.*;

public class PanelCreateCourse extends PanelBase {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;

    private BuilderForm courseForm;
    private JPanel prereqPanel;
    private JPanel self;
    private ArrayList<JComboBox<String>> prereqEntries;
    private String[] courses;

    public PanelCreateCourse(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.self = this;
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;

        courseForm = new BuilderForm(null);
        prereqPanel = new JPanel();
        prereqPanel.setLayout(new BoxLayout(prereqPanel, BoxLayout.Y_AXIS));
        initForm();
        initPrereqPanel();

        JPanel formPanel = courseForm.getPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    }

    SwingWorker<ServerMsg, Void> createCourseWorker(Course body) {
        SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
            @Override
            protected ServerMsg doInBackground() throws Exception {
                ostream.writeObject(new ClientMsg("CREATE", "course", body));
                return (ServerMsg) istream.readObject();
            }

            @Override
            protected void done() {
                try {
                    var resp = get();
                    frame.stopLoading();
                    if (resp.isOk()) {
                        JOptionPane.showMessageDialog(self, "Course created successfully.");
                    } else {
                        JOptionPane.showMessageDialog(self, (String) resp.getBody());
                    }
                } catch (Exception err) {
                    err.printStackTrace();
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
                    var resp = get();
                    if (resp.isOk()) {
                        var cs = (Course[]) resp.getBody();
                        courses = Arrays.stream(cs)
                                .map((Course c) -> {
                                    return c.getID();
                                })
                                .collect(Collectors.toList())
                                .toArray(new String[0]);
                        refreshPanel();
                    } else {
                        System.out.println("Uh oh");
                    }
                    frame.stopLoading();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        };
        return worker;
    }

    void initForm() {
        JTextField prefixField = new JTextField(10);
        JTextField numberField = new JTextField(10);
        JTextField descField = new JTextField(10);

        courseForm.addEntry(new JLabel("Prefix:"), prefixField, () -> prefixField.getText());
        courseForm.addEntry(new JLabel("Number:"), numberField, () -> numberField.getText());
        courseForm.addEntry(new JLabel("Description:"), descField, () -> descField.getText());
        courseForm.addEntry(new JLabel("Prerequisites:"), prereqPanel, () -> {
            // Dropdown has default values so we need this to filter the duplicates.
            HashSet<String> unique = new HashSet<>();
            for (var input : prereqEntries) {
                var item = (String) input.getSelectedItem();
                if (!item.isBlank()) {
                    unique.add(item);
                }
            }
            String out = String.join("\n", unique);
            return out;
        });

        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var results = courseForm.getResults();

                String prefix = results.get(0);
                String number = results.get(1);
                String description = results.get(2);
                String prereqs = results.get(3);

                Course course = new Course(prefix, number, description);
                String[] parts = prereqs.split("\n");
                for (var part : parts) {
                    String[] prereqInfo = part.split(" ");
                    course.insertPrereq(new Course(prereqInfo[0], prereqInfo[1], ""));
                }

                var worker = createCourseWorker(course);
                worker.execute();
                frame.showLoading();
            }
        });
        JPanel formPanel = courseForm.getPanel();
        formPanel.add(submitBtn);
    }

    void initPrereqPanel() {
        prereqEntries = new ArrayList<>();
        JButton newPrereqBtn = new JButton("+");
        newPrereqBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (prereqEntries.size() < 5) {
                    JComboBox<String> dropdown = new JComboBox<>(courses);
                    prereqEntries.add(dropdown);
                    prereqPanel.add(dropdown);
                    prereqPanel.revalidate();
                    prereqPanel.repaint();
                }
            }
        });
        prereqPanel.add(newPrereqBtn);
        JButton delPrereqBtn = new JButton("-");
        delPrereqBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (prereqEntries.size() > 0) {
                    prereqPanel.remove(prereqEntries.getLast());
                    prereqEntries.removeLast();
                    prereqPanel.revalidate();
                    prereqPanel.repaint();
                }
            }
        });
        prereqPanel.add(delPrereqBtn);
    }

    @Override
    void onLoad() {
        initForm();
        initPrereqPanel();
        var worker = getCoursesWorker();
        worker.execute();
        add(courseForm.getPanel());
        frame.showLoading();
    }

    @Override
    void onUnload() {
        courseForm.removeAll();
        prereqPanel.removeAll();
        removeAll();
    }
}

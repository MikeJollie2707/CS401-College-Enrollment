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

public class CourseEditForm {
    private BuilderForm form;
    private Course course;
    private ObjectOutputStream ostream;
    private ObjectInputStream istream;

    private String[] courses;
    private JPanel panel;
    private JPanel prereqPanel;
    private ArrayList<JComboBox<String>> prereqEntries;
    private JPanel addOrRemovePrereqPanel;

    public CourseEditForm(Course course, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.course = course;
        this.ostream = ostream;
        this.istream = istream;
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        prereqEntries = new ArrayList<>();

        form = new BuilderForm(null);
        prereqPanel = new JPanel();
        prereqPanel.setLayout(new BoxLayout(prereqPanel, BoxLayout.Y_AXIS));
        addOrRemovePrereqPanel = new JPanel();

        initForm();
        var worker = getCoursesWorker();
        worker.execute();
    }

    public JPanel getPanel() {
        return panel;
    }

    public Course getEditedCourse() {
        var results = form.getResults();

        String name = results.get(0);
        String description = results.get(1);
        String prereqs = results.get(2);

        Course newCourse = new Course(course.getPrefix(), course.getNumber(), name, description);
        String[] parts = prereqs.split("\n");
        for (var part : parts) {
            if (!part.isBlank()) {
                String[] prereqInfo = part.split(" ");
                newCourse.insertPrereq(new Course(prereqInfo[0], prereqInfo[1], "", ""));
            }
        }
        return newCourse;
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
                        initPrereqPanel();
                        panel.revalidate();
                        panel.repaint();
                    } else {
                        System.out.println("Uh oh");
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        };
        return worker;
    }

    void initForm() {
        JTextField nameField = new JTextField(course.getName());
        JTextArea descriptionField = new JTextArea(course.getDescription());
        descriptionField.setLineWrap(true);
        descriptionField.setColumns(25);
        descriptionField.setRows(5);

        form.addEntry(new JLabel("Subject:"), nameField, () -> nameField.getText());
        form.addEntry(new JLabel("Description:"), descriptionField, () -> descriptionField.getText());
        form.addEntry(new JLabel("Prerequisites:"), prereqPanel, () -> {
            HashSet<String> unique = new HashSet<>();
            for (var input : prereqEntries) {
                var item = (String) input.getSelectedItem();
                if (!item.isBlank()) {
                    unique.add(item);
                }
            }
            return String.join("\n", unique);
        });

        JPanel formPanel = form.getPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        panel.add(formPanel);

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
        addOrRemovePrereqPanel.add(newPrereqBtn);
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
        addOrRemovePrereqPanel.add(delPrereqBtn);
        panel.add(addOrRemovePrereqPanel);
    }

    void initPrereqPanel() {
        for (var prereqID : course.getPrerequisites()) {
            for (var courseID : courses) {
                if (prereqID.equals(courseID)) {
                    JComboBox<String> dropdown = new JComboBox<>(courses);
                    dropdown.setSelectedItem(prereqID);
                    prereqEntries.add(dropdown);
                    prereqPanel.add(dropdown);
                }
            }
        }

    }
}

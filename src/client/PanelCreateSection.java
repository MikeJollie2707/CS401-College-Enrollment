package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.swing.*;

import objects.*;

public class PanelCreateSection extends PanelBase {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;
    
    private JComboBox<String> courseDropdown;  // Store Course objects directly in the dropdown
    private BuilderForm courseForm;
    private JPanel self;
    private String[] courses;
    private HashMap<String, Course> courseMap = new HashMap<>();
    
    public PanelCreateSection(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.self = this;
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;

        courseForm = new BuilderForm(null);
        JPanel formPanel = courseForm.getPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
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
                    var resp = get();
                    frame.stopLoading();
                    if (resp.isOk()) {
                        JOptionPane.showMessageDialog(self, "Section created successfully.");
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

                        courseDropdown = new JComboBox<>();
                        for(Course c: cs) {
                            String courseInfo = c.getPrefix() + c.getNumber() + ", " + c.getDescription();
                            courseDropdown.addItem(courseInfo);
                            courseMap.put(courseInfo, c);
                        }
                        SwingUtilities.invokeLater(() -> {
                            courseForm.addEntry(new JLabel("Select Course:"), courseDropdown, () -> {
                                String selectedCourseDescription = (String) courseDropdown.getSelectedItem();
                                Course selectedCourse = courseMap.get(selectedCourseDescription);
                                return selectedCourse != null ? selectedCourse.getID() : "";
                            });
                            initForm();
                            refreshPanel();
                        });
                    } else {
                        System.out.println("Error: Unable to fetch courses");
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
        JTextField secNum = new JTextField(10);
        JTextField secCapacity = new JTextField(10);
        JTextField secWaitlist = new JTextField(10);
        JTextField secInstructor = new JTextField(10);
        courseForm.addEntry(new JLabel("Section Number:"), secNum, () -> secNum.getText());
        courseForm.getPanel().add(Box.createVerticalStrut(10));
        courseForm.addEntry(new JLabel("Max Capacity:"), secCapacity, () -> secCapacity.getText());
        courseForm.getPanel().add(Box.createVerticalStrut(10));
        courseForm.addEntry(new JLabel("Waitlist Size:"), secWaitlist, () -> secWaitlist.getText());
        courseForm.getPanel().add(Box.createVerticalStrut(10));
        courseForm.addEntry(new JLabel("Instructor Name:"), secInstructor, () -> secInstructor.getText());
        courseForm.getPanel().add(Box.createVerticalStrut(20));
        
        JButton submitBtn = new JButton("Submit section");
        submitBtn.setBackground(Color.GREEN);
        submitBtn.setForeground(Color.BLACK);
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var results = courseForm.getResults();
                
                Course selectedCourse = courseMap.get(courseDropdown.getSelectedItem());

                
                String secNumStr = results.get(1);
                int secCapacityInt = Integer.parseInt(results.get(2));
                int secWaitlistInt = Integer.parseInt(results.get(3));
                for (int i = 0; i < results.get(4).length(); i++) {
                    char c = results.get(4).charAt(i);
                    if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                        JOptionPane.showMessageDialog(self, "Instructor name must contain only letters/spaces.");
                        return;
                    }
                }
                Instructor instructorVal = new Instructor(results.get(4), null);
                
                if (results.get(1).isEmpty() || results.get(2).isEmpty() || results.get(3).isEmpty() 
                        || results.get(4).isEmpty()) {
                    JOptionPane.showMessageDialog(self, "Full section information must be provided.");
                    return;
                }
                if (Integer.parseInt(secNumStr) <= 0 || secWaitlistInt <= 0 || secWaitlistInt <= 0) {
                    JOptionPane.showMessageDialog(self, "Must use valid numbers > 0 for section.");
                    return;
                }
               
                Section section = new Section(selectedCourse, secNumStr, secCapacityInt, secWaitlistInt, instructorVal);
                System.out.println("Creating section:" + selectedCourse.getPrefix() + selectedCourse.getNumber() +"-" + results.get(1) + " " + results.get(2)+ " " + " " + results.get(3) + " "  + results.get(4));
                var worker = createSectionWorker(section);
                worker.execute();
                frame.showLoading();
            }
        });
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        JPanel formPanel = courseForm.getPanel();
        formPanel.add(submitBtn);
    }

    @Override
    void onLoad() {
        var worker = getCoursesWorker();
        worker.execute();
        JPanel centered = new JPanel(new FlowLayout(FlowLayout.CENTER));
        courseForm.getPanel().add(Box.createVerticalStrut(100));
        centered.add(courseForm.getPanel(), BorderLayout.CENTER);
        add(centered,BorderLayout.CENTER);
        frame.showLoading();
    }

    @Override
    void onUnload() {
        courseForm.removeAll();
        removeAll();
    }
}

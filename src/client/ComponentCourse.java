package client;

import java.awt.Color;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import objects.ClientMsg;
import objects.Course;
import objects.Section;
import objects.ServerMsg;
import objects.Student;

public class ComponentCourse {
    private Course course;
    private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;
    private List<JButton> sectionButtons = new ArrayList<>();
    private Student currentStudent;
    private CourseState courseState;
    
    public ComponentCourse(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream, Course course, CourseState existingState) {
        this.course = course;
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;
        this.currentStudent = (Student) frame.getMe();
        this.sectionButtons = new ArrayList<>();
        
        if (existingState != null) {
            this.courseState = existingState;
        } else {
            this.courseState = CourseStateManager.getInstance().getOrCreateState(course, currentStudent);
        }
    }
    
    public JScrollPane build() {
        JPanel panel = new JPanel();
        String prefix = course.getPrefix();
        String number = course.getNumber();
        String desc = course.getDescription();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel courseHeader = new JPanel();
        courseHeader.setLayout(new FlowLayout(FlowLayout.CENTER));
        courseHeader.add(new JLabel("Course: " + prefix + " " + number + ", " + desc));
        panel.add(courseHeader);

        JPanel sectionsLabelPanel = new JPanel();
        sectionsLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        sectionsLabelPanel.add(new JLabel("Sections available:"));
        panel.add(sectionsLabelPanel);

        for (var section : course.getSections()) {
            JPanel sectionPanel = new JPanel(new FlowLayout());
            sectionPanel.add(new JLabel(prefix + number + "-" + section.getNumber()));
            sectionPanel.add(new JLabel("Max Capacity: " + section.getMaxCapacity()));
            sectionPanel.add(new JLabel("Max Waitlist Size: " + section.getMaxWaitlistSize()));
            sectionPanel.add(new JLabel("Instructor: " + section.getInstructor().getName()));

            SectionState sectionState = courseState.getSectionState(section);

            JButton actionButton = new JButton();
            
            if (sectionState.isWaitlisted()) {
                actionButton.setText("Drop From Waitlist");
                actionButton.setBackground(Color.BLUE);
                actionButton.setForeground(Color.BLACK);
            } else if (sectionState.isEnrolled()) {
                actionButton.setText("Drop Enrollment");
                actionButton.setBackground(Color.RED);
                actionButton.setForeground(Color.BLACK);
            } else {
                actionButton.setText("Enroll");
                actionButton.setBackground(Color.GREEN);
                actionButton.setForeground(Color.BLACK);
            }
            
            actionButton.addActionListener(e -> {
                String buttonText = actionButton.getText();
                if (buttonText.equals("Drop From Waitlist")) {
                    dropWaitlist(section, sectionState, actionButton);
                } else if (buttonText.equals("Enroll") || buttonText.equals("Enroll Waitlist")) {
                    enrollCourse(section, sectionState, actionButton);
                } else if (buttonText.equals("Drop Enrollment")) {
                    dropEnrollment(section, sectionState, actionButton);
                }
            });
            
            sectionButtons.add(actionButton);
            sectionPanel.add(actionButton);
            panel.add(sectionPanel);
        }
        
        JScrollPane scroll = new JScrollPane(panel);
        return scroll;
    }

    private void dropWaitlist(Section section, SectionState sectionState, JButton button) {
        try {
            ostream.writeObject(new ClientMsg("CREATE", "drop", section));
            var ServerMsg = (ServerMsg) istream.readObject();
            if (ServerMsg.isOk()) {
                sectionState.setWaitlisted(false);
                button.setText("Enroll Waitlist");
                button.setBackground(Color.BLUE);
                button.setForeground(Color.BLACK);
                enableAllButtons();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void enrollCourse(Section section, SectionState sectionState, JButton button) {
        try {
            ostream.writeObject(new ClientMsg("CREATE", "enroll", section));
            var ServerMsg = (ServerMsg) istream.readObject();
            if (ServerMsg.isOk()) {
                sectionState.setEnrolled(true);
                sectionState.setWaitlisted(false);
                button.setText("Drop Enrollment");
                button.setBackground(Color.RED);
                button.setForeground(Color.BLACK);
                disableAllOtherButtons(section, button);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void dropEnrollment(Section section, SectionState sectionState, JButton button) {
        try {
            ostream.writeObject(new ClientMsg("CREATE", "drop", section));
            var ServerMsg = (ServerMsg) istream.readObject();
            if (ServerMsg.isOk()) {
                sectionState.setEnrolled(false);
                button.setText("Enroll");
                button.setBackground(Color.GREEN);
                button.setForeground(Color.BLACK);
                enableAllButtons();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void disableAllOtherButtons(Section section, JButton button) {
        for (JButton b : sectionButtons) {
            if(b != button)
                b.setEnabled(false);
        }
    }

    private void enableAllButtons() {
        for (JButton button : sectionButtons) {
            button.setEnabled(true);
        }
    }
}
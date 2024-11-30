package client;

import java.awt.Color;
import java.awt.FlowLayout;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public ComponentCourse(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream, Course course,
            CourseState existingState) {
        this.course = course;
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;
        this.currentStudent = (Student) frame.getMe();
        this.sectionButtons = new ArrayList<>();
        if (existingState != null) {
            this.courseState = existingState;
        } else {
            // this.courseState = CourseStateManager.getInstance().getOrCreateState(course,
            // currentStudent);
        }
    }

    public JScrollPane build() {
        JPanel panel = new JPanel();
        String prefix = course.getPrefix();
        String number = course.getNumber();
        String name = course.getName();
        String desc = course.getDescription();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel courseHeader = new JPanel();
        courseHeader.setLayout(new FlowLayout(FlowLayout.CENTER));
        courseHeader.add(new JLabel("Course: " + prefix + " " + number + ", " + name));
        panel.add(courseHeader);

        JPanel sectionsLabelPanel = new JPanel();
        sectionsLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        sectionsLabelPanel.add(new JLabel("Sections available:"));
        panel.add(sectionsLabelPanel);

        for (var section : course.getSections()) {
            JPanel sectionPanel = new JPanel();
            sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
            JPanel headerPanel = new JPanel();
            headerPanel.add(new JLabel(prefix + number + "-" + section.getNumber()));
            headerPanel.add(new JLabel("Max Capacity: " + section.getMaxCapacity()));
            headerPanel.add(new JLabel("Max Waitlist Size: " + section.getMaxWaitlistSize()));
            headerPanel.add(new JLabel("Instructor: " + section.getInstructor().getName()));

            JPanel schedulePanel = new JPanel();
            schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
            for (var entry : section.getSchedule()) {
                JPanel row = new JPanel();
                var time = entry.getTime();
                row.add(new JLabel(entry.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US)));
                row.add(new JLabel(String.format("Start: %s - End: %s",
                        time.getStart().toLocalTime(), time.getEnd().toLocalTime())));
                row.add(new JLabel(String.format("@ %s", entry.getLocation())));
                row.add(new JLabel(String.format("(%s)", entry.isSync() ? "sync" : "async")));
                schedulePanel.add(row);
            }

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

            actionButton.addActionListener(_ -> {
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
            headerPanel.add(actionButton);
            sectionPanel.add(headerPanel);
            sectionPanel.add(schedulePanel);
            panel.add(sectionPanel);
        }
        JScrollPane scroll = new JScrollPane(panel);
        return scroll;
    }

    private void dropWaitlist(Section section, SectionState sectionState, JButton button) {
        try {
            SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
                @Override
                protected ServerMsg doInBackground() throws Exception {
                    ostream.writeObject(new ClientMsg("CREATE", "drop", section));
                    return (ServerMsg) istream.readObject();
                }
            };
            worker.execute();
            var resp = worker.get(3, TimeUnit.SECONDS);
            if (resp.isOk()) {
                sectionState.setWaitlisted(false);
                button.setText("Enroll Waitlist");
                button.setBackground(Color.BLUE);
                button.setForeground(Color.BLACK);
                enableAllButtons();
            } else {
                JOptionPane.showMessageDialog(null, (String) resp.getBody());
            }
        } catch (TimeoutException err) {
            frame.showTimeoutDialog();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void enrollCourse(Section section, SectionState sectionState, JButton button) {
        try {
            SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
                @Override
                protected ServerMsg doInBackground() throws Exception {
                    ostream.writeObject(new ClientMsg("CREATE", "enroll", section));
                    return (ServerMsg) istream.readObject();
                }
            };
            worker.execute();
            var resp = worker.get(3, TimeUnit.SECONDS);
            if (resp.isOk()) {
                sectionState.setEnrolled(true);
                sectionState.setWaitlisted(false);
                button.setText("Drop Enrollment");
                button.setBackground(Color.RED);
                button.setForeground(Color.BLACK);
                disableAllOtherButtons(section, button);
            } else {
                JOptionPane.showMessageDialog(null, (String) resp.getBody());
            }
        } catch (TimeoutException err) {
            frame.showTimeoutDialog();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException err) {

        }
    }

    private void dropEnrollment(Section section, SectionState sectionState, JButton button) {
        try {
            SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
                @Override
                protected ServerMsg doInBackground() throws Exception {
                    ostream.writeObject(new ClientMsg("CREATE", "drop", section));
                    return (ServerMsg) istream.readObject();
                }
            };
            worker.execute();
            var resp = worker.get(3, TimeUnit.SECONDS);
            if (resp.isOk()) {
                sectionState.setEnrolled(false);
                button.setText("Enroll");
                button.setBackground(Color.GREEN);
                button.setForeground(Color.BLACK);
                enableAllButtons();
            } else {
                JOptionPane.showMessageDialog(null, (String) resp.getBody());
            }
        } catch (TimeoutException err) {
            frame.showTimeoutDialog();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void disableAllOtherButtons(Section section, JButton button) {
        for (JButton b : sectionButtons) {
            if (b != button)
                b.setEnabled(false);
        }
    }

    private void enableAllButtons() {
        for (JButton button : sectionButtons) {
            button.setEnabled(true);
        }
    }
}
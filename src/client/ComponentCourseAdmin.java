package client;

import java.awt.FlowLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import objects.BodyCourseSearch;
import objects.ClientMsg;
import objects.Course;
import objects.Section;
import objects.ServerMsg;

public class ComponentCourseAdmin {
    private Course course;
    private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;
    private List<JButton> sectionButtons;
    private JPanel panel;
    private PanelCatalog panelCatalog;

    public ComponentCourseAdmin(MainFrame frame, PanelCatalog panelCatalog, ObjectOutputStream ostream,
            ObjectInputStream istream, Course course) {
        this.course = course;
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;
        this.panelCatalog = panelCatalog;
        sectionButtons = new ArrayList<>();
    }

    public JScrollPane build() {
        panel = new JPanel();
        String prefix = course.getPrefix();
        String number = course.getNumber();
        String name = course.getName();
        String desc = course.getDescription();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel courseHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel courseLabel = new JLabel("Course: " + prefix + " " + number + ", " + name);
        courseHeader.add(courseLabel);

        JButton editCourseBtn = new JButton("Edit Course");
        editCourseBtn.addActionListener(e -> {
            CourseEditForm courseForm = new CourseEditForm(course, ostream, istream);
            int result = JOptionPane.showConfirmDialog(null, new JScrollPane(courseForm.getPanel()), "Edit Course",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Course newCourse = courseForm.getEditedCourse();
                editCourse(newCourse);
            }
        });
        JButton deleteCourseBtn = new JButton("Delete Course");
        deleteCourseBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(null,
                    "This will delete ALL active sections, are you sure to continue?",
                    "Delete Course",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                deleteCourse();
            }
        });
        courseHeader.add(editCourseBtn);
        courseHeader.add(deleteCourseBtn);

        panel.add(courseHeader);

        JPanel sectionsLabelPanel = new JPanel();
        sectionsLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        sectionsLabelPanel.add(new JLabel("Sections:"));
        panel.add(sectionsLabelPanel);

        sectionButtons.clear();

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

            JButton editButton = new JButton("Edit Section");
            editButton.addActionListener(e -> {
                SectionEditForm form = new SectionEditForm(section);
                int result = JOptionPane.showConfirmDialog(null, new JScrollPane(form.getPanel()), "Edit Section",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    Section newSection = null;
                    try {
                        newSection = form.getEditedSection();
                    } catch (Exception err) {
                        JOptionPane.showMessageDialog(null, "One of the field is invalid.");
                        return;
                    }

                    editSection(newSection, sectionPanel);
                }
            });
            sectionButtons.add(editButton);

            JButton deleteButton = new JButton("Delete Section");
            deleteButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(null,
                        "This will drop ALL students and instructors from this section, are you sure to continue?",
                        "Delete Section",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    deleteSection(section, sectionPanel);
                }
            });
            sectionButtons.add(deleteButton);

            headerPanel.add(editButton);
            headerPanel.add(deleteButton);
            sectionPanel.add(headerPanel);
            sectionPanel.add(schedulePanel);
            panel.add(sectionPanel);
        }

        JScrollPane scroll = new JScrollPane(panel);
        return scroll;
    }

    private void deleteCourse() {
        try {
            ostream.writeObject(new ClientMsg("DELETE", "course", course));
            var resp = (ServerMsg) istream.readObject();
            if (resp.isOk()) {
                SwingUtilities.invokeLater(() -> {
                    panelCatalog.getSearchWorker(new BodyCourseSearch()).execute();
                });
            } else {
                JOptionPane.showMessageDialog(null, (String) resp.getBody());
            }
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void editCourse(Course course) {
        try {
            ostream.writeObject(new ClientMsg("EDIT", "course", course));
            var resp = (ServerMsg) istream.readObject();
            if (resp.isOk()) {
                // refreshCourse();
                SwingUtilities.invokeLater(() -> {
                    panelCatalog.getSearchWorker(new BodyCourseSearch()).execute();
                });
            } else {
                JOptionPane.showMessageDialog(null, (String) resp.getBody());
            }
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void editSection(Section section, JPanel sectionPanel) {
        try {
            ostream.writeObject(new ClientMsg("EDIT", "section", section));
            var resp = (ServerMsg) istream.readObject();
            if (resp.isOk()) {
                // refreshCourse();
                SwingUtilities.invokeLater(() -> {
                    panelCatalog.getSearchWorker(new BodyCourseSearch()).execute();
                });
            } else {
                JOptionPane.showMessageDialog(null, (String) resp.getBody());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void deleteSection(Section section, JPanel sectionPanel) {
        try {
            ostream.writeObject(new ClientMsg("DELETE", "section", section));
            var resp = (ServerMsg) istream.readObject();
            if (resp.isOk()) {
                sectionPanel.removeAll();
                sectionPanel.revalidate();
                sectionPanel.repaint();
                // refreshCourse();
                SwingUtilities.invokeLater(() -> {
                    panelCatalog.getSearchWorker(new BodyCourseSearch()).execute();
                });
            } else {
                JOptionPane.showMessageDialog(null, (String) resp.getBody());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
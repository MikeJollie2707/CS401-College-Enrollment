package client;

import java.awt.Color;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import objects.BodyCourseSearch;
import objects.ClientMsg;
import objects.Course;
import objects.Section;
import objects.ServerMsg;
import objects.Instructor;

public class ComponentCourseAdmin {
    private Course course;
    private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;
    private List<JButton> sectionButtons = new ArrayList<>();
    private JPanel panel;
    private JPanel resultPanel;
    private PanelCatalog panelCatalog;
    public ComponentCourseAdmin(MainFrame frame, PanelCatalog panelCatalog, ObjectOutputStream ostream, ObjectInputStream istream, Course course) {
        this.course = course;
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;
        this.panelCatalog = panelCatalog;
    }

    public JScrollPane build() {
        panel = new JPanel();
        String prefix = course.getPrefix();
        String number = course.getNumber();
        String desc = course.getDescription();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel courseHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel courseLabel = new JLabel("Course: " + prefix + " " + number + ", " + desc);
        courseHeader.add(courseLabel);

        JButton deleteCourseBtn = new JButton("Delete Course");
        deleteCourseBtn.addActionListener(e -> {
            deleteCourse();
        });
        courseHeader.add(deleteCourseBtn);

        panel.add(courseHeader);

        JPanel sectionsLabelPanel = new JPanel();
        sectionsLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        sectionsLabelPanel.add(new JLabel("Sections:"));
        panel.add(sectionsLabelPanel);

        sectionButtons.clear();

        for (var section : course.getSections()) {
            JPanel sectionPanel = new JPanel(new FlowLayout());
            sectionPanel.add(new JLabel(prefix + number + "-" + section.getNumber()));
            sectionPanel.add(new JLabel("Max Capacity: " + section.getMaxCapacity()));
            sectionPanel.add(new JLabel("Max Waitlist Size: " + section.getMaxWaitlistSize()));
            sectionPanel.add(new JLabel("Instructor: " + section.getInstructor().getName()));

            JButton editButton = new JButton("Edit Section");
            editButton.addActionListener(e -> {
                editSection(section, sectionPanel);
            });
            sectionButtons.add(editButton);
            sectionPanel.add(editButton, sectionPanel);

            JButton deleteButton = new JButton("Delete Section");
            deleteButton.addActionListener(e -> {
                deleteSection(section, sectionPanel);
            });
            
            sectionButtons.add(deleteButton);
            sectionPanel.add(deleteButton);
            panel.add(sectionPanel);
        }
        JButton addSectionButton = new JButton("Add New Section");
        addSectionButton.addActionListener(e -> {
            addNewSection();
            panel.revalidate();
            panel.repaint();
        });
        panel.add(addSectionButton);

        JScrollPane scroll = new JScrollPane(panel);
        return scroll;
    }

    private void deleteCourse() {
        try {
            ostream.writeObject(new ClientMsg("DELETE", "course", course));
            var ServerMsg = (ServerMsg) istream.readObject();
            if (ServerMsg.isOk()) {
                SwingUtilities.invokeLater(() -> {
                    panelCatalog.getSearchWorker(new BodyCourseSearch()).execute();
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addNewSection() {
        try {
            ostream.writeObject(new ClientMsg("CREATE", "section", course));
            var ServerMsg = (ServerMsg) istream.readObject();
            if (ServerMsg.isOk()) {
                //refreshCourse();
                SwingUtilities.invokeLater(() -> {
                    panelCatalog.getSearchWorker(new BodyCourseSearch()).execute();
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void editSection(Section section, JPanel sectionpanel) {
        try {
            ostream.writeObject(new ClientMsg("EDIT", "section", section));
            var ServerMsg = (ServerMsg) istream.readObject();
            if (ServerMsg.isOk()) {
               // refreshCourse();
                SwingUtilities.invokeLater(() -> {
                    panelCatalog.getSearchWorker(new BodyCourseSearch()).execute();
                });
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
            var ServerMsg = (ServerMsg) istream.readObject();
            if (ServerMsg.isOk()) {
                sectionPanel.removeAll();
                sectionPanel.revalidate();
                sectionPanel.repaint();
            //    refreshCourse();
                SwingUtilities.invokeLater(() -> {
                    panelCatalog.getSearchWorker(new BodyCourseSearch()).execute();
                });
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
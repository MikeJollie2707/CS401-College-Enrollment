package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;

import objects.*;

public class CatalogPanel extends RAIIPanel {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;

    public CatalogPanel(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;
    }

    @Override
    void onLoad() {
        SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
            @Override
            protected ServerMsg doInBackground() throws Exception {
                BodyCourseSearch searchBody = new BodyCourseSearch();
                // no filter, all courses (for now)
                searchBody.setCourseName("");
                searchBody.setCoursePrefix("");
                searchBody.setCourseNumber("");
                searchBody.setInstructorName("");
                ClientMsg searchAttempt = new ClientMsg("GET", "courses", searchBody);
                ostream.writeObject(searchAttempt);
                return (ServerMsg) istream.readObject();
            }

            @Override
            protected void done() {
                try {
                    var resp = get();

                    if (resp.isOk()) {
                        Course[] courses = (Course[]) resp.getBody();
                        String[] columnNames = { "Course Prefix", "Course Number", "Course Prerequisites" };
                        Object[][] data = new Object[courses.length][columnNames.length];

                        for (int i = 0; i < courses.length; i++) {
                            Course course = courses[i];
                            data[i][0] = course.getPrefix();
                            data[i][1] = course.getNumber();
                        }
                        if (courses.length < 1) {
                            JLabel notFound = new JLabel("No courses found in :(");
                            notFound.setFont(new Font("Arial", Font.BOLD, 20));
                            notFound.setForeground(Color.BLUE);
                            add(notFound, BorderLayout.CENTER);
                        } else {
                            JLabel coursesFound = new JLabel("Course Catalog of :");
                            coursesFound.setFont(new Font("Arial", Font.BOLD, 20));
                            coursesFound.setForeground(Color.BLUE);

                            JTable catalogTable = new JTable(data, columnNames);
                            catalogTable.setDefaultEditor(Object.class, null);
                            JScrollPane scroll = new JScrollPane(catalogTable);

                            JPanel textPanel = new JPanel(new FlowLayout());
                            JPanel catalogPanel = new JPanel(new FlowLayout());
                            textPanel.add(coursesFound);
                            catalogPanel.add(scroll);

                            add(textPanel, BorderLayout.NORTH);
                            add(catalogPanel, BorderLayout.CENTER);

                        }
                        refreshPanel();
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    @Override
    void onUnload() {
        removeAll();
    }
}

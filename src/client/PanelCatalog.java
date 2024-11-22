package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;

import objects.*;

public class PanelCatalog extends PanelBase {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;

    private BuilderForm searchForm;
    private JPanel resultPanel;

    public PanelCatalog(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;

        searchForm = new BuilderForm(null);
        resultPanel = new JPanel();

        JPanel searchPanel = searchForm.getPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
    }

    SwingWorker<ServerMsg, Void> getSearchWorker(BodyCourseSearch body) {
        SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
            @Override
            protected ServerMsg doInBackground() throws Exception {
                // Have to create a new body every time
                // otherwise it'll reuse the default body (blank on all fields).
                // It's probably because of thread race, but whatever, it's a lightweight obj,
                // doesn't hurt to create a new one for every request.
                ostream.writeObject(new ClientMsg("GET", "courses", body));
                return (ServerMsg) istream.readObject();
            }

            @Override
            protected void done() {
                try {
                    var resp = get();
                    if (resp.isOk()) {
                        Course[] courses = (Course[]) resp.getBody();
                        resultPanel.removeAll();

                        if (courses.length < 1) {
                            JLabel notFound = new JLabel("No courses found in :(");
                            notFound.setFont(new Font("Arial", Font.BOLD, 20));
                            notFound.setForeground(Color.BLUE);
                            resultPanel.add(notFound, BorderLayout.CENTER);
                        } else {
                            for (int i = 0; i < courses.length; ++i) {
                                resultPanel.add(new ComponentCourse(courses[i]).build());
                            }
                        }
                        refreshPanel();
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
        JTextField instructorField = new JTextField(10);

        searchForm.addEntry(new JLabel("Course prefix:"), prefixField, () -> prefixField.getText());
        searchForm.addEntry(new JLabel("Course number:"), numberField, () -> numberField.getText());
        searchForm.addEntry(new JLabel("Course description:"), descField, () -> descField.getText());
        searchForm.addEntry(new JLabel("Instructor:"), instructorField, () -> instructorField.getText());

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var results = searchForm.getResults();
                BodyCourseSearch body = new BodyCourseSearch();
                body.setCoursePrefix(results.get(0).toLowerCase());
                body.setCourseNumber(results.get(1).toLowerCase());
                body.setCourseName(results.get(2).toLowerCase());
                body.setInstructorName(results.get(3).toLowerCase());
                var worker = getSearchWorker(body);
                worker.execute();
                frame.showLoading();
            }
        });
        JPanel searchPanel = searchForm.getPanel();
        searchPanel.add(searchBtn);
    }

    @Override
    void onLoad() {
        initForm();
        var worker = getSearchWorker(new BodyCourseSearch());
        worker.execute();
        add(searchForm.getPanel());
        add(resultPanel);
        frame.showLoading();
    }

    @Override
    void onUnload() {
        searchForm.removeAll();
        removeAll();
    }
}

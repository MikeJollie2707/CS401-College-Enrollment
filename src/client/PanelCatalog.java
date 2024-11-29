package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import objects.*;

public class PanelCatalog extends PanelBase {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;

    private BuilderForm searchForm;
    private JPanel resultPanel;
    private JScrollPane scroll;
    private Map<String, CourseState> existingStates = new HashMap<>();

    public PanelCatalog(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;

        searchForm = new BuilderForm(null);
        resultPanel = new JPanel();

        JPanel searchPanel = searchForm.getPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        scroll = new JScrollPane(resultPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    /**
     * Get the worker that'll do the course query.
     * 
     * @param body The body to be sent to server. This should be a new object every
     *             time this worker is executed.
     * @return
     */
    SwingWorker<ServerMsg, Void> getSearchWorker(BodyCourseSearch body) {
        SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
            @Override
            protected ServerMsg doInBackground() throws Exception {
                // need to save  states instead of fully resetting component course
                existingStates = CourseStateManager.getInstance().filterStatesByCriteria(body, CourseStateManager.getInstance().getCourseStates());
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
                            JLabel notFound = new JLabel("No courses found :(");
                            notFound.setFont(new Font("Arial", Font.BOLD, 20));
                            notFound.setForeground(Color.BLUE);
                            resultPanel.add(notFound, BorderLayout.CENTER);
                        } else {
                            for (int i = 0; i < courses.length; ++i) {
                                Object me = frame.getMe();
                                if (me instanceof Administrator) {
                                    ComponentCourseAdmin adminComponent = new ComponentCourseAdmin(frame, PanelCatalog.this, ostream, istream, courses[i]);
                                    resultPanel.add(adminComponent.build());
                                    refreshPanel();
                                } else if (me instanceof Student) {
                                    // have to save previous states so all sections status is saved after search
                                    String courseKey = courses[i].getPrefix() + courses[i].getNumber();
                                    CourseState courseState = existingStates.get(courseKey);
                                    ComponentCourse componentCourse = new ComponentCourse(frame, ostream, istream, courses[i], courseState);
                                    resultPanel.add(componentCourse.build());
                                }
                                refreshPanel();
                            }
                        }
                        refreshPanel();
                    }
                    refreshPanel();
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
        add(scroll);
        frame.showLoading();
    }

    @Override
    void onUnload() {
        searchForm.removeAll();
        removeAll();
    }
}
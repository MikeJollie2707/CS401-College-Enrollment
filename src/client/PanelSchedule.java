package client;

import javax.swing.*;

import objects.ClientMsg;
import objects.Section;
import objects.ServerMsg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PanelSchedule extends PanelBase {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;

    public PanelSchedule(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;
    }

    @Override
    public void onLoad() {
        SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
            @Override
            protected ServerMsg doInBackground() throws Exception {
                ostream.writeObject(new ClientMsg("GET", "schedule", null));
                return (ServerMsg) istream.readObject();
            }

            @Override
            protected void done() {
                try {
                    var resp = get(3, TimeUnit.SECONDS);
                    if (resp.isOk()) {
                        Section[] myCourses = (Section[]) resp.getBody();

                        JPanel schedulePanel = new JPanel();
                        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
                        for (int i = 0; i < myCourses.length; i++) {
                            Section section = myCourses[i];
                            String prefix = section.getCourse().getPrefix();
                            String number = section.getCourse().getNumber();
                            JPanel coursesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                            coursesPanel.setBackground(Color.LIGHT_GRAY);

                            coursesPanel.add(new JLabel(prefix + number + "-" + section.getNumber()));
                            coursesPanel.add(new JLabel("Enrollment Max Capacity: " + section.getMaxCapacity()));
                            coursesPanel.add(new JLabel("Max Waitlist Size: " + section.getMaxWaitlistSize()));
                            coursesPanel.add(new JLabel("Instructor: " + section.getInstructor().getName()));
                            // maybe add this
                            // coursesPanel.add(new JLabel("Status: " ));

                            coursesPanel.setPreferredSize(new Dimension(10000, 40));
                            coursesPanel.setMaximumSize(new Dimension(10000, 40));

                            schedulePanel.add(coursesPanel);
                            schedulePanel.add(Box.createVerticalStrut(20));
                        }

                        JLabel scheduleText = new JLabel("My Schedule:");
                        scheduleText.setFont(new Font("Arial", Font.BOLD, 20));
                        scheduleText.setForeground(Color.BLUE);

                        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        textPanel.add(scheduleText);

                        schedulePanel.setPreferredSize(new Dimension(200, 200));
                        schedulePanel.setMaximumSize(new Dimension(200, 200));

                        JScrollPane scroll = new JScrollPane(schedulePanel);
                        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                        setLayout(new BorderLayout());
                        setPreferredSize(new Dimension(200, 200));
                        setMaximumSize(new Dimension(200, 200));

                        // add(Box.createVerticalStrut(50));
                        add(textPanel, BorderLayout.NORTH);
                        add(scroll, BorderLayout.CENTER);
                        refreshPanel();
                    }
                    frame.stopLoading();
                } catch (TimeoutException err) {
                    frame.showTimeoutDialog();
                } catch (Exception err) {
                    err.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An internal error occurred.");
                }
            }
        };
        worker.execute();
        // frame.showLoading();
    }

    @Override
    public void onUnload() {
        removeAll();
    }
}

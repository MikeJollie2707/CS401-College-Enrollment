package client;

import javax.swing.*;

import objects.ClientMsg;
import objects.Section;
import objects.ServerMsg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
                    var resp = get();
                    if (resp.isOk()) {
                        Section[] myCourses = (Section[]) resp.getBody();

                        String[] titles = { "Course Prefix", "Course Number", "Status" };
                        Object[][] data = new Object[myCourses.length][titles.length];

                        for (int i = 0; i < myCourses.length; i++) {
                            Section section = myCourses[i];
                            data[i][0] = section.getCourse().getPrefix();
                            data[i][1] = section.getCourse().getNumber();
                        }
                        JTable scheduleTable = new JTable(data, titles);
                        scheduleTable.setDefaultEditor(Object.class, null);
                        JLabel scheduleText = new JLabel("My Schedule:");
                        scheduleText.setFont(new Font("Arial", Font.BOLD, 20));
                        scheduleText.setForeground(Color.BLUE);

                        JPanel textPanel = new JPanel(new FlowLayout());
                        JPanel tablePanel = new JPanel(new FlowLayout());
                        JScrollPane scroll = new JScrollPane(scheduleTable);
                        textPanel.add(scheduleText);
                        tablePanel.add(scroll);

                        add(textPanel, BorderLayout.NORTH);
                        add(tablePanel, BorderLayout.CENTER);
                        refreshPanel();
                    }
                    frame.stopLoading();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        };
        worker.execute();
        frame.showLoading();
    }

    @Override
    public void onUnload() {
        removeAll();
    }
}

package client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;

import objects.ClientMsg;
import objects.ServerMsg;

public class StudentGUI extends JPanel {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;

    private JButton logoutBtn;
    private CardLayout cl;
    private JPanel optionsPanel;

    public StudentGUI(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream, JButton logoutBtn) {
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;
        this.logoutBtn = logoutBtn;

        cl = new CardLayout();
        optionsPanel = new JPanel(cl);

        setupView();
    }

    void setupView() {
        // JPanel buttonsPanel = new JPanel();
        // buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JPanel panel2 = new JPanel();
        panel2.add(new JLabel("Schedules"));

        FooPanel panel3 = new FooPanel();
        panel3.add(new JLabel("Search"));

        RadioPanel panel = new RadioPanel();
        panel.add("View Schedules", new SchedulePanel(frame, ostream, istream));
        panel.add("View Course", new CatalogPanel(frame, ostream, istream));
        panel.add("Search Courses", panel3);

        JPanel options = panel.getOptions();
        JPanel viewer = panel.buildView();

        this.add(options);
        this.add(viewer);

        this.add(logoutBtn);
    }

}

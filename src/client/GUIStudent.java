package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.*;

public class GUIStudent extends JPanel {
    private JButton logoutBtn;
    private LinkedHashMap<String, PanelBase> panelMap;

    public GUIStudent(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream, JButton logoutBtn) {
        this.logoutBtn = logoutBtn;

        panelMap = new LinkedHashMap<>();
        panelMap.put("View Schedules", new PanelSchedule(frame, ostream, istream));
        panelMap.put("Search Courses", new PanelCatalog(frame, ostream, istream));
        
        setLayout(new BorderLayout());
        setupView();
    }

    void setupView() {
        BuilderRadioPanel radio = new BuilderRadioPanel();
        JPanel sidebar = radio.getOptions();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        
        JLabel topText = new JLabel("(Logged in as student)");
        topText.setFont(new Font("Arial", Font.BOLD, 20));
        topText.setForeground(Color.GRAY);
        sidebar.add(topText);
        sidebar.add(Box.createVerticalStrut(200));
        for (var sceneName : panelMap.sequencedKeySet()) {
            JButton btn = radio.add(sceneName, panelMap.get(sceneName));
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.CYAN);

            sidebar.add(Box.createVerticalStrut(100));
        }
        sidebar.add(logoutBtn);

        JPanel viewer = radio.buildView();

        this.add(sidebar, BorderLayout.WEST);
        this.add(viewer);
    }

}

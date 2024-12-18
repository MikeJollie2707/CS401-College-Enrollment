package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;

import javax.swing.*;

public class GUIStudent extends PanelBase {
    private JButton logoutBtn;
    private LinkedHashMap<String, PanelBase> panelMap;

    public GUIStudent(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream, JButton logoutBtn) {
        this.logoutBtn = logoutBtn;

        panelMap = new LinkedHashMap<>();
        panelMap.put("View Schedules", new PanelSchedule(frame, ostream, istream));
        panelMap.put("Search Courses", new PanelCatalog(frame, ostream, istream));

        setLayout(new BorderLayout());
    }

    void setupView() {
        BuilderRadioPanel radio = new BuilderRadioPanel();
        JPanel sidebar = radio.getOptions();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(163, 228, 215));
        
        JLabel topText = new JLabel("(Logged in as student)");
        topText.setFont(new Font("Arial", Font.BOLD, 20));
        topText.setForeground(Color.GRAY);
        topText.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(topText);
        sidebar.add(Box.createVerticalStrut(200));
        for (var sceneName : panelMap.sequencedKeySet()) {
            JButton btn = radio.add(sceneName, panelMap.get(sceneName));
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.CYAN);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(Box.createVerticalStrut(100));
        }
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, logoutBtn.getPreferredSize().height));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoutBtn);

        JPanel viewer = radio.buildView();
        viewer.setBackground(Color.LIGHT_GRAY);
        JScrollPane viewerScroll = new JScrollPane(viewer);

        this.add(sidebar, BorderLayout.WEST);
        this.add(viewerScroll);
    }

    @Override
    void onLoad() {
        setupView();
        refreshPanel();
    }

    @Override
    void onUnload() {
        removeAll();
    }

}

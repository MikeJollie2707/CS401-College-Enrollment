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

public class GUIAdmin extends PanelBase {
    private JButton logoutBtn;
    private LinkedHashMap<String, PanelBase> panelMap;

    public GUIAdmin(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream, JButton logoutBtn) {
        this.logoutBtn = logoutBtn;

        panelMap = new LinkedHashMap<>();
        panelMap.put("Search Courses", new PanelCatalog(frame, ostream, istream));
        panelMap.put("Create Course", new PanelCreateCourse(frame, ostream, istream));
        panelMap.put("Create Section", new PanelCreateSection(frame, ostream, istream));
        panelMap.put("Create Student", new PanelCreateStudent(frame, ostream, istream));
        panelMap.put("View Report", new PanelReport(frame, ostream, istream));
        setLayout(new BorderLayout());
    }

    void setupView() {
        BuilderRadioPanel radio = new BuilderRadioPanel();
        JPanel sidebar = radio.getOptions();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(242, 190, 182));
        
        JLabel topText = new JLabel("(Logged in as administrator)");
        topText.setFont(new Font("Arial", Font.BOLD, 20));
        topText.setForeground(Color.GRAY);
        topText.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(topText);
        sidebar.add(Box.createVerticalStrut(150));
        for (var sceneName : panelMap.sequencedKeySet()) {
            JButton btn = radio.add(sceneName, panelMap.get(sceneName));
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.RED);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(Box.createVerticalStrut(50));
        }
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, logoutBtn.getPreferredSize().height));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoutBtn);

        JPanel viewer = radio.buildView();
        viewer.setBackground(Color.LIGHT_GRAY);
        JScrollPane viewerScroll = new JScrollPane(viewer);

        this.add(sidebar, BorderLayout.WEST);
        this.add(viewerScroll, BorderLayout.CENTER);
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

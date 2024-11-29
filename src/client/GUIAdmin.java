package client;

import java.awt.BorderLayout;
import java.awt.Color;
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
        setLayout(new BorderLayout());
    }

    void setupView() {
        BuilderRadioPanel radio = new BuilderRadioPanel();
        JPanel sidebar = radio.getOptions();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        
        JLabel topText = new JLabel("(Logged in as administrator)");
        topText.setFont(new Font("Arial", Font.BOLD, 20));
        topText.setForeground(Color.GRAY);
        sidebar.add(topText);
        sidebar.add(Box.createVerticalStrut(150));
        for (var sceneName : panelMap.sequencedKeySet()) {
            JButton btn = radio.add(sceneName, panelMap.get(sceneName));
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.RED);

            sidebar.add(Box.createVerticalStrut(100));
        }
        sidebar.add(logoutBtn);

        JPanel viewer = radio.buildView();

        this.add(sidebar, BorderLayout.WEST);
        this.add(viewer, BorderLayout.CENTER);
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

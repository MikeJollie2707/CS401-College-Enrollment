package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.*;

public class GUIAdmin extends JPanel {
    private JButton logoutBtn;
    private LinkedHashMap<String, PanelBase> panelMap;

    public GUIAdmin(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream, JButton logoutBtn) {
        this.logoutBtn = logoutBtn;

        panelMap = new LinkedHashMap<>();
        panelMap.put("Search Courses", new PanelCatalog(frame, ostream, istream));
        panelMap.put("Create Course", new PanelCreateCourse(frame, ostream, istream));

        setLayout(new BorderLayout());
        setupView();
    }

    void setupView() {
        BuilderRadioPanel radio = new BuilderRadioPanel();
        JPanel options = radio.getOptions();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));

        ArrayList<JButton> buttons = new ArrayList<>();
        for (var sceneName : panelMap.sequencedKeySet()) {
            buttons.add(radio.add(sceneName, panelMap.get(sceneName)));
            options.add(Box.createVerticalStrut(10));
        }
        options.add(logoutBtn);

        for (var btn : buttons) {
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.CYAN);
        }

        JPanel viewer = radio.buildView();

        this.add(options, BorderLayout.WEST);
        this.add(viewer);
    }

}

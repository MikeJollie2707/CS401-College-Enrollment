package client;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

import javax.swing.*;

public class RadioPanel {
    private JPanel options;
    private JPanel currentPanel;
    private CardLayout cards;
    private LinkedHashMap<String, RAIIPanel> panelMap;

    public RadioPanel() {
        options = new JPanel();
        cards = new CardLayout();
        currentPanel = new JPanel(cards);
        panelMap = new LinkedHashMap<>();
    }

    public JButton add(String label, RAIIPanel panel) {
        JButton btn = new JButton(label);
        panelMap.put(label, panel);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cards.show(currentPanel, label);
            }
        });
        options.add(btn);
        return btn;
    }

    public JPanel getOptions() {
        return options;
    }

    public JPanel buildView() {
        for (var key : panelMap.keySet()) {
            currentPanel.add(panelMap.get(key), key);
        }
        return currentPanel;
    }
}

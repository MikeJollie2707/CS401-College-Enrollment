package client;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

import javax.swing.*;

/**
 * Build radio-like panels. (See HTML radio input type).
 * <p>
 * One panel will contains button, the other panel will render what is wanted
 * by each button.
 */
public class BuilderRadioPanel {
    private JPanel options;
    private JPanel currentPanel;
    private CardLayout cards;
    private LinkedHashMap<String, PanelBase> panelMap;

    public BuilderRadioPanel() {
        options = new JPanel();
        cards = new CardLayout();
        currentPanel = new JPanel(cards);
        panelMap = new LinkedHashMap<>();
    }

    /**
     * Add a panel to this radio panel.
     * 
     * @param sceneName The string that will be used to identify this panel.
     * @param panel     The panel to render.
     * @return The button that, once pressed, will render {@code panel}. Feel free
     *         to style it.
     */
    public JButton add(String sceneName, PanelBase panel) {
        JButton btn = new JButton(sceneName);
        panelMap.put(sceneName, panel);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                show(sceneName);
            }
        });
        options.add(btn);
        return btn;
    }

    /**
     * Forcefully render the panel identified by this string.
     * 
     * @param sceneName The string that identifies the panel to be rendered.
     */
    public void show(String sceneName) {
        cards.show(currentPanel, sceneName);
    }

    /**
     * Return the panel of buttons.
     * 
     * @return
     */
    public JPanel getOptions() {
        return options;
    }

    /**
     * Return the panel that will response to the radio buttons.
     * <p>
     * It is used as a container to render actual panels, so feel free to style it,
     * but don't add content to it.
     * 
     * @return
     */
    public JPanel buildView() {
        for (var key : panelMap.sequencedKeySet()) {
            currentPanel.add(panelMap.get(key), key);
        }
        return currentPanel;
    }
}

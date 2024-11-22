package client;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

/**
 * The base class for all {@code JPanel} that has the following characteristics:
 * - It execute some arbitrary code upon being shown.
 * - It cleans up itself upon being hidden (ie. clear all components).
 */
abstract class RAIIPanel extends JPanel {
    RAIIPanel() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                onLoad();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                onUnload();
            }
        });
    }
    
    abstract void onLoad();

    abstract void onUnload();

    /**
     * A braindead way to make sure changes to the panel gets updated.
     */
    protected void refreshPanel() {
        revalidate();
        repaint();
    }
}

package client;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

/**
 * The base class for all {@code JPanel} that has the following characteristics:
 * <ul>
 * <li>It execute some arbitrary code upon being shown, NOT upon
 * construction.</li>
 * <li>It cleans up itself upon being hidden (ie. clear all components).</li>
 * </ul>
 */
abstract class PanelBase extends JPanel {
    PanelBase() {
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

    /**
     * This method will call when {@code componentShown()} is activated (ie. when it
     * is rendered).
     * <p>
     * If the panel has its own JComponent, make sure to add them here.
     */
    abstract void onLoad();

    /**
     * This method will call when {@code componentHidden()} is activated.
     * <p>
     * Usually this will just be {@code this.removeAll()}. However, if this panel
     * contains other {@code JPanel}, you often need to clean up these
     * {@code JPanel} as well.
     */
    abstract void onUnload();

    /**
     * A way to make sure changes to the panel gets updated.
     * <p>
     * This only applies to THIS panel. If it contains any children panels,
     * you need to refresh them manually.
     */
    protected void refreshPanel() {
        revalidate();
        repaint();
    }
}

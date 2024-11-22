package client;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

public class FooPanel extends RAIIPanel {

    public FooPanel() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // System.out.println("Shown");
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // System.out.println("Hidden");
            }
        });
    }

    @Override
    public void onLoad() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUnload() {
        // TODO Auto-generated method stub

    }
}

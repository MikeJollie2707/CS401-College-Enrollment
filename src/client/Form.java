package client;

import java.util.ArrayList;
import java.util.function.Supplier;

import javax.swing.*;

public class Form {
    private JPanel form;
    private ArrayList<JComponent> inputs;
    private ArrayList<Supplier<String>> getters;

    public Form(JPanel existingPanel) {
        if (existingPanel != null) {
            form = existingPanel;
        } else {
            form = new JPanel();
        }

        inputs = new ArrayList<>();
        getters = new ArrayList<>();
    }

    public void removeAll() {
        form.removeAll();
    }

    public void clearAll() {
        for (var component: inputs) {
        }
    }

    public Form addEntry(JLabel label, JComponent input, Supplier<String> getter) {
        // Null check.

        form.add(label);
        form.add(input);

        inputs.add(input);
        getters.add(getter);

        return this;
    }

    public ArrayList<String> getResults() {
        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i < getters.size(); ++i) {
            results.add(getters.get(i).get());
        }
        return results;
    }

    public JPanel getPanel() {
        return form;
    }
}

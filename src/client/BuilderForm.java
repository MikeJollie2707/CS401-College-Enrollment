package client;

import java.util.ArrayList;
import java.util.function.Supplier;

import javax.swing.*;

/**
 * A utility class that helps dealing with forms.
 */
public class BuilderForm {
    private JPanel form;
    private ArrayList<JComponent> inputs;
    private ArrayList<Supplier<String>> getters;

    /**
     * Construct a form.
     * 
     * @param existingPanel If null, use a new {@code JPanel}, otherwise, the form
     *                      will "mount" on the provided panel. Thus, unless you're
     *                      sure what you're doing, don't use a panel that already
     *                      has stuff on it.
     */
    public BuilderForm(JPanel existingPanel) {
        if (existingPanel != null) {
            form = existingPanel;
        } else {
            form = new JPanel();
        }

        inputs = new ArrayList<>();
        getters = new ArrayList<>();
    }

    /**
     * Clear the form entirely. You'll need to re-add the components again.
     */
    public void removeAll() {
        form.removeAll();
        inputs.clear();
        getters.clear();
    }

    /**
     * Add an entry to the form.
     * <p>
     * It doesn't really matter what {@code input} is, as long as you have a way to
     * return a result from it.
     * Provide the way to return a result from it via {@code getter}.
     * <ul>
     * <li>Example 1: If {@code input} is a {@code JTextField}, you can pass
     * {@code () -> input.getText()} as the {@code getter}.
     * <li>Example 2: If {@code input} is a {@code JPanel} containing many
     * {@code JTextField}, you can pass a function that iterate through all text
     * fields and return a combined result.
     * 
     * @param label  The label for the input.
     * @param input  Any component to render. See {@code getter} attribute for more
     *               info.
     * @param getter A function that must returns a {@code String}.
     * @return {@code this}
     */
    public BuilderForm addEntry(JLabel label, JComponent input, Supplier<String> getter) {
        form.add(label);
        form.add(input);

        inputs.add(input);
        getters.add(getter);

        return this;
    }

    /**
     * Return the current values of this form.
     * 
     * @return An array containing strings in the insertion order of
     *         {@code addEntry()}.
     */
    public ArrayList<String> getResults() {
        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i < getters.size(); ++i) {
            results.add(getters.get(i).get());
        }
        return results;
    }

    /**
     * Get the internal panel to style whatever.
     * 
     * @return
     */
    public JPanel getPanel() {
        return form;
    }
}

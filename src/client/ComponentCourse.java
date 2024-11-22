package client;

import javax.swing.JLabel;
import javax.swing.JPanel;

import objects.Course;

/**
 * A component that renders a {@code Course}
 */
public class ComponentCourse {
    private Course course;

    public ComponentCourse(Course course) {
        this.course = course;
    }

    public JPanel build() {
        JPanel panel = new JPanel();
        String prefix = course.getPrefix();
        String number = course.getNumber();
        String desc = course.getDescription();

        panel.add(new JLabel(prefix));
        panel.add(new JLabel(number));
        panel.add(new JLabel(desc));

        return panel;
    }
}

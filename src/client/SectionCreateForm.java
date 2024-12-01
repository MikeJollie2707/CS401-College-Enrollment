/**
 * This is copied from SectionEditForm with hasty modifications to make it work.
 * A better way to not repeat these very similar code is ideal, but we don't have
 * time, so it is what it is.
 */

package client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.*;

import objects.*;

/**
 * A wrapper around a {@code BuilderForm} used to create a section.
 * <p>
 * To render, use {@code getPanel()}.
 */
public class SectionCreateForm {
    private BuilderForm form;
    private Course course;
    private JPanel schedulePanel;
    private String[] DAYOFWEEKSTRINGS;

    // This should be grouped into a class but I can't be bothered to get a new file
    // for this...
    private ArrayList<JComboBox<String>> day;
    private ArrayList<JTextField> fromHour;
    private ArrayList<JTextField> fromMinute;
    private ArrayList<JTextField> toHour;
    private ArrayList<JTextField> toMinute;
    private ArrayList<JTextField> locations;
    private ArrayList<JCheckBox> syncs;
    private ArrayList<JPanel> entryPanels;

    /**
     * Create a form to create a section.
     * 
     */
    public SectionCreateForm() {
        this.course = null;
        day = new ArrayList<>();
        fromHour = new ArrayList<>();
        fromMinute = new ArrayList<>();
        toHour = new ArrayList<>();
        toMinute = new ArrayList<>();
        entryPanels = new ArrayList<>();
        locations = new ArrayList<>();
        syncs = new ArrayList<>();
        DAYOFWEEKSTRINGS = Arrays.stream(DayOfWeek.values())
                .map(d -> d.getDisplayName(TextStyle.FULL, Locale.US).toUpperCase())
                .collect(Collectors.toList())
                .toArray(new String[0]);

        form = new BuilderForm(null);

        schedulePanel = new JPanel();
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
        JButton newEntry = new JButton("+");
        newEntry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (entryPanels.size() < 7) {
                    JPanel entryPanel = createScheduleEntryPanel(
                            new ScheduleEntry("", true, DayOfWeek.MONDAY, OffsetTime.MIN, OffsetTime.MAX));
                    entryPanels.add(entryPanel);
                    schedulePanel.add(entryPanel);
                    schedulePanel.revalidate();
                    schedulePanel.repaint();
                }
            }
        });
        schedulePanel.add(newEntry);
        JButton rmEntry = new JButton("-");
        rmEntry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (entryPanels.size() > 1) {
                    schedulePanel.remove(entryPanels.getLast());
                    day.removeLast();
                    fromHour.removeLast();
                    fromMinute.removeLast();
                    toHour.removeLast();
                    toMinute.removeLast();
                    locations.removeLast();
                    syncs.removeLast();
                    entryPanels.removeLast();
                    schedulePanel.revalidate();
                    schedulePanel.repaint();
                }
            }
        });
        schedulePanel.add(rmEntry);

        int capacity = 35;
        int waitlist = 5;
        String instructorName = "";

        JPanel entryPanel = createScheduleEntryPanel(
                new ScheduleEntry("", true, DayOfWeek.MONDAY, OffsetTime.MIN, OffsetTime.MAX));
        entryPanels.add(entryPanel);
        schedulePanel.add(entryPanel);

        JTextField sectionNumber = new JTextField();
        JTextField sectionCapacity = new JTextField(String.valueOf(capacity));
        JTextField sectionWaitlist = new JTextField(String.valueOf(waitlist));
        JTextField sectionInstructor = new JTextField(instructorName);

        form.addEntry(new JLabel("Section number:"), sectionNumber, () -> sectionNumber.getText());
        form.addEntry(new JLabel("Max capacity:"), sectionCapacity, () -> sectionCapacity.getText());
        form.addEntry(new JLabel("Max waitlist size:"), sectionWaitlist, () -> sectionWaitlist.getText());
        form.addEntry(new JLabel("Instructor:"), sectionInstructor, () -> sectionInstructor.getText());
        form.addEntry(new JLabel("Schedule:"), schedulePanel, () -> {
            ArrayList<String> items = new ArrayList<>();
            for (int i = 0; i < day.size(); ++i) {
                String dayOfWeek = (String) day.get(i).getSelectedItem();
                String fromH = fromHour.get(i).getText();
                String fromM = fromMinute.get(i).getText();
                String toH = toHour.get(i).getText();
                String toM = toMinute.get(i).getText();

                String location = locations.get(i).getText();
                boolean isSync = syncs.get(i).isSelected();
                items.add(String.format("%s,%s,%s,%s,%s,%s,%s", dayOfWeek, fromH, fromM, toH, toM, location,
                        isSync ? "s" : "a"));
            }
            return String.join(" ", items);
        });

        form.getPanel().setLayout(new BoxLayout(form.getPanel(), BoxLayout.Y_AXIS));
    }

    private JPanel createScheduleEntryPanel(ScheduleEntry entry) {
        JPanel entryPanel = new JPanel();
        JComboBox<String> dayofweek = new JComboBox<>(DAYOFWEEKSTRINGS);
        dayofweek.setSelectedItem(entry.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US).toUpperCase());
        day.addLast(dayofweek);

        var time = entry.getTime();
        JTextField fromHourField = new JTextField(String.valueOf(time.getStart().getHour()));
        fromHourField.setColumns(5);
        JTextField fromMinuteField = new JTextField(String.valueOf(time.getStart().getMinute()));
        fromMinuteField.setColumns(5);
        JTextField toHourField = new JTextField(String.valueOf(time.getEnd().getHour()));
        toHourField.setColumns(5);
        JTextField toMinuteField = new JTextField(String.valueOf(time.getEnd().getMinute()));
        toMinuteField.setColumns(5);

        fromHour.add(fromHourField);
        fromMinute.add(fromMinuteField);
        toHour.add(toHourField);
        toMinute.add(toMinuteField);

        JTextField locationField = new JTextField(entry.getLocation());
        locationField.setColumns(10);
        JCheckBox isSyncField = new JCheckBox("Is sync", entry.isSync());

        locations.add(locationField);
        syncs.add(isSyncField);

        entryPanel.add(dayofweek);
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setAlignmentY(0);

        JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        startTimePanel.add(new JLabel("Start:"));
        startTimePanel.add(fromHourField);
        startTimePanel.add(new JLabel(":"));
        startTimePanel.add(fromMinuteField);
        JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        endTimePanel.add(new JLabel("End:"));
        endTimePanel.add(toHourField);
        endTimePanel.add(new JLabel(":"));
        endTimePanel.add(toMinuteField);
        timePanel.add(startTimePanel);
        timePanel.add(endTimePanel);
        entryPanel.add(timePanel);

        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
        locationPanel.add(new JLabel("Location:"));
        locationPanel.add(locationField);
        entryPanel.add(locationPanel);
        
        entryPanel.add(isSyncField);
        return entryPanel;
    }

    public JPanel getPanel() {
        return form.getPanel();
    }

    /**
     * Get a {@code Section} that is meant to be created from this form.
     * 
     * @param course The course for this section.
     * 
     * @return
     * @throws NumberFormatException If one of the entry expects a number but a
     *                               string is provided.
     * @throws DateTimeException     If one of the date entry is out of bound.
     */
    public Section getEditedSection(Course course) {
        var results = form.getResults();
        String rawNumber = results.get(0);
        String rawCapacity = results.get(1);
        String rawWaitlist = results.get(2);
        String rawInstructor = results.get(3);
        String rawSchedule = results.get(4);

        int capacity = Integer.valueOf(rawCapacity);
        int waitlist = Integer.valueOf(rawWaitlist);

        // Process schedule shenanigans
        ArrayList<ScheduleEntry> entries = new ArrayList<>();
        String[] parts = rawSchedule.split(" ");
        for (var part : parts) {
            String[] p = part.split(",");
            String rawDayOfWeek = p[0];
            String rawFromH = p[1];
            String rawFromM = p[2];
            String rawToH = p[3];
            String rawToM = p[4];
            String location = p[5];
            String rawSync = p[6];

            DayOfWeek dayOfWeek = DayOfWeek.valueOf(rawDayOfWeek);
            int fromH = Integer.valueOf(rawFromH);
            int fromM = Integer.valueOf(rawFromM);
            int toH = Integer.valueOf(rawToH);
            int toM = Integer.valueOf(rawToM);
            boolean isSync = rawSync.equals("s");

            var now = OffsetTime.now(ZoneId.of("America/Los_Angeles"));
            OffsetTime start = OffsetTime.of(fromH, fromM, 0, 0, now.getOffset());
            OffsetTime end = OffsetTime.of(toH, toM, 0, 0, now.getOffset());
            entries.add(new ScheduleEntry(location, isSync, dayOfWeek, start, end));
        }

        Instructor instructor = new Instructor(rawInstructor, null);

        Section newSection = new Section(course, rawNumber, capacity, waitlist, instructor);
        newSection.setSchedule(entries.toArray(new ScheduleEntry[0]));
        return newSection;
    }

}

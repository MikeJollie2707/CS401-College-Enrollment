package client;

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
 * A wrapper around a {@code BuilderForm} used to edit a section.
 * <p>
 * To render, use {@code getPanel()}.
 */
public class SectionEditForm {
    private BuilderForm form;
    private Section section;
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
     * Create a form to edit an existing section.
     * 
     * @param section An existing section.
     */
    public SectionEditForm(Section section) {
        this.section = section;
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
        form.getPanel().add(new JLabel(
                "Warning: This may cause some students to get dropped from this section or cause conflicts to their schedule."));

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

        capacity = section.getMaxCapacity();
        waitlist = section.getMaxWaitlistSize();
        instructorName = section.getInstructor().getName();

        for (var schedule : section.getSchedule()) {
            JPanel entryPanel = createScheduleEntryPanel(schedule);
            entryPanels.add(entryPanel);
            schedulePanel.add(entryPanel);
        }

        JCheckBox isCompleted = new JCheckBox();
        isCompleted.setSelected(section.getStatus() == SectionStatus.COMPLETED);
        JTextField sectionCapacity = new JTextField(String.valueOf(capacity));
        JTextField sectionWaitlist = new JTextField(String.valueOf(waitlist));
        JTextField sectionInstructor = new JTextField(instructorName);

        // TODO: Shorten this sentence somehow.
        form.addEntry(new JLabel("Tick this to mark the section as completed and will count towards prerequisites:"),
                isCompleted, () -> isCompleted.isSelected() ? "t" : "f");
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
            return String.join("\n", items);
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
        entryPanel.add(fromHourField);
        entryPanel.add(fromMinuteField);
        entryPanel.add(toHourField);
        entryPanel.add(toMinuteField);
        entryPanel.add(locationField);
        entryPanel.add(isSyncField);
        return entryPanel;
    }

    public JPanel getPanel() {
        return form.getPanel();
    }

    /**
     * Get a {@code Section} that is meant to be created from this form.
     * 
     * @return
     * @throws NumberFormatException If one of the entry expects a number but a
     *                               string is provided.
     * @throws DateTimeException     If one of the date entry is out of bound.
     */
    public Section getEditedSection() {
        var results = form.getResults();
        String rawIsCompleted = results.get(0);
        String rawCapacity = results.get(1);
        String rawWaitlist = results.get(2);
        String rawInstructor = results.get(3);
        String rawSchedule = results.get(4);

        int capacity = Integer.valueOf(rawCapacity);
        int waitlist = Integer.valueOf(rawWaitlist);

        // Process schedule shenanigans
        ArrayList<ScheduleEntry> entries = new ArrayList<>();
        String[] parts = rawSchedule.split("\n");
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

        Section newSection = new Section(section.getCourse(), section.getNumber(), capacity, waitlist, instructor);
        newSection.setSchedule(entries.toArray(new ScheduleEntry[0]));
        if (rawIsCompleted.equals("t")) {
            newSection.setStatus(SectionStatus.COMPLETED);
        }
        return newSection;
    }

}

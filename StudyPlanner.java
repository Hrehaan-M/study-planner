
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class StudyPlanner extends JFrame {

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> dues = new ArrayList<>();
    ArrayList<Integer> hours = new ArrayList<>();

    JTextField nameField = new JTextField(15);
    JTextField dueField = new JTextField(10);
    JTextField hoursField = new JTextField(5);
    JTextArea output = new JTextArea(20, 50);
    DefaultListModel<String> listModel = new DefaultListModel<>();
    JList<String> assignmentList = new JList<>(listModel);

    public StudyPlanner() {
        setTitle("Study Planner");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        add(new JLabel("Assignment:"));
        add(nameField);
        add(new JLabel("Due (YYYY-MM-DD):"));
        add(dueField);
        add(new JLabel("Hours needed:"));
        add(hoursField);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addAssignment());
        add(addBtn);

        JButton removeBtn = new JButton("Remove selected");
        removeBtn.addActionListener(e -> removeAssignment());
        add(removeBtn);

        add(new JScrollPane(assignmentList));

        JButton genBtn = new JButton("Generate schedule");
        genBtn.addActionListener(e -> generateSchedule());
        add(genBtn);

        output.setEditable(false);
        add(new JScrollPane(output));

        pack();
        setVisible(true);
    }

    void addAssignment() {
        String name = nameField.getText().trim();
        String due = dueField.getText().trim();
        String hoursText = hoursField.getText().trim();

        if (name.isEmpty() || due.isEmpty() || hoursText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill in all fields.");
            return;
        }

        int h;
        try { h = Integer.parseInt(hoursText); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hours must be a number."); return; }

        names.add(name);
        dues.add(due);
        hours.add(h);
        listModel.addElement(name + " | due " + due + " | " + h + "h");

        nameField.setText("");
        dueField.setText("");
        hoursField.setText("");
    }

    void removeAssignment() {
        int i = assignmentList.getSelectedIndex();
        if (i < 0) { JOptionPane.showMessageDialog(this, "Select an assignment first."); return; }
        names.remove(i);
        dues.remove(i);
        hours.remove(i);
        listModel.remove(i);
    }

    void generateSchedule() {
        if (names.isEmpty()) {
            output.setText("No assignments added.");
            return;
        }
        
        Integer[] order = new Integer[names.size()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> dues.get(a).compareTo(dues.get(b)));

        int hoursPerDay = 2;
        StringBuilder sb = new StringBuilder();
        sb.append("=== SCHEDULE ===\n\n");

        int day = 1;
        int minutesUsed = 0;
        sb.append("Day ").append(day).append(":\n");

        for (int idx : order) {
            int remaining = hours.get(idx) * 60;
            String name = names.get(idx);
            String due = dues.get(idx);

            while (remaining > 0) {
                int space = hoursPerDay * 60 - minutesUsed;
                if (space == 0) {
                    day++;
                    minutesUsed = 0;
                    sb.append("\nDay ").append(day).append(":\n");
                    space = hoursPerDay * 60;
                }
                int chunk = Math.min(remaining, space);
                sb.append("  ").append(name).append(" (").append(due).append(") — ")
                        .append(chunk).append(" min\n");
                minutesUsed += chunk;
                remaining -= chunk;
            }
        }

        sb.append("\nTotal days: ").append(day);
        output.setText(sb.toString());
    }

    public static void main(String[] args) {
        new StudyPlanner();
    }
}

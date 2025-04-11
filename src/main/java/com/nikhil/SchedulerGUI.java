package com.nikhil;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

@SuppressWarnings("serial")
public class SchedulerGUI extends JFrame {
    private final TaskScheduler scheduler;
    private final EmailService emailService;
    private final String senderEmail;
    private final String recipientEmail;
    private JTextField taskNameField;
    private JSpinner durationSpinner;
    private JSpinner earningsSpinner;
    private JTextField deadlineField;
    private JTextArea scheduleArea;


    private final int FONT_SIZE = 14;
    private final Font LABEL_FONT = new Font("Arial", Font.BOLD, FONT_SIZE);
    private final Font INPUT_FONT = new Font("Arial", Font.PLAIN, FONT_SIZE);
    private final Queue<Task> taskQueue = new LinkedList<>();

    public SchedulerGUI(String senderEmail, String emailPassword, String recipientEmail) {
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.emailService = new EmailService(senderEmail, emailPassword);
        this.scheduler = new TaskScheduler(emailService);

        setupGUI();
    }

    private void setupGUI() {
        setTitle("Task Scheduler with Earnings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 900));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Task Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Task Name:");
        nameLabel.setFont(LABEL_FONT);
        inputPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        taskNameField = new JTextField(20);
        taskNameField.setFont(INPUT_FONT);
        inputPanel.add(taskNameField, gbc);

        // Duration
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel durationLabel = new JLabel("Duration (minutes):");
        durationLabel.setFont(LABEL_FONT);
        inputPanel.add(durationLabel, gbc);
        gbc.gridx = 1;
        durationSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 480, 1));
        durationSpinner.setFont(INPUT_FONT);
        ((JSpinner.DefaultEditor)durationSpinner.getEditor()).getTextField().setFont(INPUT_FONT);
        inputPanel.add(durationSpinner, gbc);

        // Earnings
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel earningsLabel = new JLabel("Earnings (₹/min):");
        earningsLabel.setFont(LABEL_FONT);
        inputPanel.add(earningsLabel, gbc);
        gbc.gridx = 1;
        earningsSpinner = new JSpinner(new SpinnerNumberModel(10.0, 0.1, 1000.0, 0.1));
        earningsSpinner.setFont(INPUT_FONT);
        ((JSpinner.DefaultEditor)earningsSpinner.getEditor()).getTextField().setFont(INPUT_FONT);
        inputPanel.add(earningsSpinner, gbc);

        // Deadline
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel deadlineLabel = new JLabel("Deadline (yyyy-MM-dd HH:mm):");
        deadlineLabel.setFont(LABEL_FONT);
        inputPanel.add(deadlineLabel, gbc);
        gbc.gridx = 1;
        deadlineField = new JTextField(20);
        deadlineField.setFont(INPUT_FONT);
        deadlineField.setText(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        inputPanel.add(deadlineField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton addButton = new JButton("Add Task");
        addButton.setFont(LABEL_FONT);
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addTask());
        buttonPanel.add(addButton);

        JButton scheduleButton = new JButton("Schedule All Tasks");
        scheduleButton.setFont(LABEL_FONT);
        scheduleButton.setBackground(new Color(34, 139, 34));
        scheduleButton.setForeground(Color.WHITE);
        scheduleButton.setFocusPainted(false);
        scheduleButton.addActionListener(e -> scheduleQueuedTasks());
        buttonPanel.add(scheduleButton);

        inputPanel.add(buttonPanel, gbc);

        // Algorithm Choice


        scheduleArea = new JTextArea(15, 40);
        scheduleArea.setFont(new Font("Monospaced", Font.PLAIN, FONT_SIZE));
        scheduleArea.setEditable(false);
        scheduleArea.setMargin(new Insets(10, 10, 10, 10));

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(scheduleArea), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public void addTask() {
        try {
            String name = taskNameField.getText();
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Task name cannot be empty");
            }

            int duration = (Integer) durationSpinner.getValue();
            double earnings = (Double) earningsSpinner.getValue();
            LocalDateTime deadline = LocalDateTime.parse(
                deadlineField.getText(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );

            Task task = new Task(name, "", 1, duration, earnings, deadline);
            taskQueue.add(task);
            updateScheduleDisplay();

            taskNameField.setText("");
            durationSpinner.setValue(30);
            earningsSpinner.setValue(10.0);
            deadlineField.setText(LocalDateTime.now().plusHours(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void scheduleQueuedTasks() {
        if (taskQueue.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No tasks in the queue to schedule!",
                    "Empty Queue",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Task> scheduledTasks;
        scheduledTasks = SchedulingAlgorithm.knapsackScheduling(
            new ArrayList<>(taskQueue),
            SchedulingAlgorithm.WORKDAY_MINUTES
        );

        taskQueue.clear();
        int totalTasks = scheduledTasks.size();

        // Immediately add tasks to the scheduler and send notifications
        for (Task task : scheduledTasks) {
            scheduler.addTask(task, recipientEmail); // This will trigger email notification
        }

        int totalDuration = scheduledTasks.stream().mapToInt(Task::getDuration).sum();

        JOptionPane.showMessageDialog(this,
                String.format("%d tasks have been scheduled using Knapsack algorithm.\n" +
                        "Total work duration: %d minutes\n" +
                        "Total potential earnings: ₹%.2f\n\n" +
                        "Email notifications have been sent for all tasks.",
                        totalTasks,
                        totalDuration,
                        scheduledTasks.stream().mapToDouble(Task::getTotalOutput).sum()),
                "Scheduling Started",
                JOptionPane.INFORMATION_MESSAGE);

        updateScheduleDisplay();
    }

    private void updateScheduleDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("Queued Tasks:\n\n");

        if (taskQueue.isEmpty()) {
            sb.append("No tasks in queue\n");
        } else {
            int totalDuration = 0;
            double totalEarnings = 0;

            for (Task task : taskQueue) {
                sb.append(String.format(
                        "Task: %s\n" +
                                "Duration: %d minutes\n" +
                                "Earnings: ₹%.2f\n\n",
                        task.getName(),
                        task.getDuration(),
                        task.getTotalOutput()));

                totalDuration += task.getDuration();
                totalEarnings += task.getTotalOutput();
            }

            sb.append(String.format(
                    "\nSummary:\n" +
                            "Total Tasks: %d\n" +
                            "Total Duration: %d minutes\n" +
                            "Total Potential Earnings: ₹%.2f",
                    taskQueue.size(),
                    totalDuration,
                    totalEarnings));
        }

        scheduleArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        String senderEmail = "nikhilrakeshg@gmail.com";
        String emailPassword = "fvla dmpc wkvu dgas";
        String recipientEmail = "denimworld12@gmail.com";

        SwingUtilities.invokeLater(() -> {
            SchedulerGUI gui = new SchedulerGUI(senderEmail, emailPassword, recipientEmail);
            gui.setVisible(true);
        });
    }
}

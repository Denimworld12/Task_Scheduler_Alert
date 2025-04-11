package com.nikhil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.MessagingException;

public class TaskScheduler {
    private final List<Task> tasks;
    private EmailService emailService;

    public TaskScheduler(EmailService emailService) {
        this.tasks = new ArrayList<>();
        this.emailService = emailService;
    }

    public TaskScheduler() {
        this.tasks = new ArrayList<>();
        this.emailService = null;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void addTask(Task task, String userEmail) {
        tasks.add(task);
        if (emailService != null && !task.isCompleted()) {
            sendTaskNotification(task, userEmail);
        }
    }

    private void sendTaskNotification(Task task, String userEmail) {
        try {
            List<Task> upcomingTasks = getUpcomingTasks(LocalDateTime.now());
            int taskOrder = upcomingTasks.indexOf(task) + 1;
            int totalTasks = upcomingTasks.size();

            String subject = String.format("It's Time to Start Your Task: %s 💪", task.getName());
            String content = String.format(
                "Hey there!\n\n" +
                "Your task \"%s\" is ready to start.\n" +
                "Duration: %d minutes\n" +
                "This is your %s priority task among %d tasks added.\n\n" +
                "Stay focused and knock it out! 💪\n" +
                "You earn ₹%.2f per minute — that's ₹%.2f total!\n\n" +
                "Keep pushing forward — success is built one task at a time! 🌟\n\n" +
                "— Your Task Scheduler",
                task.getName(),
                task.getDuration(),
                ordinal(taskOrder),
                totalTasks,
                task.getEarningsPerMinute(),
                task.getTotalOutput()
            );

            emailService.sendScheduleUpdate(userEmail, subject, content);
        } catch (MessagingException e) {
            System.err.println("Failed to send notification for task: " + task.getName());
            System.err.println("Error: " + e.getMessage());
            // Consider showing an error dialog to the user
            // We don't re-throw as this is a notification failure, not a critical error
        }
    }

    private String ordinal(int i) {
        if (i % 100 >= 11 && i % 100 <= 13) return i + "th";
        int last = i % 10;
        switch (last) {
            case 1: return i + "st";
            case 2: return i + "nd";
            case 3: return i + "rd";
            default: return i + "th";
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public List<Task> getUpcomingTasks(LocalDateTime now) {
        return tasks.stream()
            .filter(task -> !task.isCompleted())
            .sorted()
            .collect(Collectors.toList());
    }

    public void markTaskAsCompleted(Task task, String userEmail) {
        if (tasks.contains(task) && !task.isCompleted()) {
            task.setCompleted(true);
            List<Task> upcomingTasks = getUpcomingTasks(LocalDateTime.now());
            if (!upcomingTasks.isEmpty() && emailService != null) {
                Task nextTask = upcomingTasks.get(0);
                sendTaskNotification(nextTask, userEmail);
            }
        }
    }

    public String getScheduleSummary() {
        if (tasks.isEmpty()) return "No tasks scheduled.";

        StringBuilder summary = new StringBuilder("Task Schedule Summary:\n\n");
        List<Task> sortedTasks = getUpcomingTasks(LocalDateTime.now());
        int totalTasks = sortedTasks.size();

        for (int i = 0; i < sortedTasks.size(); i++) {
            Task task = sortedTasks.get(i);
            summary.append(String.format("%d. %s\n", i + 1, task.toString()));
            summary.append("----------------------------------------\n");
        }

        double totalEarnings = sortedTasks.stream().mapToDouble(Task::getTotalOutput).sum();
        int totalDuration = sortedTasks.stream().mapToInt(Task::getDuration).sum();

        summary.append(String.format("\nTotal Tasks: %d\n", totalTasks));
        summary.append(String.format("Total Duration: %d minutes\n", totalDuration));
        summary.append(String.format("Total Potential Earnings: ₹%.2f\n", totalEarnings));

        return summary.toString();
    }
}

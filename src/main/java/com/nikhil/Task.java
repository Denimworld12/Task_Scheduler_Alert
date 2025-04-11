package com.nikhil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Comparable<Task> {
    private String name;
    private String description;
    private int priority;
    private int duration; // in minutes
    private double earningsPerMinute;
    private LocalDateTime deadline;
    private boolean completed;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Task(String name, String description, int priority, int duration, double earningsPerMinute, LocalDateTime deadline) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.duration = duration;
        this.earningsPerMinute = earningsPerMinute;
        this.deadline = deadline;
        this.completed = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPriority() { return priority; }
    public int getDuration() { return duration; }
    public LocalDateTime getDeadline() { return deadline; }
    public boolean isCompleted() { return completed; }
    public double getEarningsPerMinute() { return earningsPerMinute; }
    public double getTotalOutput() { return duration * earningsPerMinute; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setEarningsPerMinute(double earningsPerMinute) { this.earningsPerMinute = earningsPerMinute; }

    @Override
    public int compareTo(Task other) {
        int outputCompare = Double.compare(other.getTotalOutput(), this.getTotalOutput());
        if (outputCompare != 0) return outputCompare;

        int priorityCompare = Integer.compare(other.priority, this.priority);
        if (priorityCompare != 0) return priorityCompare;

        return this.deadline.compareTo(other.deadline);
    }

    @Override
    public String toString() {
        return String.format("Task: %s\n" +
                           "Priority: %d\n" +
                           "Duration: %d minutes\n" +
                           "Rate: ₹%.2f/min\n" +
                           "Total Earnings: ₹%.2f\n" +
                           "Deadline: %s\n" +
                           "Status: %s\n",
            name, priority, duration, earningsPerMinute, getTotalOutput(),
            deadline.format(DATE_FORMATTER),
            completed ? "Completed" : "Pending");
    }
}

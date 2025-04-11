package com.nikhil;

import java.util.List;
import java.util.ArrayList;

public class SchedulingAlgorithm {
    public static final int WORKDAY_MINUTES = 480; // 8 hours

    public static List<Task> knapsackScheduling(List<Task> tasks, int maxTime) {
        int n = tasks.size();
        double[][] dp = new double[n + 1][maxTime + 1];
        boolean[][] selected = new boolean[n + 1][maxTime + 1];

        // Fill the dp table
        for (int i = 1; i <= n; i++) {
            Task task = tasks.get(i - 1);
            for (int w = 0; w <= maxTime; w++) {
                if (task.getDuration() <= w) {
                    double include = task.getTotalOutput() + dp[i - 1][w - task.getDuration()];
                    double exclude = dp[i - 1][w];
                    if (include > exclude) {
                        dp[i][w] = include;
                        selected[i][w] = true;
                    } else {
                        dp[i][w] = exclude;
                    }
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Reconstruct solution
        List<Task> selectedTasks = new ArrayList<>();
        int w = maxTime;
        for (int i = n; i > 0; i--) {
            if (selected[i][w]) {
                selectedTasks.add(tasks.get(i - 1));
                w -= tasks.get(i - 1).getDuration();
            }
        }
        return selectedTasks;
    }


}

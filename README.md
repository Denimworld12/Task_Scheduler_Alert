Task Scheduling System with Email Notifications
=============================================

I. Problem Statement
-------------------
Design and implement an automated task scheduling system that:
1. Allows users to input tasks with:
   - Task name
   - Duration (in minutes)
   - Earnings per minute
   - Deadline
2. Optimally schedules tasks to maximize total earnings within an 8-hour workday
3. Sends email notifications for scheduled tasks
4. Provides a user-friendly GUI interface

II. Theory
----------
A. Problem Explanation
   - The system implements a variant of the 0/1 Knapsack problem where:
     * Items (Tasks) have:
       - Value (Total earnings = duration × earnings per minute)
       - Weight (Duration in minutes)
     * Knapsack capacity is fixed at 480 minutes (8-hour workday)
     * Goal: Maximize total earnings while respecting time constraints

B. Data Structures Used
   1. Classes:
      - Task: Stores task properties (name, duration, earnings, deadline)
      - TaskScheduler: Manages task list and notifications
      - SchedulingAlgorithm: Implements the knapsack algorithm
      - EmailService: Handles email notifications
      - SchedulerGUI: Provides the user interface

   2. Core Data Structures:
      - ArrayList<Task>: Dynamic list for storing tasks
      - 2D Arrays (dp[][]): For dynamic programming implementation
      - Queue<Task>: For managing task order

C. Design Technique
   1. Object-Oriented Design Patterns:
      - MVC Pattern:
        * Model: Task, TaskScheduler
        * View: SchedulerGUI
        * Controller: SchedulingAlgorithm
      - Service Pattern: EmailService
      - Factory Pattern: For creating Task objects

   2. GUI Design:
      - Swing components for intuitive user interface
      - Grid Bag Layout for responsive design

D. Algorithm Used: 0/1 Knapsack with Dynamic Programming
   - Purpose: Find the optimal subset of tasks that maximizes total earnings
   - Approach: Bottom-up dynamic programming
   - Space Complexity: O(n × W) where:
     * n = number of tasks
     * W = total available time (480 minutes)

III. Algorithm Analysis
----------------------
A. Knapsack Implementation

   Time Complexity:
   - Best Case: O(n × W) - Always need to fill the DP table
   - Average Case: O(n × W)
   - Worst Case: O(n × W)
   where n = number of tasks, W = workday minutes (480)

   Space Complexity:
   - O(n × W) for the DP table and selected[][] array

B. Task Management

   1. Adding Tasks:
      - Best Case: O(1) - Adding to ArrayList
      - Average Case: O(1)
      - Worst Case: O(n) - If ArrayList needs resizing

   2. Email Notifications:
      - Best Case: O(1) - Single email send
      - Average Case: O(1)
      - Worst Case: O(n) - Network delays

IV. Source Code Overview
-----------------------
A. Key Components:

1. Task Class:
   - Properties: name, duration, earningsPerMinute, deadline
   - Methods: getTotalOutput(), getDuration(), getDeadline()

2. SchedulingAlgorithm Class:
   - Main method: knapsackScheduling(List<Task> tasks, int maxTime)
   - Uses dynamic programming for optimal task selection

3. TaskScheduler Class:
   - Manages task list
   - Handles email notifications
   - Integrates with EmailService

4. EmailService Class:
   - Configures SMTP settings
   - Sends formatted email notifications
   - Handles email exceptions

5. SchedulerGUI Class:
   - Provides input fields for task properties
   - Displays scheduled tasks
   - Triggers scheduling and notifications

B. Key Algorithms:

1. Knapsack Implementation:
```java
public static List<Task> knapsackScheduling(List<Task> tasks, int maxTime) {
    int n = tasks.size();
    double[][] dp = new double[n + 1][maxTime + 1];
    boolean[][] selected = new boolean[n + 1][maxTime + 1];

    // Fill DP table
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
```

V. References
------------
1. Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2009). Introduction to Algorithms (3rd ed.). MIT Press.
   - Chapter 16: Dynamic Programming
   - Section 16.2: 0-1 Knapsack Problem

2. Oracle Java Documentation
   - javax.mail API (https://javaee.github.io/javamail/)
   - Swing GUI toolkit (https://docs.oracle.com/javase/tutorial/uiswing/)

3. Design Patterns
   - Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software.
   - Chapter 2: Model-View-Controller Pattern

4. Dynamic Programming References
   - Bellman, R. (1957). Dynamic Programming. Princeton University Press.
   - Chapter 3: The Knapsack Problem

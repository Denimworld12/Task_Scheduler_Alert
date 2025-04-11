package com.nikhil;

import javax.swing.SwingUtilities;

public class App {
    private static final String SENDER_EMAIL = "nikhilrakeshg@gmail.com";
    private static final String EMAIL_PASSWORD = "majg sfbl mdhw bpwx"; // This should be stored securely
    private static final String RECIPIENT_EMAIL = "denimworld12@gmail.com";

    public static void main(String[] args) {
        // Launch the SchedulerGUI with email configuration
        SwingUtilities.invokeLater(() -> {
            SchedulerGUI gui = new SchedulerGUI(SENDER_EMAIL, EMAIL_PASSWORD, RECIPIENT_EMAIL);
            gui.pack();
            gui.setVisible(true);
        });
    }
}
m
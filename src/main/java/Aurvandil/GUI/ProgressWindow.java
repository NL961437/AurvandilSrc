package Aurvandil.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class ProgressWindow {
    JFrame frame;
    JPanel panel;
    JProgressBar bar;
    JLabel primaryLabel;
    JLabel secondaryLabel;
    JLabel fileNameLabel;
    JButton cButton;
    Timer primaryTimer;
    Timer secondaryTimer;
    int secondaryTimerCount = 0;

    /**
     * Primary time elapsed timer
     */
    ActionListener primaryListener = new ActionListener()
    {
        int currentTime = 0;
        public void actionPerformed(ActionEvent event)
        {
            int seconds = currentTime % 60;
            int minutes = currentTime / 60;
            String elapsed;
            if (currentTime % 60 > 9) elapsed = "Elapsed Time: %d:%d"; else elapsed = "Elapsed Time: %d:0%d";
            primaryLabel.setText(String.format(elapsed, minutes, seconds));
            currentTime++;
        }
    };

    /**
     * Secondary timer for file adding time
     */
    ActionListener secondaryListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent event)
        {
            secondaryTimerCount++;
        }
    };

    /**
     * Private constructor to make a customized progress window
     * @param lastFile Include a last file timer
     * @param cancelButton Include a cancel button
     */
    private ProgressWindow(boolean progressBar, boolean lastFile, boolean cancelButton) {
        frame = new JFrame();
        frame.setSize(300,200);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        panel = new JPanel(new GridLayout(0,1));
        frame.add(panel);

        // Progress Bar
        if (progressBar) {
            bar = new JProgressBar();
            panel.add(bar);
            bar.setValue(0);
            bar.setStringPainted(true);
        }

        // Time Elapsed Label
        primaryLabel = new JLabel();
        primaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(primaryLabel);

        // Start Timer
        primaryTimer = new Timer(1000, primaryListener);
        primaryTimer.setInitialDelay(0);
        primaryTimer.start();

        // Last File Timer
        if (lastFile) {
            fileNameLabel = new JLabel();
            panel.add(fileNameLabel);

            secondaryLabel = new JLabel();
            panel.add(secondaryLabel);

            // Start Timer
            secondaryTimer = new Timer(1000, secondaryListener);
            secondaryTimer.setInitialDelay(0);
            secondaryTimer.start();
        }

        // Cancel Button
        if (cancelButton) {
            cButton = new JButton("Cancel");
            panel.add(cButton);
        }

        frame.setVisible(true);
    }

    /**
     * Set the value of the progress bar (0 to 100)
     * @param progress Progress position
     */
    public void setProgressBar(int progress) {
        bar.setValue(progress);
    }

    /**
     * Update the file label, last file time label, and reset file time timer
     * @param fileName name of the file
     */
    public void newFile(String fileName) {
        secondaryTimer.stop();
        int seconds = secondaryTimerCount % 60;
        int minutes = secondaryTimerCount / 60;
        String last;
        if (seconds > 9) last = "Last File Time: %d:%d"; else last = "Last File Time: %d:0%d";
        secondaryLabel.setText(String.format(last, minutes, seconds));
        secondaryTimerCount = 0;
        secondaryTimer.start();
        fileNameLabel.setText(String.format("Current File: %s", fileName));
    }

    /**
     * Close the progress window
     */
    public void closeWindow() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Set the action to be performed when the cancel button is pressed
     * @param cancelListener Listener with cancellation action
     */
    public void setCancelListener(ActionListener cancelListener) {
        cButton.addActionListener(cancelListener);
    }

    /**
     * Disable the cancel button
     */
    public void disableCancelButton() {
        cButton.setEnabled(false);
    }

    /**
     * Factory method to get a progress window with a progress bar, time elapsed, last file time, and cancel button
     * @return Specified ProgressWindow
     */
    public static ProgressWindow getAddFileProgress() {
        return new ProgressWindow(true, true, true);
    }

    /**
     * Factory method to get a progress window with a progress bar, time elapsed, and cancel button
     * @return Specified ProgressWindow
     */
    public static ProgressWindow getProgressTimeCancel() {
        return new ProgressWindow(true, false, true);
    }

    /**
     * Factory method to get a progress window with a progress bar, and time elapsed
     * @return Specified ProgressWindow
     */
    public static ProgressWindow getProgressTime() {
        return new ProgressWindow(true, false, false);
    }

    /**
     * Factory method to get a progress window with time elapsed
     * @return Specified ProgressWindow
     */
    public static ProgressWindow getTime() {
        return new ProgressWindow(false,false, false);
    }
}

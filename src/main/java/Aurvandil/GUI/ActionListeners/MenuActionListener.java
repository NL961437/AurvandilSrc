package Aurvandil.GUI.ActionListeners;

import Aurvandil.Controllers.AurvandilController;
import Aurvandil.GUI.AurvandilGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuActionListener implements ActionListener {
    boolean circuitBreaker = true;
    private AurvandilGUI aurvandilGUI;

    /**
     * Create new listener for events in the "Advanced Commands" cascade menu
     * @param aurvandilGUI AurvandilGUI object
     */
    public MenuActionListener(AurvandilGUI aurvandilGUI) {
        this.aurvandilGUI = aurvandilGUI;
    }

    /**
     * Choose what action to perform based on selection
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "AddFiles":
                addFilesDialog();
                break;
            case "CountStars":
                countStarsDialog();
                break;
            case "BuildSphere":
                buildSphereDialog();
                break;
            case "DestroySphere":
                destroySphereDialog();
                break;
            default:
                break;
        }
    }

    /**
     * Trigger controller to add files to the database
     */
    private void addFilesDialog() {
        aurvandilGUI.setDatabaseInformation();
        int decision = JOptionPane.showConfirmDialog(new JFrame(), "Are all files in the UncommittedFiles directory?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (decision == 0 && circuitBreaker) {
            AurvandilController.addFilesToDatabase();
        }
    }

    /**
     * Trigger controller to count the number of stars in the database
     */
    private void countStarsDialog() {
        aurvandilGUI.setDatabaseInformation();
        if (circuitBreaker) AurvandilController.countAllStars();
    }

    /**
     * Trigger controller to build a HEALPix PostgreSQL database sphere
     */
    private void buildSphereDialog() {
        aurvandilGUI.setDatabaseInformation();
        try {
            int size = Integer.parseInt(JOptionPane.showInputDialog("Please enter number of HEALPix nests (MINIMUM = 1)"));
            if (circuitBreaker) AurvandilController.buildSphere(size);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Invalid Input!");
            e.printStackTrace();
        }
    }

    /**
     * Trigger controller to destroy the current HEALPix PostgreSQL database sphere
     */
    private void destroySphereDialog() {
        aurvandilGUI.setDatabaseInformation();
        int decision = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you want to destroy the sphere?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (decision == 0) {
            if (circuitBreaker) AurvandilController.destroySphere();
        }
    }
}

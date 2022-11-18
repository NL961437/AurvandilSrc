package Aurvandil.GUI.ActionListeners;

import Aurvandil.GUI.AurvandilGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuActionListener implements ActionListener {
    private AurvandilGUI aurvandilGUI;
    public MenuActionListener(AurvandilGUI aurvandilGUI) {
        this.aurvandilGUI = aurvandilGUI;
    }
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

    private void addFilesDialog() {
        aurvandilGUI.setDatabaseInformation();
        JFrame frame = new JFrame();
        int decision = JOptionPane.showConfirmDialog(frame, "Are all files in the UncommittedFiles directory?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (decision == 0) {
            //AurvandilController.addFilesToDatabase(); //TESTING //NEEDS TO HAVE LOADING DIALOG SCREEN
        }
        System.out.println("Adding files to database");
    }

    private void countStarsDialog() {
        aurvandilGUI.setDatabaseInformation();
        JFrame frame = new JFrame();
        long starCount = -1;
        //starCount = AurvandilController.countAllStars(); //TESTING //NEEDS TO HAVE LOADING DIALOG SCREEN
        JOptionPane.showMessageDialog(frame, "Number of stars: " + starCount);
        System.out.println("Counting all stars");
    }

    private void buildSphereDialog() {
        aurvandilGUI.setDatabaseInformation();
        JFrame frame = new JFrame();
        try {
            int size = Integer.parseInt(JOptionPane.showInputDialog("Please enter number of nested pixels (MINIMUM = 1)"));
            //AurvandilController.buildSphere(size); //TESTING //NEEDS TO HAVE LOADING DIALOG SCREEN
            System.out.println("Creating HEALPix Sphere with nSize: " + size);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid Input!");
            e.printStackTrace();
        }
        System.out.println("Building HEALPix sphere");
    }

    private void destroySphereDialog() {
        aurvandilGUI.setDatabaseInformation();
        JFrame frame = new JFrame();
        int decision = JOptionPane.showConfirmDialog(frame, "Are you sure you want to destroy the sphere?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (decision == 0) {
            //AurvandilController.destroySphere(); //TESTING //NEEDS TO HAVE LOADING DIALOG SCREEN
        }
        System.out.println("Destroying HEALPix sphere");
    }
}

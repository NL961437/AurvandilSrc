package Aurvandil.GUI.ActionListeners;

import Aurvandil.Aurvandil;
import Aurvandil.GUI.AurvandilGUI;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class CloseWindowListener implements WindowListener {
    private AurvandilGUI aurvandilGUI;

    /**
     * Create a new listener to run actions on window close
     * @param aurvandilGUI AurvandilGUI object
     */
    public CloseWindowListener(AurvandilGUI aurvandilGUI) {
        this.aurvandilGUI = aurvandilGUI;
    }

    /**
     * Save the values of the fields in the Aurvandil window to a settings cache file
     * @param w the event to be processed
     */
    public void windowClosing(WindowEvent w) {
        aurvandilGUI.saveSettingsToCache();
    }

    public void windowOpened(WindowEvent w) {}

    public void windowClosed(WindowEvent w) {}

    public void windowIconified(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}

    public void windowActivated(WindowEvent e) {}

    public void windowDeactivated(WindowEvent e) {}
}

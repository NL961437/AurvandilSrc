package Aurvandil.GUI.ActionListeners;

import Aurvandil.Aurvandil;
import Aurvandil.GUI.AurvandilGUI;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class CloseWindowListener implements WindowListener {
    private AurvandilGUI aurvandilGUI;
    public CloseWindowListener(AurvandilGUI aurvandilGUI) {
        this.aurvandilGUI = aurvandilGUI;
    }

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

package Aurvandil.GUI.ActionListeners;

import Aurvandil.GUI.AurvandilGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class SetFilterListener implements ActionListener {
    private final int param;
    private final AurvandilGUI aurvandilGUI;

    /**
     * Create new listener to control filter enables and disables
     * @param aurvandilGUI AurvandilGUI object
     * @param param row number of the parameter
     */
    public SetFilterListener(AurvandilGUI aurvandilGUI, int param) {
        this.aurvandilGUI = aurvandilGUI;
        this.param = param;
    }

    /**
     * Change the visibility of filter components for the row the listener is attached to
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        boolean changeTo;
        if (Objects.equals(aurvandilGUI.useFilter[param].getSelectedItem(), "Filter")) {
            changeTo = true;
        } else {
            changeTo = false;
        }

        // BOOLEAN FILTERS
        if (AurvandilGUI.variableTypes[param].contains("bool")) {
            aurvandilGUI.booleanFilter[param].setVisible(changeTo);
        } else

        // INT/FLOAT FILTERS
        if (AurvandilGUI.variableTypes[param].contains("int") || AurvandilGUI.variableTypes[param].contains("float")) {
            // VALUE GREATER THAN
            aurvandilGUI.valueGreaterThan[param].setVisible(changeTo);

            // LESS THAN LABEL
            aurvandilGUI.lt1[param].setVisible(changeTo);

            // VARIABLE LABEL
            aurvandilGUI.variableName[param].setVisible(changeTo);

            // LESS THAN LABEL
            aurvandilGUI.lt2[param].setVisible(changeTo);

            // VALUE LESS THAN
            aurvandilGUI.valueLessThan[param].setVisible(changeTo);
        } else

        // TEXT FILTERS
        if (AurvandilGUI.variableTypes[param].contains("text")) {
            aurvandilGUI.equalsLabel[param].setVisible(changeTo);

            aurvandilGUI.arrowLabel[param].setVisible(changeTo);

            aurvandilGUI.textEqualsTo[param].setVisible(changeTo);
        }
    }
}

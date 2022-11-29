package Aurvandil.GUI.ActionListeners;

import Aurvandil.Controllers.AurvandilController;
import Aurvandil.GUI.AurvandilGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class ConeSearcherListener implements ActionListener {
    private final AurvandilGUI aurvandilGUI;

    /**
     * Create new Cone Search button listener
     * @param aurvandilGUI GUI object with fields
     */
    public ConeSearcherListener(AurvandilGUI aurvandilGUI) {
        this.aurvandilGUI = aurvandilGUI;
    }

    /**
     * Run new cone search with parameters from fields in GUI object
     * Step 1: Get selected fields and filters for each field
     * Step 2: Create a SELECT query from the filters specified in the GUI object
     * Step 3: Run cone search query
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        // FIELDS TO SELECT
        ArrayList<String> fields = new ArrayList<>();
        ArrayList<String> qualifiers = new ArrayList<>();
        for (int var = 0; var < AurvandilGUI.variables.length; var++) {
            if (aurvandilGUI.toggleBoxes[var].isSelected()) {
                fields.add(AurvandilGUI.variables[var]);
            }
            if (aurvandilGUI.useFilter[var].getSelectedItem() == "Filter") {
                // BOOLEAN FILTERS
                if (AurvandilGUI.variableTypes[var].contains("bool")) {
                    qualifiers.add(AurvandilGUI.variables[var] + " = " + aurvandilGUI.booleanFilter[var].getSelectedItem());
                } else

                // INT/FLOAT FILTERS
                if (AurvandilGUI.variableTypes[var].contains("int") || AurvandilGUI.variableTypes[var].contains("float")) {
                    if (Objects.equals(aurvandilGUI.valueGreaterThan[var].getText(), "") || Objects.equals(aurvandilGUI.valueLessThan[var].getText(), "")) {
                        return;
                    }
                    qualifiers.add(aurvandilGUI.valueGreaterThan[var].getText() + " < " + AurvandilGUI.variables[var] + " < " + aurvandilGUI.valueLessThan[var].getText());
                } else

                // TEXT FILTERS
                if (AurvandilGUI.variableTypes[var].contains("text")) {
                    if (Objects.equals(aurvandilGUI.textEqualsTo[var].getText(), "")) {
                        return;
                    }
                    qualifiers.add(AurvandilGUI.variables[var] + " = '" + aurvandilGUI.textEqualsTo[var].getText() + "'");
                }
            }
        }

        if (fields.size() == AurvandilGUI.variables.length) {
            fields = new ArrayList<>();
            fields.add("*");
        }

        String query;
        if (qualifiers.size() > 0) {
            query = "SELECT " + String.join(",", fields) + " FROM %s WHERE " + String.join(" AND ", qualifiers);
        } else {
            query = "SELECT " + String.join(",", fields) + " FROM %s";
        }
        System.out.println("Running cone search with query"); // TESTING
        aurvandilGUI.setDatabaseInformation();
        //AurvandilController.coneSearch(Double.parseDouble(aurvandilGUI.raField.getText()), Double.parseDouble(aurvandilGUI.decField.getText()), Double.parseDouble(aurvandilGUI.radField.getText()), query); //TESTING
    }
}

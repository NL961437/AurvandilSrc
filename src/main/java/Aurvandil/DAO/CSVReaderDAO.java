package Aurvandil.DAO;

import java.io.File;
import java.util.Objects;
import java.util.Scanner;

public class CSVReaderDAO {
    private Scanner reader = null;
    private int rowCount;

    /**
     * Create new data access object of a GDR3 CSV file to read data
     * @param name name of CSV file
     */
    public CSVReaderDAO(String name) {
        try {
            File myObj = new File(name);
            reader = new Scanner(myObj);
            String data = "#";
            while (reader.hasNextLine() && data.startsWith("#")) {
                data = reader.nextLine();
            }

            while (reader.hasNextLine()) {
                reader.nextLine();
                rowCount++;
            }

            reader = new Scanner(myObj);
            data = "#";
            while (reader.hasNextLine() && data.startsWith("#")) {
                data = reader.nextLine();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get next data row
     * @return String array of row data
     */
    public String[] getNextRow() {
        if (reader.hasNextLine()) {
            return reader.nextLine().split(",");
        } else {
            return null;
        }
    }

    /**
     * Close CSV data access object
     */
    public void closeScanner() {
        reader.close();
    }

    /**
     * Get the number of data rows in the CSV file
     * @return number of rows
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Parse row data for insertion into database
     * @param row String array of raw row data
     * @return String array of parsed row data
     */
    public static String[] convertRow(String[] row) {
        row[1] = row[1].replaceAll("\"", "'");
        row[111] = row[111].replaceAll("\"", "'");
        row[151] = row[151].replaceAll("\"", "'");

        for (int col = 2; col < row.length; col++) {
            if (Objects.equals(row[col], "\"False\"")) {
                row[col] = "False";
            } else if (Objects.equals(row[col], "\"True\"")) {
                row[col] = "True";
            }
        }

        return row;
    }
}

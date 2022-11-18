package Aurvandil.DAO;

import java.io.File;
import java.util.Objects;
import java.util.Scanner;

public class CSVReaderDAO {
    private Scanner reader = null;
    private int rowCount;

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

    public String[] getNextRow() {
        if (reader.hasNextLine()) {
            return reader.nextLine().split(",");
        } else {
            return null;
        }
    }

    public void closeScanner() {
        reader.close();
    }

    public int getRowCount() {
        return rowCount;
    }

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

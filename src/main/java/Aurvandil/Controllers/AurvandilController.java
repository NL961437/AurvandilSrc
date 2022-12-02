package Aurvandil.Controllers;

import Aurvandil.BA.HealPixBA;
import Aurvandil.BA.PostgreSQLBA;
import Aurvandil.DAO.CSVReaderDAO;
import Aurvandil.DAO.CSVWriterDAO;
import Aurvandil.GUI.ProgressWindow;
import Aurvandil.Util.FileUtil;
import Aurvandil.Util.SphereConstructorUtil;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class AurvandilController {

    /**
     * Adds file contents to the HEALPix PostgreSQL database from "UncommittedFiles" folder
     * Step 1: Check a HEALPix SQL database exists
     * Step 2: Get all files in the "UncommittedFiles" folder
     * Step 3: Parse file contents for insertion into database
     * Step 4: Build SQL INSERT command to add star to the database
     * Step 5: Add to batch job
     * Step 6: Once all rows in a file have been added to the batch job, execute the batch job
     * Step 7: Close file and move to "CommittedFiles" folder
     * Repeat until all files have been added
     */
    public static void addFilesToDatabase() {
        Connection connection = PostgreSQLBA.createConnection();
        if (!PostgreSQLBA.sphereExists(connection)) {
            PostgreSQLBA.closeConnection(connection);
            JOptionPane.showMessageDialog(new JFrame(), "Can not add files, a HEALPix Sphere doesn't exist");
            return;
        }
        HealPixBA sphere = new HealPixBA(PostgreSQLBA.getSphereSize(connection));

        File[] fileList = FileUtil.getUncommittedFiles();

        ProgressWindow progressWindow = ProgressWindow.getAddFileProgress();
        final boolean[] cancel = {false};

        final JFrame[] cancelFrame = new JFrame[1];
        ActionListener cancelListener = event -> {
            cancel[0] = true;
            cancelFrame[0] = new JFrame();
            JLabel warningText = new JLabel("Cancelling. DO NOT CLOSE THE PROGRAM");
            warningText.setHorizontalAlignment(SwingConstants.CENTER);
            cancelFrame[0].add(warningText);
            cancelFrame[0].setVisible(true);
            cancelFrame[0].setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            cancelFrame[0].setSize(300,100);
            progressWindow.disableCancelButton();
        };
        progressWindow.setCancelListener(cancelListener);

        class AddFilesThread extends Thread {
            public void run(){
                double fileCount = 0;
                for (File file : fileList) {
                    if (cancel[0]) {
                        break;
                    }
                    progressWindow.newFile(file.getName());

                    CSVReaderDAO csvReaderDAO = new CSVReaderDAO(file.getAbsolutePath());
                    int numRows = csvReaderDAO.getRowCount();
                    String[] commands = new String[numRows];

                    for (int rowNum = 0; rowNum < numRows; rowNum++) {
                        String[] row = CSVReaderDAO.convertRow(csvReaderDAO.getNextRow());
                        long pixel = sphere.getPixelLocation(Double.parseDouble(row[5]), Double.parseDouble(row[7]));
                        commands[rowNum] = String.format("INSERT INTO pixel%d VALUES (%s)", pixel, String.join(",", row));
                    }

                    PostgreSQLBA.runCommands(commands, connection);
                    csvReaderDAO.closeScanner();

                    FileUtil.moveFile(file);

                    fileCount++;
                    progressWindow.setProgressBar((int) ((fileCount / fileList.length) * 100));
                }
                progressWindow.closeWindow();
                if (cancel[0]) {
                    cancelFrame[0].setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    cancelFrame[0].dispatchEvent(new WindowEvent(cancelFrame[0], WindowEvent.WINDOW_CLOSING));
                } else {
                    JOptionPane.showMessageDialog(null, "All files added to database!");
                }
                PostgreSQLBA.closeConnection(connection);
            }
        }
        AddFilesThread mainThread = new AddFilesThread();
        mainThread.start();
    }

    /**
     * Count the number of stars currently stored in the database
     * Loops through all tables in the database querying for the number of rows.
     */
    public static void countAllStars() {
        Connection connection = PostgreSQLBA.createConnection();

        if (!PostgreSQLBA.sphereExists(connection)) {
            PostgreSQLBA.closeConnection(connection);
            JOptionPane.showMessageDialog(new JFrame(), "Can not count stars, HEALPix sphere does not exist");
            return;
        }

        String findNumberOfRows = "SELECT COUNT(solution_id) FROM %s";
        int pixels = PostgreSQLBA.getSphereSize(connection);
        ProgressWindow progressWindow = ProgressWindow.getProgressTimeCancel();
        final boolean[] cancel = {false};

        ActionListener cancelListener = event -> cancel[0] = true;
        progressWindow.setCancelListener(cancelListener);

        class CountStarsThread extends Thread {
            public void run(){
                long sum = 0;
                for (int i = 0; i < pixels; i++) {
                    if (cancel[0]) {
                        break;
                    }
                    try {
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery(String.format(findNumberOfRows, "pixel" + i));
                        rs.next();
                        sum += rs.getInt(1);
                        st.close();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    progressWindow.setProgressBar((int)(i / (double)pixels * 100));
                }

                PostgreSQLBA.closeConnection(connection);
                progressWindow.closeWindow();
                if (!cancel[0]) {
                    JOptionPane.showMessageDialog(new JFrame(), "Number of stars: " + sum);
                }
            }
        }
        CountStarsThread mainThread = new CountStarsThread();
        mainThread.start();
    }

    /**
     * Build HEALPix PostgreSQL database
     * Step 1: Create connection to the database and check if sphere already exists
     * Step 2: Calculate the number of pixels with nSize parameter
     * Step 3: Generate CREATE TABLE commands and add to batch job
     * Step 4: Execute batch job
     * Step 5: Close database connection
     * @param nSize number of HEALPix nests (Min. 1)
     */
    public static void buildSphere(int nSize) {
        Connection connection = PostgreSQLBA.createConnection();
        if (PostgreSQLBA.sphereExists(connection)) {
            PostgreSQLBA.closeConnection(connection);
            JOptionPane.showMessageDialog(new JFrame(), "Can not create sphere, one already exists");
            return;
        }

        ProgressWindow progressWindow = ProgressWindow.getTime();

        class BuildSphereThread extends Thread {
            public void run(){
                int pixelsNeeded = 12 * (int) Math.pow(4, nSize - 1);
                String[] commands = SphereConstructorUtil.getBuildSphereCommands(pixelsNeeded);
                PostgreSQLBA.runCommands(commands, connection);
                PostgreSQLBA.closeConnection(connection);
                progressWindow.closeWindow();
                JOptionPane.showMessageDialog(new JFrame(), "Created HEALPix sphere with " + pixelsNeeded + " pixels");
            }
        }
        BuildSphereThread mainThread = new BuildSphereThread();
        mainThread.start();
    }

    /**
     * Destroy HEALPix PostgreSQL database sphere
     * Step 1: Establish database connection and check if sphere exists
     * Step 2: Get names of all tables in the database
     * Step 3: Generate commands from database names and add to batch job
     * Step 4: Execute batch job
     * Step 5: Close database connection
     */
    public static void destroySphere() {
        Connection connection = PostgreSQLBA.createConnection();
        if (!PostgreSQLBA.sphereExists(connection)) {
            PostgreSQLBA.closeConnection(connection);
            JOptionPane.showMessageDialog(new JFrame(), "Can not destroy sphere, HEALPix sphere does not exist");
            return;
        }

        ProgressWindow progressWindow = ProgressWindow.getTime();
        class DestroySphereThread extends Thread {
            public void run(){
                String[] commands =  SphereConstructorUtil.getDestroySphereCommands(connection);
                PostgreSQLBA.runCommands(commands, connection);
                PostgreSQLBA.closeConnection(connection);
                progressWindow.closeWindow();
                JOptionPane.showMessageDialog(new JFrame(), "Destroyed HEALPix Sphere");
            }
        }
        DestroySphereThread mainThread = new DestroySphereThread();
        mainThread.start();
    }

    /**
     * Step 1: Check if inputs are valid
     * Step 2: Create database connection and check if sphere exists
     * Step 3: Generate query commands and run
     * Step 4: Write results to new CSV file in the "Queries" folder
     * Step 5: Close database connection
     * @param ra right ascension in degrees (0 to 360)
     * @param dec declination in degrees (-90 to 90)
     * @param rad cone search radius in degrees
     * @param query general query with desired fields and filters
     */
    public static void coneSearch(double ra, double dec, double rad, String query) {
        Connection connection = PostgreSQLBA.createConnection();
        if (!PostgreSQLBA.sphereExists(connection)) {
            PostgreSQLBA.closeConnection(connection);
            JOptionPane.showMessageDialog(new JFrame(), "Can not cone search, HEALPix sphere does not exist");
            return;
        }

        if (ra > 360 || ra < 0) {
            JOptionPane.showMessageDialog(new JFrame(), "Right ascension out of range (0 - 360)");
            return;
        } else if (dec > 90 || dec < -90) {
            JOptionPane.showMessageDialog(new JFrame(), "Declination out of range (-90 - 90)");
            return;
        } else if (rad < 0 || rad > 180) {
            JOptionPane.showMessageDialog(new JFrame(), "Radius out of range (0 - 180)");
            return;
        }

        ProgressWindow progressWindow = ProgressWindow.getProgressTimeCancel();
        class ConeSearchThread extends Thread {
            public void run(){
                HealPixBA healPixBA = new HealPixBA(PostgreSQLBA.getSphereSize(connection));
                CSVWriterDAO file = new CSVWriterDAO();
                double pixelCount = 0;
                long[] pixelsToSearch = healPixBA.conePixelSearch(ra, dec, rad);
                for (long pixel : pixelsToSearch) {
                    List<String> queryResults = PostgreSQLBA.makeQuery(pixel, connection, query, ra, dec, rad);
                    for (String star : queryResults) {
                        file.writeLine(star);
                    }
                    pixelCount++;
                    progressWindow.setProgressBar((int)(pixelCount / pixelsToSearch.length * 100));
                }

                PostgreSQLBA.closeConnection(connection);
                file.closeFile();
                progressWindow.closeWindow();
            }
        }
        ConeSearchThread mainThread = new ConeSearchThread();
        mainThread.start();
    }
}

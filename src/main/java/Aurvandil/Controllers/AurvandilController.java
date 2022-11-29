package Aurvandil.Controllers;

import Aurvandil.BA.HealPixBA;
import Aurvandil.BA.PostgreSQLBA;
import Aurvandil.DAO.CSVReaderDAO;
import Aurvandil.DAO.CSVWriterDAO;
import Aurvandil.Util.FileUtil;
import Aurvandil.Util.SphereConstructorUtil;

import java.io.File;
import java.sql.Connection;
import java.util.Date;
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
            System.out.println("Can not add files, no sphere exists");
            PostgreSQLBA.closeConnection(connection);
            return;
        }
        HealPixBA sphere = new HealPixBA(PostgreSQLBA.getSphereSize(connection));
        for (File file : FileUtil.getUncommittedFiles()) {
            long start = new Date().getTime();
            System.out.println(file.getName());

            System.out.println("COMPILE COMMANDS");
            CSVReaderDAO csvReaderDAO = new CSVReaderDAO(file.getAbsolutePath());
            int numRows = csvReaderDAO.getRowCount();
            String[] commands = new String[numRows];

            for (int rowNum = 0; rowNum < numRows; rowNum++) {
                String[] row = CSVReaderDAO.convertRow(csvReaderDAO.getNextRow());
                long pixel = sphere.getPixelLocation(Double.parseDouble(row[5]), Double.parseDouble(row[7]));
                commands[rowNum] = String.format("INSERT INTO pixel%d VALUES (%s)", pixel, String.join(",", row));
            }

            System.out.println("RUN COMMANDS");
            PostgreSQLBA.runCommands(commands, connection);
            csvReaderDAO.closeScanner();

            FileUtil.moveFile(file);
            System.out.println((new Date().getTime() - start)/1000.0 + " seconds");
        }
        PostgreSQLBA.closeConnection(connection);
    }

    /**
     * Count the number of stars currently stored in the database
     * Loops through all tables in the database querying for the number of rows.
     * @return number of stars in HEALPix SQL database
     */
    public static long countAllStars() {
        Connection connection = PostgreSQLBA.createConnection();
        long starCount = PostgreSQLBA.getNumberOfRows(connection);
        PostgreSQLBA.closeConnection(connection);
        return starCount;
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
            System.out.println("Can not create sphere, one already exists");
            PostgreSQLBA.closeConnection(connection);
            return;
        }
        int pixelsNeeded = 12 * (int) Math.pow(4, nSize - 1);
        System.out.println("CREATING SPHERE OF SIZE " + pixelsNeeded);
        String[] commands = SphereConstructorUtil.getBuildSphereCommands(pixelsNeeded);
        PostgreSQLBA.runCommands(commands, connection);
        PostgreSQLBA.closeConnection(connection);
        System.out.println("DONE");
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
            System.out.println("Can not destroy sphere, none exists");
            PostgreSQLBA.closeConnection(connection);
            return;
        }
        System.out.println("DESTROYING SPHERE");
        String[] commands =  SphereConstructorUtil.getDestroySphereCommands(connection);
        PostgreSQLBA.runCommands(commands, connection);
        PostgreSQLBA.closeConnection(connection);
        System.out.println("DONE");
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
        long start = new Date().getTime();
        if (ra > 360 || ra < 0) {
            System.out.println("Right ascension out of range (0 - 360)");
            return;
        } else if (dec > 90 || dec < -90) {
            System.out.println("Declination out of range (-90 - 90)");
            return;
        } else if (rad < 0 || rad > 180) {
            System.out.println("Radius out of range (0 - 180)");
            return;
        }
        System.out.printf("Running cone search on RA: %f, DEC: %f, RAD: %f\n", ra, dec, rad);
        Connection connection = PostgreSQLBA.createConnection();
        HealPixBA healPixBA = new HealPixBA(PostgreSQLBA.getSphereSize(connection));
        CSVWriterDAO file = new CSVWriterDAO();
        for (long pixel : healPixBA.conePixelSearch(ra, dec, rad)) {
            List<String> queryResults = PostgreSQLBA.makeQuery(pixel, connection, query, ra, dec, rad);
            for (String star : queryResults) {
                file.writeLine(star);
            }
        }

        PostgreSQLBA.closeConnection(connection);
        file.closeFile();
        System.out.println("Cone search took " + (new Date().getTime() - start)/1000.0 + " seconds");
    }
}

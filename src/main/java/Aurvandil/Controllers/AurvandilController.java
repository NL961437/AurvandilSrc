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

    public static long countAllStars() {
        Connection connection = PostgreSQLBA.createConnection();
        long starCount = PostgreSQLBA.getNumberOfRows(connection);
        PostgreSQLBA.closeConnection(connection);
        return starCount;
    }

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

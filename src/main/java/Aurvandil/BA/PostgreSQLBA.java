package Aurvandil.BA;

import Aurvandil.GUI.ProgressWindow;
import Aurvandil.Util.HaversineDistanceUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class PostgreSQLBA {
    private static String ip;
    private static String port;
    private static String username;
    private static String password;

    /**
     * Set database settings for the window
     * @param newIP database ip
     * @param newPort database port
     * @param newUser database username
     * @param newPass database user password
     */
    public static void setDatabaseInformation(String newIP, String newPort, String newUser, String newPass) {
        ip = newIP;
        port = newPort;
        username = newUser;
        password = newPass;
    }

    /**
     * Run a set of commands on the targeted PostgreSQL database
     * @param commands String array of commands to execute
     * @param conn Java database Connection
     */
    public static void runCommands(String[] commands, Connection conn) {
        try {
            Statement statement = conn.createStatement();
            for (String command : commands) {
                statement.addBatch(command);
            }
            statement.executeBatch();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Create Java SQL database connection
     * @return Java database connection
     */
    public static Connection createConnection() {
        try {
            String url = ip;
            Properties props = new Properties();
            props.setProperty("port", port);
            props.setProperty("user", username);
            props.setProperty("password", password);
            return DriverManager.getConnection(url, props);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Close Java SQL database connection
     * @param connection connection to terminate
     */
    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get the size of the sphere currently stored in the SQL database
     * @param conn Java SQL database connection
     * @return size of current sphere
     */
    public static int getSphereSize(Connection conn) {
        String findNumberOfTables = "SELECT COUNT(table_name) FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(findNumberOfTables);
            rs.next();
            int size = rs.getInt(1);
            st.close();
            return size;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    /**
     * Get a list of query commands to send to the HEALPix pixel table
     * @param pixel pixel to send queries to
     * @param connection Java SQL database connection
     * @param query full query with included filters
     * @param ra right ascension in degrees (0 to 360)
     * @param dec declination in degrees (-90 to 90)
     * @param rad radius of the cone search in degrees
     * @return String list of commands
     */
    public static List<String> makeQuery(long pixel, Connection connection, String query, double ra, double dec, double rad) {
        List<String> stars = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet queryReturn = stmt.executeQuery(String.format(query, pixel));
            int columns = queryReturn.getMetaData().getColumnCount();
            String[] values = new String[columns];
            while (queryReturn.next()) {
                if (HaversineDistanceUtil.checkDistance(ra, dec, rad, queryReturn.getDouble(6), queryReturn.getDouble(8))) {
                    for (int col = 1; col <= columns; col++) {
                        values[col-1] = queryReturn.getString(col);
                    }
                    stars.add(String.join(",", values));
                }
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stars;
    }

    /**
     * Check if an existing HEALPix sphere already exists in the PostgreSQL database
     * @param conn Java SQL database connection
     * @return boolean if sphere exists
     */
    public static boolean sphereExists(Connection conn) {
        String findNumberOfTables = "SELECT COUNT(table_name) FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(findNumberOfTables);
            rs.next();
            int size = rs.getInt(1);
            st.close();
            return size > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return true;
        }
    }
}

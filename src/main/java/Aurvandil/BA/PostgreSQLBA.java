package Aurvandil.BA;

import Aurvandil.Util.HaversineDistanceUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PostgreSQLBA {
    private static String ip;
    private static String port;
    private static String username;
    private static String password;
    public static void setDatabaseInformation(String newIP, String newPort, String newUser, String newPass) {
        ip = newIP;
        port = newPort;
        username = newUser;
        password = newPass;
    }

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

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get size of current sphere if any
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

    public static long getNumberOfRows(Connection conn) {
        String findNumberOfRows = "SELECT COUNT(solution_id) FROM %s";
        int pixels = getSphereSize(conn);
        long sum = 0;
        for (int i = 0; i < pixels; i++) {
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(String.format(findNumberOfRows, "pixel" + i));
                rs.next();
                sum += rs.getInt(1);
                st.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return sum;
    }

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

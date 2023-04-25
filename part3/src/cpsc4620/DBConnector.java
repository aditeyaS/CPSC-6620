package cpsc4620;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConnector {

    //TODO: make this false when on production
    static boolean isTesting = true;

    // enter your user name here
    protected static String user = isTesting ? "root" : "aditeys";
    // enter your password here
    protected static String password = isTesting ? "Sher@221B" : "Clemson123";
    // enter your database name here
    private static String database_name = isTesting ? "proj_p2_g1" : "proj_p2_g1";
    // Do not change the port. 3306 is the default MySQL port
    private static String url = isTesting ? "jdbc:mysql://localhost:3306" : "jdbc:mysql://final-project.c5wyqp0fypwo.us-east-1.rds.amazonaws.com:3360";
    private static Connection conn;


    /**
     * This function will handle the connection to the database
     *
     * @return true if the connection was successfully made
     * @throws SQLException
     * @throws IOException
     */
    public static Connection make_connection() throws SQLException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load the driver");

            System.out.println("Message     : " + e.getMessage());

            return null;
        }

        conn = DriverManager.getConnection(url + "/" + database_name, user, password);
        return conn;
    }
}

package sk.upjs.winston.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by stefan on 2/15/15.
 */
public class DatabaseConnectionFactory {
    //static reference to itself
    private static DatabaseConnectionFactory instance = new DatabaseConnectionFactory();
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://stefanbocko.sk/nh2096401db?useUnicode=yes&characterEncoding=UTF-8";
    //  Database credentials
    static final String USER = "nh2096401";
    static final String PASS = "taraystol";

    //private constructor
    private DatabaseConnectionFactory() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return connection;
    }

    public static Connection getConnection() {
        return instance.createConnection();
    }
}

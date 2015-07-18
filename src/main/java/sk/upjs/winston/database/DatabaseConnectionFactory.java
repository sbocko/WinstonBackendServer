package sk.upjs.winston.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by stefan on 2/15/15.
 */
public class DatabaseConnectionFactory {
    private static final boolean PRODUCTION_ENVIRONMENT = false;
    //static reference to itself
    private static DatabaseConnectionFactory instance = new DatabaseConnectionFactory();
    // JDBC driver name and database URL
    static final String TEST_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String TEST_DB_URL = "jdbc:mysql://stefanbocko.sk/nh2096401db?useUnicode=yes&characterEncoding=UTF-8";
    static final String TEST_USER = "nh2096401";
    static final String TEST_PASS = "taraystol";

    static final String PROD_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String PROD_DB_URL = "jdbc:mysql://158.197.29.209/Winston?useUnicode=yes&characterEncoding=UTF-8";
    static final String PROD_USER = "bocko";
    static final String PROD_PASS = "176ed3eef5f3683b4f1129b7b2215859";

    static String JDBC_DRIVER;
    static String DB_URL;
    static String USER;
    static String PASS;

    //private constructor
    private DatabaseConnectionFactory() {
        if (PRODUCTION_ENVIRONMENT) {
            JDBC_DRIVER = PROD_JDBC_DRIVER;
            DB_URL = PROD_DB_URL;
            USER = PROD_USER;
            PASS = PROD_PASS;
        } else {
            JDBC_DRIVER = TEST_JDBC_DRIVER;
            DB_URL = TEST_DB_URL;
            USER = TEST_USER;
            PASS = TEST_PASS;
        }
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
            e.printStackTrace();
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return connection;
    }

    public static Connection getConnection() {
        return instance.createConnection();
    }
}

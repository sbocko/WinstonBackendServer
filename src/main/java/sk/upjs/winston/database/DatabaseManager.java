package sk.upjs.winston.database;

import sk.upjs.winston.model.Dataset;

import java.sql.*;

/**
 * Manages the database operations through JDBC.
 */
public class DatabaseManager {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://stefanbocko.sk/nh2096401db?useUnicode=yes&characterEncoding=UTF-8";
    //  Database credentials
    static final String USER = "nh2096401";
    static final String PASS = "taraystol";
    //table names
    private static final String TABLE_DATASET = "dataset";
    private static final String TABLE_ATTRIBUTE = "attribute";

    public Dataset getDataset(long datasetId) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT arff_data_file, missing_value_pattern, number_of_instances, number_of_missing_values FROM " + TABLE_DATASET + " WHERE id = " + datasetId + ";";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
//                int id = rs.getInt("datasetId");
//                int age = rs.getInt("age");
//                String first = rs.getString("first");
                String arffDataFilename = rs.getString("arff_data_file");

                //Display values
                System.out.print("DATA FILE NAME: " + arffDataFilename);
//                System.out.print(", Age: " + age);
//                System.out.print(", First: " + first);
//                System.out.println(", Last: " + last);
            }
            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();

            return null;
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
        return null;
    }

}

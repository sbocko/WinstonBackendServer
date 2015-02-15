package sk.upjs.winston.database;

import sk.upjs.winston.model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the database operations through JDBC.
 */


public class DatabaseManager {

    //table names
    private static final String TABLE_DATASET = "dataset";
    private static final String TABLE_ATTRIBUTE = "attribute";
    private static final String TABLE_ANALYSIS = "analysis";
    public static final String CLASS_WINSTON_NUMERIC_ATTRIBUTE = "winston.NumericAttribute";
    public static final String CLASS_WINSTON_STRING_ATTRIBUTE = "winston.StringAttribute";
    public static final String CLASS_WINSTON_BOOLEAN_ATTRIBUTE = "winston.BooleanAttribute";
    public static final int COLUMN_INDEX = 1;
    public static final int DATA_VERSION = 1;

    public Dataset getDataset(long datasetId) {
        Dataset result = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT title, data_file, arff_data_file, missing_value_pattern, number_of_missing_values, number_of_instances FROM " + TABLE_DATASET + " WHERE id = " + datasetId + ";";
            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            if (rs.next()) {
                //Retrieve by column name
                String title = rs.getString("title");
                String csvDataFilename = rs.getString("data_file");
                String arffDataFilename = rs.getString("arff_data_file");
                String missingValuePattern = rs.getString("missing_value_pattern");
                int numberOfMissingValues = rs.getInt("number_of_missing_values");
                int numberOfInstances = rs.getInt("number_of_instances");
                List<Attribute> attributes = getAttributesForDataset(conn, datasetId);
                result = new Dataset(datasetId, title, csvDataFilename, arffDataFilename, missingValuePattern, numberOfMissingValues, numberOfInstances, attributes);
            }

            rs.close();
            statement.close();
            conn.close();
            return result;
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
                // nothing we can do
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return result;
    }

    public Attribute getAttribute(long attributeId) {
        Attribute result = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT dataset_id, number_of_missing_values, position_in_data_file, title, class, average, maximum, minimum, number_of_distinct_values, number_of_false_values, number_of_true_values FROM " + TABLE_ATTRIBUTE + " WHERE id = " + attributeId + ";";
            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            if (rs.next()) {
                //Retrieve by column name
                int numberOfMissingValues = rs.getInt("number_of_missing_values");
                int positionInDataFile = rs.getInt("position_in_data_file");
                String title = rs.getString("title");
                String attributeClass = rs.getString("class");

                if (CLASS_WINSTON_NUMERIC_ATTRIBUTE.equals(attributeClass)) {
                    double average = rs.getDouble("average");
                    double maximum = rs.getDouble("maximum");
                    double minimum = rs.getDouble("minimum");
                    int numberOfDistinctValues = rs.getInt("number_of_distinct_values");
                    Attribute numeric = new NumericAttribute(attributeId, title, numberOfMissingValues, positionInDataFile, average, minimum, maximum, numberOfDistinctValues);
                    result = numeric;
                } else if (CLASS_WINSTON_STRING_ATTRIBUTE.equals(attributeClass)) {
                    int numberOfDistinctValues = rs.getInt("number_of_distinct_values");
                    Attribute string = new StringAttribute(attributeId, title, numberOfMissingValues, positionInDataFile, numberOfDistinctValues);
                    result = string;
                } else if (CLASS_WINSTON_BOOLEAN_ATTRIBUTE.equals(attributeClass)) {
                    int numberOfFalseValues = rs.getInt("number_of_false_values");
                    int numberOfTrueValues = rs.getInt("number_of_true_values");
                    Attribute booleanAttribute = new BooleanAttribute(attributeId, title, numberOfMissingValues, positionInDataFile, numberOfTrueValues, numberOfFalseValues);
                    result = booleanAttribute;
                }
            }
            rs.close();
            statement.close();
            conn.close();
            return result;
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
                // nothing we can do
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return result;
    }

    public Long saveAnalysis(Analysis analysis) {
        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();

            int analyzedByGridSearch = analysis.isAnalyzedByGridSearch() ? 1 : 0;
            int gridSearchAnalysisInProgress = analysis.isGridSearchAnalysisInProgress() ? 1 : 0;

            String insertQuery = "INSERT INTO " + TABLE_ANALYSIS
                    + "(dataset_id, data_file, data_type, number_of_attributes, analyzed_by_grid_search, grid_search_analysis_in_progress, version) " + "VALUES"
                    + " (" + analysis.getDataset().getId() + ",'" + analysis.getDataFile() + "','" + analysis.getDataType() + "', " + analysis.getNumberOfAttributes() + ", "
                    + analyzedByGridSearch + ", " + gridSearchAnalysisInProgress + ", " + DATA_VERSION + ")";
//            System.out.println("QUERY: " + insertQuery);

            statement.executeUpdate(insertQuery);
            ResultSet rs = statement.getGeneratedKeys();

            Long analysisId = null;
            if (rs.next()) {
                analysisId = rs.getLong(COLUMN_INDEX);
            }

            rs.close();
            statement.close();
            conn.close();
            return analysisId;
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
                // nothing we can do
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return null;
    }

    /**
     * HELPER METHODS
     */

    private List<Attribute> getAttributesForDataset(Connection conn, long datasetId) throws SQLException {
        List<Attribute> datasetAttributes = new ArrayList<Attribute>();

        Statement statement = conn.createStatement();
        String query = "SELECT id, dataset_id, number_of_missing_values, position_in_data_file, title, class, average, maximum, minimum, number_of_distinct_values, number_of_false_values, number_of_true_values FROM " + TABLE_ATTRIBUTE + " WHERE dataset_id = " + datasetId + ";";
        ResultSet rs = statement.executeQuery(query);

        // Extract data from result set
        while (rs.next()) {
            //Retrieve by column name
            long id = rs.getLong("id");
            int numberOfMissingValues = rs.getInt("number_of_missing_values");
            int positionInDataFile = rs.getInt("position_in_data_file");
            String title = rs.getString("title");
            String attributeClass = rs.getString("class");

            if (CLASS_WINSTON_NUMERIC_ATTRIBUTE.equals(attributeClass)) {
                double average = rs.getDouble("average");
                double maximum = rs.getDouble("maximum");
                double minimum = rs.getDouble("minimum");
                int numberOfDistinctValues = rs.getInt("number_of_distinct_values");
                Attribute numeric = new NumericAttribute(id, title, numberOfMissingValues, positionInDataFile, average, minimum, maximum, numberOfDistinctValues);
                datasetAttributes.add(numeric);
            } else if (CLASS_WINSTON_STRING_ATTRIBUTE.equals(attributeClass)) {
                int numberOfDistinctValues = rs.getInt("number_of_distinct_values");
                Attribute string = new StringAttribute(id, title, numberOfMissingValues, positionInDataFile, numberOfDistinctValues);
                datasetAttributes.add(string);
            } else if (CLASS_WINSTON_BOOLEAN_ATTRIBUTE.equals(attributeClass)) {
                int numberOfFalseValues = rs.getInt("number_of_false_values");
                int numberOfTrueValues = rs.getInt("number_of_true_values");
                Attribute booleanAttribute = new BooleanAttribute(id, title, numberOfMissingValues, positionInDataFile, numberOfTrueValues, numberOfFalseValues);
                datasetAttributes.add(booleanAttribute);
            }
        }

        rs.close();
        statement.close();
        return datasetAttributes;
    }
}

package sk.upjs.winston.database;

import sk.upjs.winston.algorithms.DecisionTreeModel;
import sk.upjs.winston.algorithms.KnnModel;
import sk.upjs.winston.algorithms.LogisticRegressionModel;
import sk.upjs.winston.algorithms.SvmModel;
import sk.upjs.winston.model.*;

import java.sql.*;
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
    private static final String TABLE_ANALYSIS_RESULT = "analysis_result";
    private static final String TABLE_USER = "user";
    public static final String CLASS_WINSTON_NUMERIC_ATTRIBUTE = "winston.NumericAttribute";
    public static final String CLASS_WINSTON_STRING_ATTRIBUTE = "winston.StringAttribute";
    public static final String CLASS_WINSTON_BOOLEAN_ATTRIBUTE = "winston.BooleanAttribute";
    public static final String CLASS_WINSTON_KNN_RESULT = "winston.KnnResult";
    public static final String CLASS_WINSTON_LOGISTIC_REGRESSION_RESULT = "winston.LogisticRegressionResult";
    public static final String CLASS_WINSTON_DECISION_TREE_RESULT = "winston.DecisionTreeResult";
    public static final String CLASS_WINSTON_SVM_RESULT = "winston.SvmResult";
    public static final int COLUMN_INDEX = 1;
    public static final int DATA_VERSION = 1;

    public Dataset getDataset(long datasetId) {
        Dataset result = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT title, data_file, arff_data_file, missing_value_pattern, number_of_missing_values, number_of_instances, user_id FROM " + TABLE_DATASET + " WHERE id = " + datasetId + ";";
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
                long userId = rs.getLong("user_id");
                User user = getUser(userId);
                List<Attribute> attributes = getAttributesForDataset(conn, datasetId);
                result = new Dataset(datasetId, title, csvDataFilename, arffDataFilename, missingValuePattern, numberOfMissingValues, numberOfInstances, user, attributes);
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

    public Analysis getAnalysis(long analysisId) {
        Analysis result = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT id, dataset_id, data_file, data_type, number_of_attributes, analyzed_by_grid_search, grid_search_analysis_in_progress FROM " + TABLE_ANALYSIS + " WHERE id = " + analysisId + ";";
            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            if (rs.next()) {
                //Retrieve by column name
                Dataset dataset = getDataset(rs.getLong("dataset_id"));
                String dataFile = rs.getString("data_file");
                String dataType = rs.getString("data_type");
                int numberOfAttributes = rs.getInt("number_of_attributes");
                boolean analyzedByGridSearch = rs.getBoolean("analyzed_by_grid_search");
                boolean gridSearchAnalysisInProgress = rs.getBoolean("grid_search_analysis_in_progress");
                result = new Analysis(analysisId, dataset, dataFile, dataType, numberOfAttributes, analyzedByGridSearch, gridSearchAnalysisInProgress);
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

    public User getUser(long userId) {
        User user = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT email FROM " + TABLE_USER + " WHERE id = " + userId + ";";
            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            if (rs.next()) {
                //Retrieve by column name
                String email = rs.getString("email");
                user = new User(userId, email);
            }

            rs.close();
            statement.close();
            conn.close();
            return user;
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
        return user;
    }

    public void updateAnalysis(Analysis analysis) {
        Connection conn = DatabaseConnectionFactory.getConnection();
        PreparedStatement update = null;

        try {
            update = conn.prepareStatement("UPDATE " + TABLE_ANALYSIS + " SET dataset_id = ?, data_file = ?, number_of_attributes = ?, analyzed_by_grid_search = ?, grid_search_analysis_in_progress = ? WHERE id = ?");
            update.setLong(1, analysis.getDataset().getId());
            update.setString(2, analysis.getDataFile());
            update.setInt(3, analysis.getNumberOfAttributes());
            update.setBoolean(4, analysis.isAnalyzedByGridSearch());
            update.setBoolean(5, analysis.isGridSearchAnalysisInProgress());
            update.setLong(6, analysis.getId());

            update.executeUpdate();

            update.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (update != null)
                    update.close();
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
    }

    public AnalysisResult defaultKnnResultForAnalysis(Analysis analysis) {
        AnalysisResult result = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT rmse FROM " + TABLE_ANALYSIS_RESULT + " WHERE analysis_id = " + analysis.getId() +
                    " and class = '" + CLASS_WINSTON_KNN_RESULT + "' and k = " + KnnModel.DEFAULT_KNN_PARAMETER_K
                    + ";";
            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            if (rs.next()) {
                //Retrieve by column name
                double rmse = rs.getDouble("rmse");
                result = new KnnResult(analysis.getId(), rmse, KnnModel.DEFAULT_KNN_PARAMETER_K);
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

    public AnalysisResult defaultDecisionTreeResultForAnalysis(Analysis analysis) {
        AnalysisResult result = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            int unpruned = DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED ? 1 : 0;
            String query = "SELECT rmse FROM " + TABLE_ANALYSIS_RESULT + " WHERE analysis_id = " + analysis.getId()
                    + " and class = '" + CLASS_WINSTON_DECISION_TREE_RESULT + "' and confidence_factor = "
                    + DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_PRUNING + " and minimum_number_of_instances_per_leaf = "
                    + DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES + " and unpruned = "
                    + unpruned + ";";
            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            if (rs.next()) {
                //Retrieve by column name
                double rmse = rs.getDouble("rmse");
                result = new DecisionTreeResult(analysis.getId(), rmse, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_PRUNING, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_MIN_NUMBER_OF_INSTANCES, DecisionTreeModel.DEFAULT_DECISION_TREE_PARAMETER_UNPRUNED);
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

    public AnalysisResult defaultLogisticRegressionResultForAnalysis(Analysis analysis) {
        AnalysisResult result = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT rmse FROM " + TABLE_ANALYSIS_RESULT + " WHERE analysis_id = " + analysis.getId()
                    + " and class = '" + CLASS_WINSTON_LOGISTIC_REGRESSION_RESULT + "' and ridge = "
                    + LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE + " and maximum_number_of_iterations = "
                    + LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_MAXIMUM_NUMBER_OF_ITERATIONS
                    + ";";
            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            if (rs.next()) {
                //Retrieve by column name
                double rmse = rs.getDouble("rmse");
                result = new LogisticRegressionResult(analysis.getId(), rmse, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_RIDGE, LogisticRegressionModel.DEFAULT_LOGISTIC_REGRESSION_PARAMETER_MAXIMUM_NUMBER_OF_ITERATIONS);
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

    public AnalysisResult defaultSvmResultForAnalysis(Analysis analysis) {
        AnalysisResult result = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT rmse FROM " + TABLE_ANALYSIS_RESULT + " WHERE analysis_id = " + analysis.getId()
                    + " and class = '" + CLASS_WINSTON_SVM_RESULT + "' and kernel = '"
                    + SvmModel.DEFAULT_SVM_PARAMETER_KERNEL + "' and complexity_constant = "
                    + SvmModel.DEFAULT_SVM_PARAMETER_C_COMPLEXITY_CONSTANT + " and gamma = "
                    + SvmModel.DEFAULT_SVM_PARAMETER_GAMMA + ";";
            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            if (rs.next()) {
                //Retrieve by column name
                double rmse = rs.getDouble("rmse");
                result = new SvmResult(analysis.getId(), rmse, SvmModel.DEFAULT_SVM_PARAMETER_KERNEL, SvmModel.DEFAULT_SVM_PARAMETER_C_COMPLEXITY_CONSTANT, SvmModel.DEFAULT_SVM_PARAMETER_GAMMA);
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

    public AnalysisResult bestMethodForAnalysis(Analysis analysis) {
        AnalysisResult result = null;

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT * FROM " + TABLE_ANALYSIS_RESULT + " WHERE analysis_id = " + analysis.getId()
                    + " and rmse = (SELECT min(rmse) FROM " + TABLE_ANALYSIS_RESULT + " WHERE analysis_id = " + analysis.getId() + ");";

            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            if (rs.next()) {
                //Retrieve by column name
                String resultClass = rs.getString("class");
                double rmse = rs.getDouble("rmse");

                if (CLASS_WINSTON_KNN_RESULT.equals(resultClass)) {
                    int k = rs.getInt("k");
                    result = new KnnResult(analysis.getId(), rmse, k);
                } else if (CLASS_WINSTON_LOGISTIC_REGRESSION_RESULT.equals(resultClass)) {
                    double ridge = rs.getDouble("ridge");
                    int maximumNumberOfIterations = rs.getInt("maximum_number_of_iterations");
                    result = new LogisticRegressionResult(analysis.getId(), rmse, ridge, maximumNumberOfIterations);
                } else if (CLASS_WINSTON_DECISION_TREE_RESULT.equals(resultClass)) {
                    double confidenceFactor = rs.getDouble("confidence_factor");
                    int minimumNumberOfInstancesPerLeaf = rs.getInt("minimum_number_of_instances_per_leaf");
                    boolean unpruned = rs.getBoolean("unpruned");
                    result = new DecisionTreeResult(analysis.getId(), rmse, confidenceFactor, minimumNumberOfInstancesPerLeaf, unpruned);
                } else if (CLASS_WINSTON_SVM_RESULT.equals(resultClass)) {
                    String kernel = rs.getString("kernel");
                    double complexityConstant = rs.getDouble("complexity_constant");
                    double gamma = rs.getDouble("gamma");
                    result = new SvmResult(analysis.getId(), rmse, kernel, complexityConstant, gamma);
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

    public List<Analysis> analysesAnalyzedByGridSearch() {
        List<Analysis> analyzedByGridSearch = new ArrayList<Analysis>();

        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();

            String query = "SELECT id, dataset_id, data_file, data_type, number_of_attributes, grid_search_analysis_in_progress FROM " + TABLE_ANALYSIS + " WHERE analyzed_by_grid_search = 1;";
            ResultSet rs = statement.executeQuery(query);

            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                long id = rs.getLong("id");
                long datasetId = rs.getLong("dataset_id");
                Dataset dataset = getDataset(datasetId);
                String dataFile = rs.getString("data_file");
                String dataType = rs.getString("data_type");
                int numberOfAttributes = rs.getInt("number_of_attributes");
                boolean gridSearchAnalysisInProgress = rs.getBoolean("grid_search_analysis_in_progress");

                Analysis analyzed = new Analysis(id, dataset, dataFile, dataType, numberOfAttributes, true, gridSearchAnalysisInProgress);
                analyzedByGridSearch.add(analyzed);
            }

            rs.close();
            statement.close();
            conn.close();
            return analyzedByGridSearch;
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
        return analyzedByGridSearch;
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

    public void saveAnalysisResult(AnalysisResult toSave) {
        Connection conn = DatabaseConnectionFactory.getConnection();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String insertQuery = null;

            if (toSave instanceof KnnResult) {
                KnnResult knn = (KnnResult) toSave;
                insertQuery = "INSERT INTO " + TABLE_ANALYSIS_RESULT
                        + "(analysis_id, rmse, k, class, version) " + "VALUES"
                        + " (" + knn.getAnalysis_id() + ", " + knn.getRmse() + ", " + knn.getK() + ",'" + CLASS_WINSTON_KNN_RESULT + "', " + DATA_VERSION + ")";
            } else if (toSave instanceof LogisticRegressionResult) {
                LogisticRegressionResult logisticRegression = (LogisticRegressionResult) toSave;
                insertQuery = "INSERT INTO " + TABLE_ANALYSIS_RESULT
                        + "(analysis_id, rmse, ridge, maximum_number_of_iterations, class, version) " + "VALUES"
                        + " (" + logisticRegression.getAnalysis_id() + ", " + logisticRegression.getRmse() + ", "
                        + logisticRegression.getRidge() + ", " + logisticRegression.getMaximumNumberOfIterations() + ",'" + CLASS_WINSTON_LOGISTIC_REGRESSION_RESULT + "', " + DATA_VERSION + ")";
            } else if (toSave instanceof DecisionTreeResult) {
                DecisionTreeResult decisionTree = (DecisionTreeResult) toSave;
                int unpruned = decisionTree.isUnpruned() ? 1 : 0;
                insertQuery = "INSERT INTO " + TABLE_ANALYSIS_RESULT
                        + "(analysis_id, rmse, confidence_factor, minimum_number_of_instances_per_leaf, unpruned, class, version) " + "VALUES"
                        + " (" + decisionTree.getAnalysis_id() + ", " + decisionTree.getRmse() + ", "
                        + decisionTree.getConfidenceFactor() + ", " + decisionTree.getMinimumNumberOfInstancesPerLeaf() + ", " + unpruned + ",'"
                        + CLASS_WINSTON_DECISION_TREE_RESULT + "', " + DATA_VERSION + ")";
            } else if (toSave instanceof SvmResult) {
                SvmResult svm = (SvmResult) toSave;
                insertQuery = "INSERT INTO " + TABLE_ANALYSIS_RESULT
                        + "(analysis_id, rmse, kernel, complexity_constant, gamma, class, version) " + "VALUES"
                        + " (" + svm.getAnalysis_id() + ", " + svm.getRmse() + ", '"
                        + svm.getKernel() + "', " + svm.getComplexityConstant() + ", " + svm.getGamma() + ",'"
                        + CLASS_WINSTON_SVM_RESULT + "', " + DATA_VERSION + ")";
            }

            if (insertQuery != null) {
                statement.executeUpdate(insertQuery);
            }

            statement.close();
            conn.close();
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

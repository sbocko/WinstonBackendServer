package sk.upjs.winston.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan on 2/15/15.
 */
public class Analysis {
    public static final String DATA_TYPE_INTEGER = "INT";
    public static final String DATA_TYPE_REAL = "REAL";
    public static final String DATA_TYPE_CATEGORICAL = "CAT";
    public static final String DATA_TYPE_MULTIVARIATE = "MULT";

    public static final String TASK_CLASSIFICATION = "CLASSIFICATION";
    public static final String TASK_REGRESSION = "REGRESSION";
    public static final String TASK_PATTERN_MINING = "PATTERN";

    private long id;
    private Dataset dataset;
    private String task;
    private String dataFile;
    private String dataType;
    private int numberOfAttributes;
    private boolean analyzedByGridSearch = false;
    private boolean gridSearchAnalysisInProgress = false;
//    private List<AnalysisResult> results = new ArrayList<AnalysisResult>();

    public Analysis(Dataset dataset, String task, String dataFile, String dataType, int numberOfAttributes) {
        this.dataset = dataset;
        this.task = task;
        this.dataFile = dataFile;
        this.dataType = dataType;
        this.numberOfAttributes = numberOfAttributes;
    }

    public Analysis(long id, Dataset dataset, String task, String dataFile, String dataType, int numberOfAttributes, boolean analyzedByGridSearch, boolean gridSearchAnalysisInProgress) {
        this.id = id;
        this.dataset = dataset;
        this.task = task;
        this.dataFile = dataFile;
        this.dataType = dataType;
        this.numberOfAttributes = numberOfAttributes;
        this.analyzedByGridSearch = analyzedByGridSearch;
        this.gridSearchAnalysisInProgress = gridSearchAnalysisInProgress;
    }

    public int getNumberOfInstances() {
        return this.dataset.getNumberOfInstances();
    }

    public int getNumberOfMissingValues() {
        return this.dataset.getNumberOfMissingValues();
    }

    public String getMissingValuePattern() {
        return this.dataset.getMissingValuePattern();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getNumberOfAttributes() {
        return numberOfAttributes;
    }

    public void setNumberOfAttributes(int numberOfAttributes) {
        this.numberOfAttributes = numberOfAttributes;
    }

    public boolean isAnalyzedByGridSearch() {
        return analyzedByGridSearch;
    }

    public void setAnalyzedByGridSearch(boolean analyzedByGridSearch) {
        this.analyzedByGridSearch = analyzedByGridSearch;
    }

    public boolean isGridSearchAnalysisInProgress() {
        return gridSearchAnalysisInProgress;
    }

    public void setGridSearchAnalysisInProgress(boolean gridSearchAnalysisInProgress) {
        this.gridSearchAnalysisInProgress = gridSearchAnalysisInProgress;
    }

    @Override
    public String toString() {
        return "Analysis{" +
                "id=" + id +
                ", dataset=" + dataset +
                ", task='" + task + '\'' +
                ", dataFile='" + dataFile + '\'' +
                ", dataType='" + dataType + '\'' +
                ", numberOfAttributes=" + numberOfAttributes +
                ", analyzedByGridSearch=" + analyzedByGridSearch +
                ", gridSearchAnalysisInProgress=" + gridSearchAnalysisInProgress +
                '}';
    }
}

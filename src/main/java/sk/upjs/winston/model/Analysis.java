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

    private long id;
    private Dataset dataset;
    private String dataFile;
    private String dataType;
    private int numberOfAttributes;
    private boolean analyzedByGridSearch = false;
    private boolean gridSearchAnalysisInProgress = false;
    private List<AnalysisResult> results = new ArrayList<AnalysisResult>();

    public Analysis(Dataset dataset, String dataFile, String dataType, int numberOfAttributes) {
        this.dataset = dataset;
        this.dataFile = dataFile;
        this.dataType = dataType;
        this.numberOfAttributes = numberOfAttributes;
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

    public List<AnalysisResult> getResults() {
        return results;
    }

    public void setResults(List<AnalysisResult> results) {
        this.results = results;
    }
}

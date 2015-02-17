package sk.upjs.winston.model;

import java.util.List;

/**
 * Created by stefan on 2/14/15.
 */
public class Dataset {
    public static final String DEFAULT_DELIMITER = ",";

    private Long id;
    private String title;
    private String dataFile;
    private String arffDataFile;
    private String missingValuePattern;
    private int numberOfMissingValues;
    private int numberOfInstances;
    private User user;
    private List<Attribute> attributes;

    public Dataset(Long id, String title, String dataFile, String arffDataFile, String missingValuePattern, int numberOfMissingValues, int numberOfInstances, User user, List<Attribute> attributes) {
        this.id = id;
        this.title = title;
        this.dataFile = dataFile;
        this.arffDataFile = arffDataFile;
        this.missingValuePattern = missingValuePattern;
        this.numberOfMissingValues = numberOfMissingValues;
        this.numberOfInstances = numberOfInstances;
        this.user = user;
        this.attributes = attributes;
    }

    public static String getDefaultDelimiter() {
        return DEFAULT_DELIMITER;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getArffDataFile() {
        return arffDataFile;
    }

    public void setArffDataFile(String arffDataFile) {
        this.arffDataFile = arffDataFile;
    }

    public String getMissingValuePattern() {
        return missingValuePattern;
    }

    public void setMissingValuePattern(String missingValuePattern) {
        this.missingValuePattern = missingValuePattern;
    }

    public int getNumberOfMissingValues() {
        return numberOfMissingValues;
    }

    public void setNumberOfMissingValues(int numberOfMissingValues) {
        this.numberOfMissingValues = numberOfMissingValues;
    }

    public int getNumberOfInstances() {
        return numberOfInstances;
    }

    public void setNumberOfInstances(int numberOfInstances) {
        this.numberOfInstances = numberOfInstances;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", dataFile='" + dataFile + '\'' +
                ", arffDataFile='" + arffDataFile + '\'' +
                ", missingValuePattern='" + missingValuePattern + '\'' +
                ", numberOfMissingValues=" + numberOfMissingValues +
                ", numberOfInstances=" + numberOfInstances +
                ", # of attributes=" + attributes.size() +
                '}';
    }
}

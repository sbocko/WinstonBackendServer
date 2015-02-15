package sk.upjs.winston.model;

/**
 * Created by stefan on 2/14/15.
 */
public class Attribute implements Comparable<Attribute> {
    private long id;
    private String title;
    private int numberOfMissingValues;
    //the position is zero based
    private int positionInDataFile;

    public Attribute(long id, String title, int numberOfMissingValues, int positionInDataFile) {
        this.id = id;
        this.title = title;
        this.numberOfMissingValues = numberOfMissingValues;
        this.positionInDataFile = positionInDataFile;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPositionInDataFile(int positionInDataFile) {
        this.positionInDataFile = positionInDataFile;
    }

    public int getNumberOfMissingValues() {
        return numberOfMissingValues;
    }

    public void setNumberOfMissingValues(int numberOfMissingValues) {
        this.numberOfMissingValues = numberOfMissingValues;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPositionInDataFile() {
        return positionInDataFile;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", numberOfMissingValues=" + numberOfMissingValues +
                ", positionInDataFile=" + positionInDataFile +
                '}';
    }

    @Override
    public int compareTo(Attribute o) {
        if (this.getId() == o.getId())
            return 0;
        return -1;
    }
}

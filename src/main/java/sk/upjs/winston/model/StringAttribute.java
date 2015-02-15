package sk.upjs.winston.model;

/**
 * Created by stefan on 2/15/15.
 */
public class StringAttribute extends Attribute{
    private int numberOfDistinctValues;

    public StringAttribute(long id, String title, int numberOfMissingValues, int positionInDataFile, int numberOfDistinctValues) {
        super(id, title, numberOfMissingValues, positionInDataFile);
        this.numberOfDistinctValues = numberOfDistinctValues;
    }

    public int getNumberOfDistinctValues() {
        return numberOfDistinctValues;
    }

    public void setNumberOfDistinctValues(int numberOfDistinctValues) {
        this.numberOfDistinctValues = numberOfDistinctValues;
    }
}

package sk.upjs.winston.model;

/**
 * Created by stefan on 2/15/15.
 */
public class BooleanAttribute extends Attribute{
    private int numberOfTrueValues;
    private int numberOfFalseValues;

    public BooleanAttribute(long id, String title, int numberOfMissingValues, int positionInDataFile, int numberOfTrueValues, int numberOfFalseValues) {
        super(id, title, numberOfMissingValues, positionInDataFile);
        this.numberOfTrueValues = numberOfTrueValues;
        this.numberOfFalseValues = numberOfFalseValues;
    }

    public int getNumberOfTrueValues() {
        return numberOfTrueValues;
    }

    public void setNumberOfTrueValues(int numberOfTrueValues) {
        this.numberOfTrueValues = numberOfTrueValues;
    }

    public int getNumberOfFalseValues() {
        return numberOfFalseValues;
    }

    public void setNumberOfFalseValues(int numberOfFalseValues) {
        this.numberOfFalseValues = numberOfFalseValues;
    }
}

package sk.upjs.winston.model;

/**
 * Created by stefan on 2/15/15.
 */
public class NumericAttribute extends Attribute {
    private double average;
    private double minimum;
    private double maximum;
    private int numberOfDistinctValues;

    public NumericAttribute(long id, String title, int numberOfMissingValues, int positionInDataFile, double average, double minimum, double maximum, int numberOfDistinctValues) {
        super(id, title, numberOfMissingValues, positionInDataFile);
        this.average = average;
        this.minimum = minimum;
        this.maximum = maximum;
        this.numberOfDistinctValues = numberOfDistinctValues;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public int getNumberOfDistinctValues() {
        return numberOfDistinctValues;
    }

    public void setNumberOfDistinctValues(int numberOfDistinctValues) {
        this.numberOfDistinctValues = numberOfDistinctValues;
    }
}

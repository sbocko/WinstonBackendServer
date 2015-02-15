package sk.upjs.winston.computation;

import sk.upjs.winston.model.Attribute;
import sk.upjs.winston.model.BooleanAttribute;
import sk.upjs.winston.model.NumericAttribute;
import sk.upjs.winston.model.StringAttribute;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stefan on 2/15/15.
 */
public class MissingValuesHandler {
    public static final double MISSING_VALUE_CODE = Double.NaN;

    public Instances replaceMissingValuesByMeanInNumericAttribute(Instances original, Instances toReplace, NumericAttribute datasetAttribute) {
        for (int i = 0; i < toReplace.numInstances(); i++) {
            int positionOfAttribute = datasetAttribute.getPositionInDataFile();
            double instanceValue = original.instance(i).value(positionOfAttribute);
            if (instanceValue == MISSING_VALUE_CODE) {
                toReplace.instance(i).setValue(positionOfAttribute, datasetAttribute.getAverage());
            }
        }
        return toReplace;
    }

    public Instances replaceMissingValuesByMajorValueInNumericAttribute(Instances original, Instances toReplace, NumericAttribute datasetAttribute) {
        double newValue = getMajorValueForAttribute(toReplace, datasetAttribute);

        for (int i = 0; i < toReplace.numInstances(); i++) {
            int positionOfAttribute = datasetAttribute.getPositionInDataFile();
            double instanceValue = original.instance(i).value(positionOfAttribute);
            if (instanceValue == MISSING_VALUE_CODE) {
                toReplace.instance(i).setValue(positionOfAttribute, newValue);
            }
        }
        return toReplace;
    }

    public Instances replaceMissingValuesByMajorValueInStringAttribute(Instances original, Instances toReplace, StringAttribute datasetAttribute) {
        double newValue = getMajorValueForAttribute(toReplace, datasetAttribute);

        for (int i = 0; i < toReplace.numInstances(); i++) {
            int positionOfAttribute = datasetAttribute.getPositionInDataFile();
            double instanceValue = original.instance(i).value(positionOfAttribute);
            if (instanceValue == MISSING_VALUE_CODE) {
                toReplace.instance(i).setValue(positionOfAttribute, newValue);
            }
        }
        return toReplace;
    }

    public Instances replaceMissingValuesByMajorInBooleanAttribute(Instances original, Instances toReplace, BooleanAttribute datasetAttribute) {
        double newValue = getMajorValueForAttribute(toReplace, datasetAttribute);

        for (int i = 0; i < toReplace.numInstances(); i++) {
            int positionOfAttribute = datasetAttribute.getPositionInDataFile();
            double instanceValue = original.instance(i).value(positionOfAttribute);
            if (instanceValue == MISSING_VALUE_CODE) {
                toReplace.instance(i).setValue(positionOfAttribute, newValue);
            }
        }
        return toReplace;
    }

    /**
     * HELPER METHODS
     */

    private double getMajorValueForAttribute(Instances instances, Attribute datasetAttribute) {
        Map<Double, Integer> occurences = new HashMap<Double, Integer>();
        for (int i = 0; i < instances.numInstances(); i++) {
            double value = instances.instance(i).value(datasetAttribute.getPositionInDataFile());
            if (value == MISSING_VALUE_CODE) {
                continue;
            }
            if (occurences.containsKey(value)) {
                occurences.put(value, occurences.get(value) + 1);
            } else {
                occurences.put(value, 1);
            }
        }
        return getMostCommonValue(occurences);
    }

    private double getMostCommonValue(Map<Double, Integer> map) {
        int maxCount = Integer.MIN_VALUE;
        double result = MISSING_VALUE_CODE;
        for (Map.Entry<Double, Integer> entry : map.entrySet()) {
            if (maxCount < entry.getValue()) {
                maxCount = entry.getValue();
                result = entry.getKey();
            }
        }
        return result;
    }
}

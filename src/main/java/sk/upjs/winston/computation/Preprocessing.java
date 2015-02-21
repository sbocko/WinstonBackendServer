package sk.upjs.winston.computation;

import sk.upjs.winston.helper.Mailer;
import sk.upjs.winston.model.Attribute;
import sk.upjs.winston.model.Dataset;
import sk.upjs.winston.model.NumericAttribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.PartitionedMultiFilter;
import weka.filters.unsupervised.attribute.Standardize;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by stefan on 2/15/15.
 */
public class Preprocessing {
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String BINARIZATION_DELIMITER = "-";

    public Instances zeroOneNormalize(Instances toProcess, NumericAttribute datasetAttribute) {
        Normalize normalization01 = new Normalize();
        return useFilterAtAttribute(toProcess, datasetAttribute, normalization01);
    }

    public Instances standardize(Instances toProcess, NumericAttribute datasetAttribute) {
        Standardize standardization = new Standardize();
        return useFilterAtAttribute(toProcess, datasetAttribute, standardization);
    }

    public Instances discretizeByEqualWidth(Instances toProcess, NumericAttribute datasetAttribute) {
        Discretize equalWidthDiscretization = new Discretize();
        equalWidthDiscretization.setUseEqualFrequency(false);
        return useFilterAtAttribute(toProcess, datasetAttribute, equalWidthDiscretization);
    }

    public Instances discretizeByEqualFrequency(Instances toProcess, NumericAttribute datasetAttribute) {
        Discretize equalFrequencyDiscretization = new Discretize();
        equalFrequencyDiscretization.setUseEqualFrequency(true);
        return useFilterAtAttribute(toProcess, datasetAttribute, equalFrequencyDiscretization);
    }

    public Instances binarize(Dataset dataset, Instances toBinarize, Map<Attribute, Boolean> attributesToSplit) {
        int numberOfAttributes = toBinarize.numAttributes();
        Set<String> attributeNames = new HashSet<String>(numberOfAttributes);
        for (Map.Entry<Attribute, Boolean> entry : attributesToSplit.entrySet()) {
            if (entry.getValue()){
                attributeNames.add(entry.getKey().getTitle());
            }
        }

        for (int attributePosition = numberOfAttributes - 1; attributePosition >= 0; attributePosition--) {
            // iterate attributes from the end

            // split attribute at attributePosition
            weka.core.Attribute toSplit = toBinarize.attribute(attributePosition);

            boolean split = false;
            for (String name : attributeNames) {
                if (toSplit.name().contains(name)) {
                    split = true;
                    break;
                }
            }
            if (!split) {
                // continue if attribute doesnt have to be splitted
                continue;
            }

            if (toSplit.isNumeric()) {
                //numeric attribute

                // get number of distinct values and assign them position for new attribute
                Map<Double, Integer> attributeValuePosition = new LinkedHashMap<Double, Integer>();
                for (int j = 0; j < toBinarize.numInstances(); j++) {
                    Instance actual = toBinarize.instance(j);
                    double instanceValue = actual.value(attributePosition);
                    if (!attributeValuePosition.containsKey(instanceValue)) {
                        attributeValuePosition.put(instanceValue, 1 + attributePosition + attributeValuePosition.size());
                    }
                }

                FastVector insertedAttributeValues = new FastVector();
                insertedAttributeValues.addElement(TRUE);
                insertedAttributeValues.addElement(FALSE);
                // create new attributes
                for (Map.Entry<Double, Integer> entry : attributeValuePosition.entrySet()) {
                    String attributeName = toSplit.name() + BINARIZATION_DELIMITER + entry.getKey();
                    weka.core.Attribute toInsert = new weka.core.Attribute(attributeName, insertedAttributeValues);
                    toBinarize.insertAttributeAt(toInsert, entry.getValue());
                }
                // copy data
                for (int i = 0; i < toBinarize.numInstances(); i++) {
                    Instance actual = toBinarize.instance(i);
                    for (Map.Entry<Double, Integer> entry : attributeValuePosition.entrySet()) {
                        if (actual.value(attributePosition) == entry.getKey()) {
                            actual.setValue(entry.getValue(), TRUE);
                        } else {
                            actual.setValue(entry.getValue(), FALSE);
                        }
                    }
                }

                // remove old attribute
                toBinarize.deleteAttributeAt(attributePosition);
            } else {
                // nominal attribute

                // get number of distinct values and assign them position for new attribute
                Map<String, Integer> attributeValuePosition = new LinkedHashMap<String, Integer>();
                for (int j = 0; j < toBinarize.numInstances(); j++) {
                    Instance actual = toBinarize.instance(j);
                    String instanceValue = actual.stringValue(attributePosition);
                    if (!attributeValuePosition.containsKey(instanceValue)) {
                        attributeValuePosition.put(instanceValue, 1 + attributePosition + attributeValuePosition.size());
                    }
                }

                FastVector insertedAttributeValues = new FastVector();
                insertedAttributeValues.addElement(TRUE);
                insertedAttributeValues.addElement(FALSE);
                // create new attributes
                for (Map.Entry<String, Integer> entry : attributeValuePosition.entrySet()) {
                    String attributeName = toSplit.name() + BINARIZATION_DELIMITER + entry.getKey();
                    weka.core.Attribute toInsert = new weka.core.Attribute(attributeName, insertedAttributeValues);
                    toBinarize.insertAttributeAt(toInsert, entry.getValue());
                }
                // copy data
                for (int i = 0; i < toBinarize.numInstances(); i++) {
                    Instance actual = toBinarize.instance(i);
                    for (Map.Entry<String, Integer> entry : attributeValuePosition.entrySet()) {
                        if (actual.stringValue(attributePosition).equals(entry.getKey())) {
                            actual.setValue(entry.getValue(), TRUE);
                        } else {
                            actual.setValue(entry.getValue(), FALSE);
                        }
                    }
                }

                // remove old attribute
                toBinarize.deleteAttributeAt(attributePosition);
            }
        }
        return toBinarize;
    }

    /**
     * HELPER METHODS
     */

    private Instances useFilterAtAttribute(Instances toFilter, Attribute datasetAttribute, Filter preprocessingMethod) {
        Filter[] toApply = new Filter[1];
        toApply[0] = preprocessingMethod;
        PartitionedMultiFilter filter = new PartitionedMultiFilter();
        filter.setFilters(toApply);

        Range[] ranges = new Range[1];
        int attributePositionIndex = datasetAttribute.getPositionInDataFile() + 1;
        ranges[0] = new Range(attributePositionIndex + "");
        filter.setRanges(ranges);

        filter.setRemoveUnused(false);
        try {
            filter.setInputFormat(toFilter);
            toFilter = Filter.useFilter(toFilter, filter);
            return toFilter;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

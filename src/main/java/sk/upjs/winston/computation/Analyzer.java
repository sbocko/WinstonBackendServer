package sk.upjs.winston.computation;

import org.apache.commons.io.FileUtils;
import sk.upjs.winston.database.DatabaseManager;
import sk.upjs.winston.helper.FileManipulationUtilities;
import sk.upjs.winston.model.*;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan on 2/15/15.
 */
public class Analyzer {
    private static final String CONTAINS_ATTRIBUTE_VALUE = "1";
    private static final String DOES_NOT_CONTAIN_ATTRIBUTE_VALUE = "0";

    private Preprocessing preprocessing = new Preprocessing();
    private Modelling modelling = new Modelling();
    private MissingValuesHandler missingValuesHandler = new MissingValuesHandler();
    private DatabaseManager databaseManager = new DatabaseManager();

    public List<Analysis> generateAnalyzes(Dataset dataset, File arffData, Map<Attribute, Boolean> attributesToSplit, Attribute target) {
        List<Analysis> analyzes = new ArrayList<Analysis>();

        try {
            List<Instances> replaced = replaceMissingValues(dataset, arffData);
            for (Instances instances : replaced) {
                instances.setClassIndex(target.getPositionInDataFile());
            }

            List<Instances> preprocessed = generatePreprocessedDataInstances(dataset, replaced, attributesToSplit, target);


            for (Instances instances : preprocessed) {
                String fileName = saveInstancesToFiles(instances, dataset.getTitle());
                analyzes.add(createAnalysis(dataset, fileName, instances));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return analyzes;
    }

    public Analysis createAnalysis(Dataset dataset, String dataFileName, Instances instances) {
        String dataType = getDataTypeForData(instances);

        int numberOfAttributes = instances.numAttributes();
        Analysis analysis = new Analysis(dataset, dataFileName, dataType, numberOfAttributes);
        long analysisId = databaseManager.saveAnalysis(analysis);
        analysis.setId(analysisId);

        try {
            modelling.performAnalysisWithDefaultHyperparameters(analysis);
            modelling.performRecommendedDataMiningMethodForAnalysis(analysis);
        }catch (IOException e){
            e.printStackTrace();
        }

//        modellingService.performGridsearchAnalysisForFile(analysis)

        return analysis;
    }

    /**
     * HELPER METHODS
     */

    private List<Instances> generatePreprocessedDataInstances(Dataset dataset, List<Instances> toProcess, Map<Attribute, Boolean> attributesToSplit, Attribute target) {
        for (Attribute datasetAttribute : dataset.getAttributes()) {
            if (attributesToSplit.get(datasetAttribute)) {
                continue;
            }

            List<Instances> generated = new ArrayList<Instances>();

            for (Instances actual : toProcess) {
                if (datasetAttribute instanceof NumericAttribute) {
                    Instances zScoreNormalized = preprocessing.zeroOneNormalize(actual, (NumericAttribute) datasetAttribute);
                    generated.add(zScoreNormalized);
                    Instances standardized = preprocessing.standardize(actual, (NumericAttribute) datasetAttribute);
                    generated.add(standardized);
                    Instances discretizedByEqualWidth = preprocessing.discretizeByEqualWidth(actual, (NumericAttribute) datasetAttribute);
                    generated.add(discretizedByEqualWidth);
                    Instances discretizedByEqualFrequency = preprocessing.discretizeByEqualFrequency(actual, (NumericAttribute) datasetAttribute);
                    generated.add(discretizedByEqualFrequency);
                } else if (datasetAttribute instanceof StringAttribute) {
                    // nothing to do
                } else if (datasetAttribute instanceof BooleanAttribute) {
                    // nothing to do
                }
            }

            if (generated != null && generated.size() > 0) {
                toProcess.addAll(generated);
            }
        }

        addStringAttributesToBinarization(attributesToSplit, target.getPositionInDataFile());
        List<Instances> binarized = generateBinarizedDataInstances(dataset, toProcess, attributesToSplit);
        return binarized;
//        return toProcess
    }

    private void addStringAttributesToBinarization(Map<Attribute, Boolean> attributesToSplit, int targetAttributePosition) {
        for (Map.Entry<Attribute, Boolean> entry : attributesToSplit.entrySet()) {
            Attribute actual = entry.getKey();
            if (actual.getPositionInDataFile() != targetAttributePosition && actual instanceof StringAttribute) {
                entry.setValue(true);
            }
        }
    }

    private List<Instances> generateBinarizedDataInstances(Dataset dataset, List<Instances> toBinarize, Map<Attribute, Boolean> attributesToSplit) {
        List<Instances> binarized = new ArrayList<Instances>();
        for (Instances actual : toBinarize) {
            binarized.add(preprocessing.binarize(dataset, actual, attributesToSplit));
        }
        return binarized;
    }

    private String saveInstancesToFiles(Instances toSave, String datasetTitle) {
        try {
            File file = generateEmptyFileForDatasetAnalysis(datasetTitle);
            ArffSaver saver = new ArffSaver();
            saver.setInstances(toSave);
            saver.setFile(file);
            saver.writeBatch();
            return file.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File generateEmptyFileForDatasetAnalysis(String datasetTitle) throws IOException {
        int analysisNumber = 1;
        File file;
        String filepath;
        while (true) {
            filepath = FileManipulationUtilities.PREPARED_DATAFILES_DIRECTORY + "/" + datasetTitle + "-analysis_" + analysisNumber + ".arff";
            file = new File(filepath);
            if (!file.exists()) {
                break;
            }
            analysisNumber++;
        }

        file = FileManipulationUtilities.createFileForPath(filepath);
        return file;
    }

    private List<Instances> replaceMissingValues(Dataset dataset, File arff) throws IOException {
        BufferedReader r = new BufferedReader(
                new FileReader(arff));
        Instances original = new Instances(r);
        r.close();

        List<Instances> replaced = new ArrayList<Instances>();
        replaced.add(makeClone(original));

        for (Attribute datasetAttribute : dataset.getAttributes()) {
            if (datasetAttribute.getNumberOfMissingValues() == 0) {
                continue;
            }

            int positionInDataFile = datasetAttribute.getPositionInDataFile();
            weka.core.Attribute attribute = original.attribute(positionInDataFile);

            Instances toAdd = null;

            for (Instances actual : replaced) {
                //nahrad chybajuce hodnoty
                if (datasetAttribute instanceof NumericAttribute) {
                    missingValuesHandler.replaceMissingValuesByMeanInNumericAttribute(original, actual, (NumericAttribute) datasetAttribute);
                    Instances alternative = makeClone(actual);
                    alternative = missingValuesHandler.replaceMissingValuesByMajorValueInNumericAttribute(original, alternative, (NumericAttribute) datasetAttribute);
                    toAdd = alternative;
                } else if (datasetAttribute instanceof StringAttribute) {
                    missingValuesHandler.replaceMissingValuesByMajorValueInStringAttribute(original, actual, (StringAttribute) datasetAttribute);
                } else if (datasetAttribute instanceof BooleanAttribute) {
                    missingValuesHandler.replaceMissingValuesByMajorInBooleanAttribute(original, actual, (BooleanAttribute) datasetAttribute);
                }
            }

            if (toAdd != null) {
                replaced.add(toAdd);
            }
        }

        return replaced;
    }

    private Instances makeClone(Instances source) {
        Instances destination = new Instances(source);
        return destination;
    }

    private String getDataTypeForData(Instances instances) {
        boolean wasInt = false;
        boolean wasReal = false;
        boolean wasCategorical = false;

        int numberOfAttributes = instances.numAttributes();
        int CLASSIFICATION_ATTRIBUTE_POSITION = numberOfAttributes - 1;

        for (int i = 0; i < numberOfAttributes; i++) {
            AttributeStats attributeStats = instances.attributeStats(i);
            if (i != CLASSIFICATION_ATTRIBUTE_POSITION) {
                boolean isNominal = instances.attribute(i).isNominal();
                if (attributeStats.intCount != 0 && attributeStats.realCount == 0 && !isNominal) {
                    if (attributeStats.intCount == 2) {
                        wasCategorical = true;
                    } else {
                        wasInt = true;
                    }
                } else if (attributeStats.realCount != 0 && !isNominal) {
                    wasReal = true;
                } else {
                    wasCategorical = true;
                }
            }
        }

        if ((wasCategorical && wasInt) || (wasCategorical && wasReal) || (wasReal && wasInt)) {
            return Analysis.DATA_TYPE_MULTIVARIATE;
        } else if (wasCategorical) {
            return Analysis.DATA_TYPE_CATEGORICAL;
        } else if (wasReal) {
            return Analysis.DATA_TYPE_REAL;
        } else {
            return Analysis.DATA_TYPE_INTEGER;
        }
    }

    private String saveDataToFile(List<String[]> data, String datasetTitle) throws IOException {
        File file = generateEmptyFileForDatasetAnalysis(datasetTitle);

        for (int i = 0; i < data.get(0).length; i++) {
            String toWrite = "";
            for (int j = 0; j < data.size() - 1; j++) {
                toWrite += data.get(j)[i] + Dataset.DEFAULT_DELIMITER;
            }
            //last line without new line character
            if (i == data.get(0).length - 1) {
                toWrite += data.get(data.size() - 1)[i];
            } else {
                toWrite += data.get(data.size() - 1)[i] + "\n";
            }
            FileUtils.writeStringToFile(file, toWrite, true);
        }

        return file.getName();
    }

    private List<String[]> splitAttributes(String[][] datasetAttributesData, Map<Attribute, Boolean> attributesToSplit, Attribute target) {
        List<String[]> result = new ArrayList<String[]>();

        for (int i = 0; i < datasetAttributesData.length; i++) {
            for (Map.Entry<Attribute, Boolean> entry : attributesToSplit.entrySet()) {
                Attribute attr = entry.getKey();
                boolean split = entry.getValue();
                if (attr.getPositionInDataFile() == i) {
                    if (attr.getId() != target.getId()) {
                        result.addAll(getNewAttributeData(datasetAttributesData[i], attr, split));
                    }
                }
            }
        }
        result.addAll(getNewAttributeData(datasetAttributesData[target.getPositionInDataFile()], target, false));

        return result;
    }

    /*
     *  @param splitAttribute null if this attribute should not be devided
     */

    private List<String[]> getNewAttributeData(String[] attributeData, Attribute splitAttribute, boolean split) {
        List<String[]> result = new ArrayList<String[]>();

        if (!split) {
            String[] data = addAttributeTitleToData(attributeData, splitAttribute.getTitle());
            result.add(data);
            return result;
        }

        Map<String, Integer> attributeValuePositionMap = new HashMap<String, Integer>();
        for (int i = 0; i < attributeData.length; i++) {
            String dataValue = attributeData[i];
            if (attributeValuePositionMap.containsKey(dataValue)) {
                int dataValueIndex = attributeValuePositionMap.get(dataValue);
//                println "attrDataLength: ${attributeData.length}"
                result.get(dataValueIndex)[i + 1] = CONTAINS_ATTRIBUTE_VALUE;
            } else {
                String[] newColumn = initializeArrayWithValue(attributeData.length + 1, DOES_NOT_CONTAIN_ATTRIBUTE_VALUE);
                newColumn[0] = splitAttribute.getTitle() + ":" + dataValue;
                newColumn[i + 1] = CONTAINS_ATTRIBUTE_VALUE;
                result.add(newColumn);
                attributeValuePositionMap.put(dataValue, result.size() - 1);
            }
        }

        return result;
    }

    private String[] addAttributeTitleToData(String[] data, String title) {
        String[] result = new String[data.length + 1];
        result[0] = title;
        for (int i = 1; i < result.length; i++) {
            result[i] = data[i - 1];
        }
        return result;
    }

    private String[] initializeArrayWithValue(int length, String value) {
        String[] array = new String[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = value;
        }
        return array;
    }
}

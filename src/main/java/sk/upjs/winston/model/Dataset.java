package sk.upjs.winston.model;

import java.util.List;

/**
 * Created by stefan on 2/14/15.
 */
public class Dataset {
    private String title;
    private String dataFile;
    private String arffDataFile;
    private String missingValuePattern;
    private int numberOfMissingValues;
    private int numberOfInstances;
    private List<Attribute> attributes;
}

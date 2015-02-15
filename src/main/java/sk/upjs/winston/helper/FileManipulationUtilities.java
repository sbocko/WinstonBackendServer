package sk.upjs.winston.helper;

import java.io.File;
import java.io.IOException;

/**
 * Created by stefan on 2/15/15.
 */
public class FileManipulationUtilities {
    public static final String DATA_FILES_DIRECTORY = "datasets";
    public static final String PREPARED_DATAFILES_DIRECTORY = "prepared-datasets";

    public static File createFileForPath(String filepath) throws IOException {
        File created = new File(filepath);
        created.getParentFile().mkdirs();
        created.createNewFile();
        return created;
    }
}

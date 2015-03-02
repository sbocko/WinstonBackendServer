package sk.upjs.winston.server;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SelectedTag;

import java.io.*;
import java.util.Random;

/**
 * Created by stefan on 3/2/15.
 */
public class Test {

    public static void main(String[] args) throws Exception {
        File arffFile = new File("datasets/iris.arff");
        BufferedReader reader = new BufferedReader(
                new FileReader(arffFile));
        Instances dataInstances = new Instances(reader);
        reader.close();
        dataInstances.setClassIndex(dataInstances.numAttributes() - 1);

        PrintStream printStreamOriginal = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }));

        LibSVM svm = new LibSVM();
        svm.setCost(0.1);
        svm.setGamma(0.1);
        svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));

        svm.buildClassifier(dataInstances);
        Evaluation evaluation = new Evaluation(dataInstances);
        evaluation.crossValidateModel(svm, dataInstances, 5, new Random(1));

        System.setOut(printStreamOriginal);

        System.out.println(evaluation.toSummaryString());

    }
}

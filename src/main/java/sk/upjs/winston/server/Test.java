package sk.upjs.winston.server;

import sk.upjs.winston.computation.Preprocessing;
import sk.upjs.winston.model.BooleanAttribute;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stefan on 3/2/15.
 */
public class Test {

//    public static void main(String[] args) throws Exception {
//        File arffFile = new File("datasets/car.arff");
//        BufferedReader reader = new BufferedReader(
//                new FileReader(arffFile));
//        Instances dataInstances = new Instances(reader);
//        reader.close();
//
//        Apriori apriori = new Apriori();
//        apriori.setNumRules(20);
//
//        apriori.buildAssociations(dataInstances);
//        AssociationRules associationRules = apriori.getAssociationRules();
//
//
//        for (AssociationRule associationRule : associationRules.getRules()) {
//            System.out.println(associationRule);
//        }
//
////        System.out.println(associationRules);
//
////        for (Object rules : allTheRules) {
////            if (rules == null) {
////                System.out.println("rules are null");
////                continue;
////            }
////            System.out.println(rules);
//////            for (int i = 0; i < rules.size(); i++) {
//////                Object rule = rules.elementAt(i);
//////                if (rule instanceof AprioriItemSet) {
//////                    AprioriItemSet aprioriItemSet = (AprioriItemSet) rule;
//////                    System.out.print(aprioriItemSet.getRevision() + ", ");
//////                } else if (rule instanceof Double) {
//////                    Double d = (Double) rule;
//////                    System.out.print(d + ", ");
//////                } else {
//////
//////                    System.out.print("unknown rule: " + rule);
//////                }
//////            }
////            System.out.println();
////        }
//
//    }

    public static void main(String[] args) throws Exception {
//        File arffFile = new File("datasets/iris(Tf06KTbDb).arff");
//        BufferedReader reader = new BufferedReader(
//                new FileReader(arffFile));
//        Instances dataInstances = new Instances(reader);
//        reader.close();
//
//
//        Map<Attribute, Boolean> toSplit = new HashMap<Attribute, Boolean>();
//        for (int i = 0; i < dataInstances.numAttributes(); i++) {
//            toSplit.put(dataInstances.attribute(i),true);
//        }
//
//        Preprocessing preprocessingStep = new Preprocessing();
//        preprocessingStep.binarize(null, dataInstances, toSplit);

//        PrintStream printStreamOriginal = System.out;
//        System.setOut(new PrintStream(new OutputStream() {
//            public void write(int b) {
//            }
//        }));

//        SimpleKMeans kmeans = new SimpleKMeans();
//
//
//// This is the important parameter to set
//        kmeans.setSeed(10);
//        kmeans.setPreserveInstancesOrder(true);
//        kmeans.setNumClusters(3);
//        kmeans.setInitializationMethod(new SelectedTag(SimpleKMeans.CANOPY, SimpleKMeans.TAGS_SELECTION));
//        kmeans.buildClusterer(dataInstances);
//
//// This array returns the cluster number (starting with 0) for each instance
//// The array has as many elements as the number of instances
//        int[] assignments = kmeans.getAssignments();
//        double[] clusterSizes = kmeans.getClusterSizes();
//        System.out.println(kmeans.getSquaredError());
//        Instances clusterCentroids = kmeans.getClusterCentroids();
//        System.out.println(clusterCentroids);
//        System.out.println("options: " + Arrays.toString(kmeans.getOptions()));
//        int i = 0;
//        System.out.println("SIZE: " + assignments.length);
//        for (double clusterNum : clusterSizes) {
//            System.out.printf("Instance %d -> Cluster %f\n", i, clusterNum);
//            i++;
//        }

//
//        dataInstances.setClassIndex(dataInstances.numAttributes() - 1);
//
//        LibSVM svm = new LibSVM();
//        svm.setCost(0.1);
//        svm.setGamma(0.1);
//        svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
//
//        svm.buildClassifier(dataInstances);
//        Evaluation evaluation = new Evaluation(dataInstances);
//        evaluation.crossValidateModel(svm, dataInstances, 5, new Random(1));
//
////        System.setOut(printStreamOriginal);
//
//
//        System.out.println(evaluation.toSummaryString());

    }
}

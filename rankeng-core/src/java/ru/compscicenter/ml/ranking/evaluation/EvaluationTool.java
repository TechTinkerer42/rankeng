package ru.compscicenter.ml.ranking.evaluation;

import ru.compscicenter.ml.ranking.data.DataSet;
import ru.compscicenter.ml.ranking.data.FeatureRow;
import ru.compscicenter.ml.ranking.trees.AdditiveTrees;
import ru.compscicenter.ml.ranking.utils.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   24.04.12
 */
public class EvaluationTool {

    private EvaluationTool() {
        // Do nothing
    }

    public static void evaluate(String message, AdditiveTrees predictor, DataSet testSet) {
        double[] predictions = new double[testSet.numberOfRows()];
        int index = 0;
        for (FeatureRow featureRow : testSet.getRowList()) {
            predictions[index] = predictor.predict(featureRow);
            index++;
        }
        evaluate(message, predictions, testSet);
    }

    public static void evaluate(String message, double[] predictions, DataSet testSet) {
        if (testSet.queries().isEmpty()) {
            System.out.println("Evaluation - Test set is empty");
        }

        String text = message + "DCG=" + Utils.calculateDCG(testSet, predictions);
//        try (PrintWriter writer = new PrintWriter(new FileWriter("data/log.txt", true))) {
//            writer.println(text);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.println(text);
    }
}

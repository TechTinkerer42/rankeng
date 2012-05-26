package ru.compscicenter.ranking.utils;

import org.apache.log4j.Logger;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.FeatureRow;
import ru.compscicenter.ranking.RegressionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   24.04.12
 */
public class Evaluator {

    private static final Logger logger = Logger.getLogger(Evaluator.class);

    private final DataSet testSet;

    public Evaluator(DataSet testSet) {
        this.testSet = testSet;
    }

    public void evaluate(String message, RegressionModel ensemble) {
        double[] predictions = new double[testSet.numberOfRows()];
        int index = 0;
        for (FeatureRow featureRow : testSet.getRowList()) {
            predictions[index] = ensemble.predict(featureRow);
            index++;
        }
        evaluate(message, predictions);
    }

    public void evaluate(String message, double[] predictions) {
        if (testSet.queries().isEmpty()) {
            logger.error("Evaluation - Test set is empty");
        }

        String text = message + "DCG=" + calculateDCG(testSet, predictions);
        logger.debug(text);
    }

    public static double calculateDCG(DataSet dataSet, double[] predictions) {
        double sum = 0.0;
        double size = 0;
        for (List<Integer> query : dataSet.queries()) {
            int i = 1;
            List<Pair<Integer, Double>> pairs = new ArrayList<>();
            for (int doc : query) {
                pairs.add(new Pair<>(doc, -predictions[doc]));
            }
            PairUtils.sortBySecond(pairs);
            for (Integer doc : PairUtils.firsts(pairs)) {
                sum += dataSet.relevanceAt(doc) / (Math.log(i) / Math.log(2) + 1);
                i++;
            }
            if (!query.isEmpty()) {
                size++;
            }
        }
        return sum / size;
    }
}

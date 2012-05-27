package ru.compscicenter.ranking.utils;

import org.apache.log4j.Logger;
import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date:   24.04.12
 */
public class Evaluator {

    private static final Logger logger = Logger.getLogger(Evaluator.class);

    private final DataSet testSet;
    private final Outputs testRelevance;

    public Evaluator(DataSet testSet, Outputs testRelevance) {
        this.testSet = testSet;
        this.testRelevance = testRelevance;
    }

    public void evaluate(String message, RegressionModel ensemble) {
        evaluate(message, RankingUtils.predictAll(testSet, ensemble));
    }

    public void evaluate(String message, Outputs predictions) {
        if (testSet.queries().isEmpty()) {
            logger.error("Evaluation - Test set is empty");
        }

        String text = message + "DCG=" + calculateDCG(testSet, testRelevance, predictions);
        logger.debug(text);
    }

    public static double calculateDCG(DataSet dataSet, Outputs relevance, Outputs predictions) {
        double sum = 0.0;
        double size = 0;
        for (Query query : dataSet.queries()) {
            int i = 1;
            List<Pair<Instance, Double>> pairs = new ArrayList<>();
            for (Instance instance : query.getInstances()) {
                pairs.add(new Pair<>(instance, -predictions.valueOf(instance)));
            }
            PairUtils.sortBySecond(pairs);
            for (Instance instance : PairUtils.firsts(pairs)) {
                sum += relevance.valueOf(instance) / (Math.log(i) / Math.log(2) + 1);
                i++;
            }
            if (!query.getInstances().isEmpty()) {
                size++;
            }
        }
        return sum / size;
    }
}

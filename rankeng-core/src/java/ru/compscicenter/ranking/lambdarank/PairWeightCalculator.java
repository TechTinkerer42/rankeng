package ru.compscicenter.ranking.lambdarank;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;
import ru.compscicenter.ranking.utils.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date:   25.04.12
 */
public class PairWeightCalculator {

    public PairWeights calculateWeights(DataSet dataSet, Outputs relevance) {
        Map<Pair<Instance, Instance>, Double> pairWeights = new HashMap<>();
        for (Query query : dataSet.queries()) {
            for (Instance i : query.getInstances()) {
                for (Instance j : query.getInstances()) {
                    double difference = calculateDifference(i, j, relevance);
                    pairWeights.put(new Pair<>(i, j), difference > 0 ? difference : 0);
                }
            }
        }
        return new PairWeights(pairWeights);
    }

    private double calculateDifference(Instance i1, Instance i2, Outputs relevance) {
        return relevance.valueOf(i1) - relevance.valueOf(i2);
    }
}

// TODO: we can try confusion weights
// Probability to change editors mark (first) to true mark (second)
/*
public static double[][] confusionProbabilities = new double[][]{
        {0, 0, 0.83},
        {0, 1, 0.15},
        {0, 2, 0.02},
        {1, 0, 0.3},
        {1, 1, 0.6},
        {1, 2, 0.09},
        {1, 3, 0.01},
        {2, 0, 0.06},
        {2, 1, 0.1},
        {2, 2, 0.77},
        {2, 3, 0.06},
        {2, 4, 0.01},
        {3, 0, 0.03},
        {3, 1, 0.03},
        {3, 2, 0.38},
        {3, 3, 0.47},
        {3, 4, 0.09},
        {4, 0, 0.03},
        {4, 1, 0.02},
        {4, 2, 0.04},
        {4, 3, 0.06},
        {4, 4, 0.85}
}; */

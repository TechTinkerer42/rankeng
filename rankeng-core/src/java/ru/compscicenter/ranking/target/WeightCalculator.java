package ru.compscicenter.ranking.target;

import ru.compscicenter.ranking.data.DataSet;

import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public class WeightCalculator {

    public double[][][] calculateWeights(DataSet dataSet) {
        double[][][] w = new double[dataSet.queries().size()][][];
        int queryIndex = 0;
        for (List<Integer> query : dataSet.queries()) {
            w[queryIndex] = new double[query.size()][query.size()];
            for (int i = 0; i < query.size(); i++) {
                for (int j = 0; j < query.size(); j++) {
                    double difference =
                            calculateDifference(query.get(i), query.get(j), dataSet);
                    w[queryIndex][i][j] = difference > 0 ? difference : 0;
                }
            }
            queryIndex++;
        }
        return w;
    }

    private double calculateDifference(int doc1, int doc2, DataSet dataSet) {
        return dataSet.relevanceAt(doc1) - dataSet.relevanceAt(doc2);
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

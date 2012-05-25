package ru.compscicenter.ml.ranking.trees;

import ru.compscicenter.ml.ranking.data.DataSet;
import ru.compscicenter.ml.ranking.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public class ConfusionWeightCalculator implements WeightCalculator {

    // Probability to change editors mark (first) to true mark (second)
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
    };

    @Override
    public double[][][] calculateWeights(DataSet dataSet) {
        Map<Double, Double> labelsProbabilities = Utils.averageDCGPerLabel(dataSet);
        Map<Double, Map<Double, Double>> probabilities = new HashMap<>();
        for (Map.Entry<Double, Double> entry1 : labelsProbabilities.entrySet()) {
            Map<Double, Double> cp = new HashMap<>();
            probabilities.put(entry1.getKey(), cp);
            for (Map.Entry<Double, Double> entry2 : labelsProbabilities.entrySet()) {
                double difference = calculateDifference(entry1.getKey(), entry2.getKey(), dataSet, labelsProbabilities);
                cp.put(entry2.getKey(), difference);
            }
        }

        double[][][] w = new double[dataSet.queries().size()][0][0];
        int queryIndex = 0;
        for (List<Integer> query : dataSet.queries()) {
            w[queryIndex] = new double[query.size()][query.size()];
            for (int i = 0; i < query.size(); i++) {
                for (int j = 0; j < query.size(); j++) {
                    double r1 = dataSet.relevanceAt(query.get(i));
                    double r2 = dataSet.relevanceAt(query.get(j));
                    w[queryIndex][i][j] = probabilities.get(r1).get(r2);
                }
            }
            queryIndex++;
        }
        return w;
    }

    private double calculateDifference(double r1, double r2, DataSet dataSet, Map<Double, Double> p) {
        double result = 0;
        for (Map.Entry<Double, Double> entry1 : p.entrySet()) {
            for (Map.Entry<Double, Double> entry2 : p.entrySet()) {
                double p1 = probability(entry1.getKey(), r1);
                double p2 = probability(entry2.getKey(), r2);
                double d = entry1.getKey() - entry2.getKey();
//                        entry1.getValue() - entry2.getValue();
                result += d > 0 ? p1 * p2 * d : 0;
            }
        }
        return result;
    }

    private double probability(double label, double condition) {
        for (double[] d : confusionProbabilities) {
            if (Double.compare(condition, d[0]) == 0 && Double.compare(label, d[1]) == 0) {
                return d[2];
            }
        }
        return label == condition ? 1 : 0;
    }
}

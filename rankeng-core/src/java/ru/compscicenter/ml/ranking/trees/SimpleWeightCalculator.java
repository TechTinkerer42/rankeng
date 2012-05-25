package ru.compscicenter.ml.ranking.trees;

import ru.compscicenter.ml.ranking.data.DataSet;
import ru.compscicenter.ml.ranking.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public class SimpleWeightCalculator implements WeightCalculator {

    @Override
    public double[][][] calculateWeights(DataSet dataSet) {
        Map<Double, Double> labelsProbabilities = Utils.averageDCGPerLabel(dataSet);
        double[][][] w = new double[dataSet.queries().size()][0][0];
        int queryIndex = 0;
        for (List<Integer> query : dataSet.queries()) {
            w[queryIndex] = new double[query.size()][query.size()];
            for (int i = 0; i < query.size(); i++) {
                for (int j = 0; j < query.size(); j++) {
                    double difference =
                            calculateDifference(query.get(i), query.get(j), dataSet, labelsProbabilities);
                    w[queryIndex][i][j] = difference > 0 ? difference : 0;
                }
            }
            queryIndex++;
        }
        return w;
    }

    private double calculateDifference(int doc1, int doc2, DataSet dataSet, Map<Double, Double> p) {
          return dataSet.relevanceAt(doc1) - dataSet.relevanceAt(doc2);
//        return p.get(dataSet.relevanceAt(doc1)) - p.get(dataSet.relevanceAt(doc2));
    }
}

package ru.compscicenter.ranking.trees;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;
import ru.compscicenter.ranking.utils.Pair;
import ru.compscicenter.ranking.utils.PairUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   23.04.12
 */
public class VarianceTreeSplitter implements TreeSplitter {

    private int minPerLeaf = 20; // will be deprecated

    public void setMinPerLeaf(int minPerLeaf) {
        this.minPerLeaf = minPerLeaf;
    }

    @Override
    public Pair<Integer, Double> obtainSplit(Weights weights, DataSet dataSet, Outputs outputs) {
        Pair<Integer, Double> result = null;
        double minVariance = Double.MAX_VALUE;
        for (int featureIndex = 0; featureIndex < dataSet.numberOfFeatures(); featureIndex++) {
            List<Pair<Instance, Double>> indexedValues = getIndexedValues(dataSet, featureIndex);

            // Variances of left subgroups of observations
            List<Pair<Double, Double>> leftVariances = aggregateVariances(weights, outputs, indexedValues);

            // Variances of right subgroups of observations
            Collections.reverse(indexedValues);
            List<Pair<Double, Double>> rightVariances = aggregateVariances(weights, outputs, indexedValues);

            // Sum observations
            List<Pair<Double, Double>> variances = new ArrayList<>();
            for (int index = 0; index < leftVariances.size(); index++) {
                Pair<Double, Double> leftVariance = leftVariances.get(index);
                Pair<Double, Double> rightVariance = rightVariances.get(rightVariances.size() - index - 1);
                variances.add(new Pair<>(leftVariance.first(), leftVariance.second() + rightVariance.second()));
            }

            // Update result
            if (variances.size() > 0) {
                int minIndex = PairUtils.minIndexOfSecond(variances);
                if (variances.get(minIndex).second() < minVariance) {
                    result = new Pair<>(featureIndex, variances.get(minIndex).first());
                    minVariance = variances.get(minIndex).second();
                }
            }
        }
        return result;
    }

    private List<Pair<Double, Double>> aggregateVariances(
            Weights weights,
            Outputs outputs,
            List<Pair<Instance, Double>> indexedValues
    ) {
        VarianceAggregator varianceAggregator = new VarianceAggregator();
        List<Pair<Double, Double>> partialVariances = new ArrayList<>();
        for (Pair<Double, List<Instance>> splitValue : calculateSplitValues(indexedValues)) {
            for (Instance instance : splitValue.second()) {
                varianceAggregator.add(weights.weightOf(instance), outputs.valueOf(instance));
            }
            partialVariances.add(new Pair<>(splitValue.first(), varianceAggregator.variance()));
        }
        return partialVariances;
    }

    public List<Pair<Double, List<Instance>>> calculateSplitValues(List<Pair<Instance, Double>> indexedValues) {
        List<Pair<Double, List<Instance>>> result = new ArrayList<>();
        double prevValue = indexedValues.get(0).second();
        List<Instance> values = new ArrayList<>();
        int leftCount = 0;
        int rightCount = indexedValues.size();
        for (Pair<Instance, Double> indexedValue : indexedValues) {
            double curValue = indexedValue.second();
            if (Double.compare(prevValue, curValue) == 0) {
                values.add(indexedValue.first());
            } else {
                if (leftCount >= minPerLeaf && rightCount >= minPerLeaf) {
                    result.add(new Pair<>((prevValue + curValue) / 2, values));
                }
                values = new ArrayList<>();
                values.add(indexedValue.first());
            }
            prevValue = curValue;
            leftCount++;
            rightCount--;
        }
        return result;
    }

    private List<Pair<Instance, Double>> getIndexedValues(DataSet dataSet, int featureIndex) {
        List<Pair<Instance, Double>> indexedValues = new ArrayList<>();
        for (Query query : dataSet.queries()) {
            for (Instance instance : query.getInstances()) {
                indexedValues.add(new Pair<>(instance, instance.featureValue(featureIndex)));
            }
        }
        PairUtils.sortBySecond(indexedValues);

        return indexedValues;
    }

    static class VarianceAggregator {
        private double sumW = 0.0;
        private double sumWR = 0.0;
        private double sumWRR = 0.0;

        private double s = 0.0;
        private int N = 0;

        public void add(double w, double r) {
            if (N == 0) {
                sumW = w;
                sumWR = w * r;
                sumWRR = w * r * r;
            } else {
                double newSumW = w + sumW;
                double newSumWR = w * r + sumWR;
                double newSumWRR = w * r * r + sumWRR;

                double nominator = w * r * r * newSumW * newSumW
                        + sumWRR * (w * w + 2 * w * sumW)
                        + s * sumW * sumW
                        - (2 * sumW - N) * (w * w * r * r + 2 * w * r * sumWR)
                        + (1 - 2 * w) * newSumWR * newSumWR;

                s = nominator / (newSumW * newSumW);

                // Update
                sumW = newSumW;
                sumWR = newSumWR;
                sumWRR = newSumWRR;
            }
            N++;
        }

        // Not exactly variance
        public double variance() {
            return s;
        }
    }
}

package ru.compscicenter.ml.ranking.trees;

import ru.compscicenter.ml.ranking.data.DataSet;
import ru.compscicenter.ml.ranking.utils.Pair;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   23.04.12
 */
public class RegressionTreeLearner {

    private final int maxDepth;

    private final Splitter splitter;
    private final Estimator estimator;

    public RegressionTreeLearner(Splitter splitter, Estimator estimator, int maxDepth) {
        this.splitter = splitter;
        this.estimator = estimator;

        this.maxDepth = maxDepth;
    }

    public RegressionTree learn(double[] weights, DataSet dataSet) {
        return new RegressionTree(makeNode(0, weights, dataSet));
    }

    public RegressionTree.RegressionNode makeNode(int depth, double[] weights, DataSet dataSet) {
        Pair<Integer, Double> split = splitter.obtainSplit(weights, dataSet);
        if (split != null && depth < maxDepth - 1) {
            Pair<DataSet, DataSet> dataSets = dataSet.split(split.first(), split.second());
            return RegressionTree.makeInnerNode(
                    split.first(), split.second(),
                    makeNode(depth + 1, weights, dataSets.first()),
                    makeNode(depth + 1, weights, dataSets.second())
            );
        }
        return RegressionTree.makeLeaf(estimator.estimate(weights, dataSet));
    }
}

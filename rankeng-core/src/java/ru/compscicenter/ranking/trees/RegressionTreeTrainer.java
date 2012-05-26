package ru.compscicenter.ranking.trees;

import ru.compscicenter.ranking.RegressionModelTrainer;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.utils.Pair;

import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   23.04.12
 */
public class RegressionTreeTrainer implements RegressionModelTrainer<RegressionTree> {

    private final int maxDepth;
    private final Splitter splitter;
    private final Estimator estimator;

    public RegressionTreeTrainer(Splitter splitter, Estimator estimator, int maxDepth) {
        this.maxDepth = maxDepth;
        this.estimator = estimator;
        this.splitter = splitter;
    }

    @Override
    public RegressionTree train(double[] weights, DataSet dataSet) {
        DataSet newDataSet =
                new DataSet(dataSet.queries(), dataSet.features(), dataSet.relevance());
        return new RegressionTree(makeNode(0, weights, newDataSet));
    }

    private RegressionTree.RegressionNode makeNode(int depth, double[] weights, DataSet dataSet) {
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

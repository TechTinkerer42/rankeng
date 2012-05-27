package ru.compscicenter.ranking.trees;

import org.apache.log4j.Logger;
import ru.compscicenter.ranking.RegressionModelTrainer;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.utils.Pair;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date:   23.04.12
 */
public class RegressionTreeTrainer implements RegressionModelTrainer<RegressionTree> {

    private static final Logger logger = Logger.getLogger(RegressionTreeTrainer.class);

    private final int maxDepth;
    private final TreeSplitter treeSplitter;
    private final TreeEstimator treeEstimator;

    public RegressionTreeTrainer(TreeSplitter treeSplitter, TreeEstimator treeEstimator, int maxDepth) {
        this.maxDepth = maxDepth;
        this.treeEstimator = treeEstimator;
        this.treeSplitter = treeSplitter;
    }

    @Override
    public RegressionTree train(Weights weights, DataSet dataSet, Outputs outputs) {
        logger.info("Training regression tree (data set size is " + dataSet.size() + ")");
        return new RegressionTree(makeNode(0, weights, dataSet, outputs));
    }

    private RegressionTree.RegressionNode makeNode(int depth, Weights weights, DataSet dataSet, Outputs outputs) {
        logger.debug("Making node (depth = " + depth + ", data set size is " + dataSet.size() + ")");

        Pair<Integer, Double> split = treeSplitter.obtainSplit(weights, dataSet, outputs);
        if (split != null && depth < maxDepth - 1) {
            Pair<DataSet, DataSet> dataSets = dataSet.split(split.first(), split.second());
            return RegressionTree.makeInnerNode(
                    split.first(), split.second(),
                    makeNode(depth + 1, weights, dataSets.first(), outputs),
                    makeNode(depth + 1, weights, dataSets.second(), outputs)
            );
        }
        return RegressionTree.makeLeaf(treeEstimator.estimate(weights, dataSet, outputs));
    }
}

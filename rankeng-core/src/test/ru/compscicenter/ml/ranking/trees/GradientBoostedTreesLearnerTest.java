package ru.compscicenter.ml.ranking.trees;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import ru.compscicenter.ml.ranking.data.DataProvider;
import ru.compscicenter.ml.ranking.data.DataSet;
import ru.compscicenter.ml.ranking.data.TestDataProvider;
import ru.compscicenter.ml.ranking.utils.CommonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   19.04.12
 */
public class GradientBoostedTreesLearnerTest {

    private static final Logger logger = Logger.getLogger(GradientBoostedTreesLearnerTest.class);

    @Test
    public void test1() {
        List<List<Integer>> queries = new ArrayList<>();

        List<Integer> query1 = new ArrayList<>();
        query1.add(0);
        query1.add(1);

        List<Integer> query2 = new ArrayList<>();
        query2.add(2);
        query2.add(3);
        query2.add(4);

        queries.add(query1);
        queries.add(query2);

        double[][] featureValues = new double[][]{
                {1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 1, 0, 0}
        };
        double[] relevance = new double[]{1, 2, 3, 4, 0};
        DataSet dataSet = new DataSet(queries, featureValues, relevance);

        GradientBoostedTreesLearner treesLearner = new GradientBoostedTreesLearner();
        treesLearner.setMinNumPerLeaf(1);
        treesLearner.setSampleRatio(1);
        treesLearner.setShrinkage(0.1);
        treesLearner.setMaxDepth(2);
        treesLearner.setLearningRate(1);

        AdditiveTrees predictor = treesLearner.learn(dataSet, 100);
        double[] predictions = new double[relevance.length];
        for (int index = 0; index < predictions.length; index++) {
            predictions[index] = predictor.predict(dataSet.getRow(index));
        }

        Assert.assertTrue("DCG", Double.compare(4.0, CommonUtils.calculateDCG(dataSet, predictions)) == 0);
    }

    @Test
    public void test2() {
        DataProvider dataProvider = new TestDataProvider("test-data/1.data", 1091, 10);
        DataSet dataSet;
        try {
            dataSet = dataProvider.loadData();
        } catch (IOException e) {
            logger.fatal("Unexpected IO exception", e);
            throw new AssertionError("Unexpected IO exception", e);
        }

        GradientBoostedTreesLearner treesLearner = new GradientBoostedTreesLearner();
        treesLearner.setMinNumPerLeaf(1);
        treesLearner.setSampleRatio(1);
        treesLearner.setShrinkage(0.1);
        treesLearner.setMaxDepth(2);
        treesLearner.setLearningRate(1);

        AdditiveTrees predictor = treesLearner.learn(dataSet, 10);
        double[] predictions = new double[dataSet.numberOfRows()];
        for (int index = 0; index < predictions.length; index++) {
            predictions[index] = predictor.predict(dataSet.getRow(index));
        }
        Assert.assertTrue(
                "Final DCG",
                Double.compare(12.654153934544977, CommonUtils.calculateDCG(dataSet, predictions)) == 0
        );

        RegressionTree tree0 = predictor.treeEnsemble.get(0);
        Assert.assertTrue(
                "Value in the root node",
                Double.compare(0.7565385968716039, tree0.getRoot().value()) == 0
        );
        Assert.assertTrue("Node was not split", tree0.getRoot().isLeaf());

        RegressionTree tree5 = predictor.treeEnsemble.get(5);
        Assert.assertEquals("Split index in the root", 1, tree5.getRoot().splitIndex());
        Assert.assertTrue(
                "Split value in the root",
                Double.compare(0.41666666666666663, tree5.getRoot().splitValue()) == 0
        );

        Assert.assertTrue(
                "Value at the leaf node",
                Double.compare(0.0692991729450361, tree5.getRoot().greaterNode().value()) == 0
        );
        Assert.assertTrue("The leaf node wasn't spilt", tree5.getRoot().greaterNode().isLeaf());

        RegressionTree tree9 = predictor.treeEnsemble.get(9);
        Assert.assertEquals("Split index in the root", 2, tree9.getRoot().splitIndex());
        Assert.assertTrue(
                "Split value in the root",
                Double.compare(0.75, tree9.getRoot().splitValue()) == 0
        );

        Assert.assertTrue(
                "Value at the leaf node",
                Double.compare(-0.020769791671996423, tree9.getRoot().lessOrEqualNode().value()) == 0
        );
        Assert.assertTrue("The leaf node wasn't spilt", tree9.getRoot().lessOrEqualNode().isLeaf());

    }
}

package ru.compscicenter.ml.ranking.trees;

import junit.framework.Assert;
import org.junit.Test;
import ru.compscicenter.ml.ranking.data.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   18.04.12
 */
public class RegressionTreeLearnerTest {

    @Test
    public void test1() {
        double[][] featureValues = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[] relevance = new double[]{1, 2, 3, 4};

        List<Integer> query1 = new ArrayList<>();
        query1.add(0);
        query1.add(2);
        List<Integer> query2 = new ArrayList<>();
        query1.add(1);
        query1.add(3);
        List<List<Integer>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        DataSet dataSet = new DataSet(queries, featureValues, relevance);

        double[] weights = new double[]{1, 1, 1, 1};

        Estimator estimator = new LambdaRankEstimator();
        Splitter splitter = new VarianceSplitter(1);
        RegressionTreeLearner regressionTreeLearner = new RegressionTreeLearner(splitter, estimator, 5);
        RegressionTree regressionTree = regressionTreeLearner.learn(weights, dataSet);

        Assert.assertEquals("Node (represents single observation)",
                1.0, regressionTree.predict(dataSet.getRowList().get(0))
        );
        Assert.assertEquals("Node (represents single observation)",
                2.0, regressionTree.predict(dataSet.getRowList().get(1))
        );
        Assert.assertEquals("Node (represents single observation)",
                3.0, regressionTree.predict(dataSet.getRowList().get(2))
        );
        Assert.assertEquals("Node (represents single observation)",
                4.0, regressionTree.predict(dataSet.getRowList().get(3))
        );
    }

    @Test
    public void test2() {
        double[][] featureValues = new double[][]{
                {0, 0, 0}, {0, 0, 1},
                {0, 1, 0}, {0, 1, 1},
                {1, 0, 0}, {1, 0, 1},
                {1, 1, 0}, {1, 1, 1}
        };
        List<Integer> query1 = new ArrayList<>();
        query1.add(0);
        query1.add(1);
        query1.add(2);
        query1.add(3);
        List<Integer> query2 = new ArrayList<>();
        query1.add(4);
        query1.add(5);
        query1.add(6);
        query1.add(7);
        List<List<Integer>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        double[] relevance = new double[]{0, 0, 0, 0, 2, 3, 2, 4};
        DataSet dataSet = new DataSet(queries, featureValues, relevance);

        double[] weights = new double[]{1, 1, 1, 1, 1, 1, 1, 1};

        Splitter splitter = new VarianceSplitter(1);
        Estimator estimator = new LambdaRankEstimator();
        RegressionTreeLearner regressionTreeLearner = new RegressionTreeLearner(splitter, estimator, 3);
        RegressionTree regressionTree = regressionTreeLearner.learn(weights, dataSet);

        Assert.assertEquals("Accurate observation",
                0.0, regressionTree.predict(dataSet.getRowList().get(0))
        );
        Assert.assertEquals("Accurate observation",
                2.0, regressionTree.predict(dataSet.getRowList().get(4))
        );
        Assert.assertEquals("Not accurate observation (due max depth limit)",
                3.5, regressionTree.predict(dataSet.getRowList().get(5))
        );
    }
}

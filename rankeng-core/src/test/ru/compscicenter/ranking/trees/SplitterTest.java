package ru.compscicenter.ranking.trees;

import junit.framework.Assert;
import org.junit.Test;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.utils.Pair;
import ru.compscicenter.ranking.utils.ProbabilityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   23.04.12
 */
public class SplitterTest {

    private static final double EPS = 1e-8;

    @Test
    public void testVarianceSplitter() {
        double[][] features = new double[][]{
                {0.9, 0.5},
                {0.4, 0.7},
                {1.2, 0.2},
                {1.2, 0.7},
                {0.7, 0.9},
        };
        double[] relevance = new double[]{1, 2, 30, 40, 12};

        List<Integer> query1 = new ArrayList<>();
        query1.add(0);
        query1.add(1);
        query1.add(4);
        List<Integer> query2 = new ArrayList<>();
        query2.add(2);
        query2.add(3);

        List<List<Integer>> queries1 = new ArrayList<>();
        queries1.add(query1);
        List<List<Integer>> queries2 = new ArrayList<>();
        queries2.add(query2);
        List<List<Integer>> queries3 = new ArrayList<>();
        queries3.add(query1);
        queries3.add(query2);

        double[] weights = new double[]{1, 1, 1, 1, 1};

        DataSet dataSet1 = new DataSet(queries1, features, relevance);
        DataSet dataSet2 = new DataSet(queries2, features, relevance);
        DataSet dataSet3 = new DataSet(queries3, features, relevance);

        VarianceSplitter splitter1 = new VarianceSplitter();
        splitter1.setMinPerLeaf(1);
        Pair<Integer, Double> split11 = splitter1.obtainSplit(weights, dataSet1);
        Assert.assertTrue("Splitting values", Math.abs(0.8 - split11.second()) < EPS && split11.first() == 1);

        Pair<Integer, Double> split21 = splitter1.obtainSplit(weights, dataSet2);
        Assert.assertTrue("Splitting values", Math.abs(0.45 - split21.second()) < EPS && split21.first() == 1);

        Pair<Integer, Double> split31 = splitter1.obtainSplit(weights, dataSet3);
        Assert.assertTrue("Splitting values", Math.abs(1.05 - split31.second()) < EPS && split31.first() == 0);

        VarianceSplitter splitter2 = new VarianceSplitter();
        splitter2.setMinPerLeaf(2);
        Pair<Integer, Double> split12 = splitter2.obtainSplit(weights, dataSet1);
        Assert.assertNull("Splitting values", split12);

        Pair<Integer, Double> split22 = splitter2.obtainSplit(weights, dataSet2);
        Assert.assertNull("Splitting values", split22);

        Pair<Integer, Double> split32 = splitter2.obtainSplit(weights, dataSet3);
        Assert.assertTrue("Splitting values", Math.abs(1.05 - split32.second()) < EPS && split32.first() == 0);

        VarianceSplitter splitter3 = new VarianceSplitter();
        splitter3.setMinPerLeaf(3);
        Pair<Integer, Double> split33 = splitter3.obtainSplit(weights, dataSet3);
        Assert.assertNull("Splitting values", split33);
    }

    @Test
    public void testVarianceAggregator() {
        VarianceSplitter.VarianceAggregator aggregator = new VarianceSplitter.VarianceAggregator();
        aggregator.add(1, 1);
        Assert.assertEquals(
                "Aggregated variances",
                ProbabilityUtils.variance(new double[]{1}),
                aggregator.variance());
        aggregator.add(1, -1);
        Assert.assertEquals(
                "Compare aggregated and correct variances ",
                2 * ProbabilityUtils.variance(new double[]{1, -1}),
                aggregator.variance());
        aggregator.add(1, 0);
        Assert.assertEquals(
                "Compare aggregated and correct variances ",
                3 * ProbabilityUtils.variance(new double[]{1, -1, 0}),
                aggregator.variance());
        aggregator.add(1, 0.5);
        Assert.assertEquals(
                "Compare aggregated and correct variances ",
                4 * ProbabilityUtils.variance(new double[]{1, -1, 0, 0.5}),
                aggregator.variance());
    }
}


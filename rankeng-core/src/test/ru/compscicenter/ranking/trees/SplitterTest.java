package ru.compscicenter.ranking.trees;

import junit.framework.Assert;
import org.junit.Test;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;
import ru.compscicenter.ranking.utils.Pair;
import ru.compscicenter.ranking.utils.ProbabilityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   23.04.12
 */
public class SplitterTest {

    private static final double EPS = 1e-8;

    private static final double[][] features = new double[][]{
            {0.9, 0.5},
            {0.4, 0.7},
            {1.2, 0.2},
            {1.2, 0.7},
            {0.7, 0.9},
    };

    private static final double[] relevance = new double[]{1, 2, 30, 40, 12};


    @Test
    public void testVarianceSplitter1() {

        Instance instance0 = new Instance(0, 0, features[0]);
        Instance instance1 = new Instance(1, 0, features[1]);
        Instance instance2 = new Instance(2, 0, features[4]);

        Map<Instance, Double> relevanceMap = new HashMap<>();
        relevanceMap.put(instance0, relevance[0]);
        relevanceMap.put(instance1, relevance[1]);
        relevanceMap.put(instance2, relevance[4]);
        Outputs outputs = new Outputs(relevanceMap);

        Map<Instance, Double> weightMap = new HashMap<>();
        weightMap.put(instance0, 1.0);
        weightMap.put(instance1, 1.0);
        weightMap.put(instance2, 1.0);
        Weights weights = new Weights(weightMap);

        List<Instance> queryInstances = new ArrayList<>();
        queryInstances.add(instance0);
        queryInstances.add(instance1);
        queryInstances.add(instance2);

        List<Query> queries = new ArrayList<>();
        queries.add(new Query(queryInstances));

        DataSet dataSet = new DataSet(queries, 2);

        VarianceTreeSplitter splitter1 = new VarianceTreeSplitter();
        splitter1.setMinPerLeaf(1);
        Pair<Integer, Double> split11 = splitter1.obtainSplit(weights, dataSet, outputs);
        Assert.assertTrue("Splitting values", Double.compare(0.8, split11.second()) == 0 && split11.first() == 1);

        VarianceTreeSplitter splitter2 = new VarianceTreeSplitter();
        splitter2.setMinPerLeaf(2);
        Pair<Integer, Double> split12 = splitter2.obtainSplit(weights, dataSet, outputs);
        Assert.assertNull("Splitting values", split12);
    }

    @Test
    public void testVarianceSplitter2() {

        List<Instance> queryInstances = new ArrayList<>();
        Instance instance0 = new Instance(2, 0, features[2]);
        Instance instance1 = new Instance(3, 0, features[3]);
        queryInstances.add(instance0);
        queryInstances.add(instance1);

        Map<Instance, Double> relevanceMap = new HashMap<>();
        relevanceMap.put(instance0, relevance[2]);
        relevanceMap.put(instance1, relevance[3]);
        Outputs outputs = new Outputs(relevanceMap);

        Map<Instance, Double> weightMap = new HashMap<>();
        weightMap.put(instance0, 1.0);
        weightMap.put(instance1, 1.0);
        Weights weights = new Weights(weightMap);

        List<Query> queries = new ArrayList<>();
        queries.add(new Query(queryInstances));

        DataSet dataSet = new DataSet(queries, 2);

        VarianceTreeSplitter splitter1 = new VarianceTreeSplitter();
        splitter1.setMinPerLeaf(1);

        Pair<Integer, Double> split1 = splitter1.obtainSplit(weights, dataSet, outputs);
        Assert.assertTrue("Splitting values", Math.abs(0.45 - split1.second()) < EPS && split1.first() == 1);

        VarianceTreeSplitter splitter2 = new VarianceTreeSplitter();
        splitter2.setMinPerLeaf(2);

        Pair<Integer, Double> split2 = splitter2.obtainSplit(weights, dataSet, outputs);
        Assert.assertNull("Splitting values", split2);
    }

    @Test
    public void testVarianceSplitter3() {

        Instance instance0 = new Instance(0, 0, features[0]);
        Instance instance1 = new Instance(1, 0, features[1]);
        Instance instance2 = new Instance(2, 1, features[2]);
        Instance instance3 = new Instance(3, 1, features[3]);
        Instance instance4 = new Instance(4, 0, features[4]);

        List<Instance> queryInstances = new ArrayList<>();
        queryInstances.add(instance4);
        queryInstances.add(instance2);
        queryInstances.add(instance3);
        queryInstances.add(instance0);
        queryInstances.add(instance1);

        Map<Instance, Double> relevanceMap = new HashMap<>();
        relevanceMap.put(instance0, relevance[0]);
        relevanceMap.put(instance1, relevance[1]);
        relevanceMap.put(instance2, relevance[2]);
        relevanceMap.put(instance3, relevance[3]);
        relevanceMap.put(instance4, relevance[4]);
        Outputs outputs = new Outputs(relevanceMap);

        Map<Instance, Double> weightMap = new HashMap<>();
        weightMap.put(instance0, 1.0);
        weightMap.put(instance1, 1.0);
        weightMap.put(instance2, 1.0);
        weightMap.put(instance3, 1.0);
        weightMap.put(instance4, 1.0);
        Weights weights = new Weights(weightMap);

        List<Query> queries = new ArrayList<>();
        queries.add(new Query(queryInstances));

        DataSet dataSet = new DataSet(queries, 2);

        VarianceTreeSplitter splitter1 = new VarianceTreeSplitter();
        splitter1.setMinPerLeaf(1);

        Pair<Integer, Double> split1 = splitter1.obtainSplit(weights, dataSet, outputs);
        Assert.assertTrue("Splitting values", Math.abs(1.05 - split1.second()) < EPS && split1.first() == 0);

        VarianceTreeSplitter splitter2 = new VarianceTreeSplitter();
        splitter2.setMinPerLeaf(2);

        Pair<Integer, Double> split2 = splitter2.obtainSplit(weights, dataSet, outputs);
        Assert.assertTrue("Splitting values", Math.abs(1.05 - split2.second()) < EPS && split2.first() == 0);

        VarianceTreeSplitter splitter3 = new VarianceTreeSplitter();
        splitter3.setMinPerLeaf(3);
        Pair<Integer, Double> split3 = splitter3.obtainSplit(weights, dataSet, outputs);
        Assert.assertNull("Splitting values", split3);
    }

    @Test
    public void testVarianceAggregator() {
        VarianceTreeSplitter.VarianceAggregator aggregator = new VarianceTreeSplitter.VarianceAggregator();
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


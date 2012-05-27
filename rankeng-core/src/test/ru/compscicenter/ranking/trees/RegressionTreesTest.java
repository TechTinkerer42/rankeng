package ru.compscicenter.ranking.trees;

import junit.framework.Assert;
import org.junit.Test;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;
import ru.compscicenter.ranking.lambdarank.LambdaRankEstimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date:   18.04.12
 */
public class RegressionTreesTest {

    @Test
    public void test1() {
        double[][] featureValues = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};

        Instance instance0 = new Instance(0, 0, featureValues[0]);
        Instance instance1 = new Instance(1, 1, featureValues[1]);
        Instance instance2 = new Instance(2, 0, featureValues[2]);
        Instance instance3 = new Instance(3, 1, featureValues[3]);

        List<Instance> query1Instances = new ArrayList<>();
        query1Instances.add(instance0);
        query1Instances.add(instance2);
        List<Instance> query2Instances = new ArrayList<>();
        query2Instances.add(instance1);
        query2Instances.add(instance3);

        List<Query> queries = new ArrayList<>();
        queries.add(new Query(query1Instances));
        queries.add(new Query(query2Instances));

        Map<Instance, Double> relevanceMap = new HashMap<>();
        relevanceMap.put(instance0, 1.0);
        relevanceMap.put(instance1, 2.0);
        relevanceMap.put(instance2, 3.0);
        relevanceMap.put(instance3, 4.0);
        Outputs relevance = new Outputs(relevanceMap);

        Map<Instance, Double> weightMap = new HashMap<>();
        weightMap.put(instance0, 1.0);
        weightMap.put(instance1, 1.0);
        weightMap.put(instance2, 1.0);
        weightMap.put(instance3, 1.0);
        Weights weights = new Weights(weightMap);

        DataSet dataSet = new DataSet(queries, 2);

        VarianceTreeSplitter splitter = new VarianceTreeSplitter();
        splitter.setMinPerLeaf(1);
        TreeEstimator treeEstimator = new LambdaRankEstimator();

        RegressionTreeTrainer regressionTreeTrainer =
                new RegressionTreeTrainer(splitter, treeEstimator, 5);
        RegressionTree regressionTree = regressionTreeTrainer.train(weights, dataSet, relevance);

        Assert.assertEquals("Node (represents single observation)",
                2.0, regressionTree.predict(instance1)
        );
        Assert.assertEquals("Node (represents single observation)",
                3.0, regressionTree.predict(instance2)
        );
        Assert.assertEquals("Node (represents single observation)",
                4.0, regressionTree.predict(instance3)
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
        Instance instance0 = new Instance(0, 0, featureValues[0]);
        Instance instance1 = new Instance(1, 0, featureValues[1]);
        Instance instance2 = new Instance(2, 0, featureValues[2]);
        Instance instance3 = new Instance(3, 0, featureValues[3]);
        Instance instance4 = new Instance(4, 1, featureValues[4]);
        Instance instance5 = new Instance(5, 1, featureValues[5]);
        Instance instance6 = new Instance(6, 1, featureValues[6]);
        Instance instance7 = new Instance(7, 1, featureValues[7]);

        List<Instance> query1Instances = new ArrayList<>();
        query1Instances.add(instance0);
        query1Instances.add(instance1);
        query1Instances.add(instance2);
        query1Instances.add(instance3);

        List<Instance> query2Instances = new ArrayList<>();
        query1Instances.add(instance4);
        query1Instances.add(instance5);
        query1Instances.add(instance6);
        query1Instances.add(instance7);

        List<Query> queries = new ArrayList<>();
        queries.add(new Query(query1Instances));
        queries.add(new Query(query2Instances));

        Map<Instance, Double> relevanceMap = new HashMap<>();
        relevanceMap.put(instance0, 0.0);
        relevanceMap.put(instance1, 0.0);
        relevanceMap.put(instance2, 0.0);
        relevanceMap.put(instance3, 0.0);
        relevanceMap.put(instance4, 2.0);
        relevanceMap.put(instance5, 3.0);
        relevanceMap.put(instance6, 2.0);
        relevanceMap.put(instance7, 4.0);
        Outputs outputs = new Outputs(relevanceMap);

        Map<Instance, Double> weightMap = new HashMap<>();
        weightMap.put(instance0, 1.0);
        weightMap.put(instance1, 1.0);
        weightMap.put(instance2, 1.0);
        weightMap.put(instance3, 1.0);
        weightMap.put(instance4, 1.0);
        weightMap.put(instance5, 1.0);
        weightMap.put(instance6, 1.0);
        weightMap.put(instance7, 1.0);
        Weights weights = new Weights(weightMap);

        DataSet dataSet = new DataSet(queries, 3);

        VarianceTreeSplitter splitter = new VarianceTreeSplitter();
        splitter.setMinPerLeaf(1);
        TreeEstimator treeEstimator = new LambdaRankEstimator();

        RegressionTreeTrainer regressionTreeTrainer =
                new RegressionTreeTrainer(splitter, treeEstimator, 3);
        RegressionTree regressionTree = regressionTreeTrainer.train(weights, dataSet, outputs);

        Assert.assertEquals("Accurate observation",
                0.0, regressionTree.predict(instance0)
        );
        Assert.assertEquals("Accurate observation",
                2.0, regressionTree.predict(instance4)
        );
        Assert.assertEquals("Not accurate observation (due max depth limit)",
                3.5, regressionTree.predict(instance5)
        );
    }
}


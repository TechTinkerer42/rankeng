package ru.compscicenter.ranking.ensembles;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import ru.compscicenter.ranking.Target;
import ru.compscicenter.ranking.data.DataLoader;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;
import ru.compscicenter.ranking.data.TestDataLoader;
import ru.compscicenter.ranking.lambdarank.LambdaRankEstimator;
import ru.compscicenter.ranking.lambdarank.LambdaRankTarget;
import ru.compscicenter.ranking.trees.RegressionTree;
import ru.compscicenter.ranking.trees.RegressionTreeTrainer;
import ru.compscicenter.ranking.trees.TreeEstimator;
import ru.compscicenter.ranking.trees.VarianceTreeSplitter;
import ru.compscicenter.ranking.utils.Evaluator;
import ru.compscicenter.ranking.utils.Pair;
import ru.compscicenter.ranking.utils.RankingUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date:   19.04.12
 */
public class GradientBoostingTest {

    private static final Logger logger = Logger.getLogger(GradientBoostingTest.class);

    @Test
    public void test1() {
        double[][] featureValues = new double[][]{
                {1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 1, 0, 0}
        };

        Instance instance0 = new Instance(0, 0, featureValues[0]);
        Instance instance1 = new Instance(1, 0, featureValues[1]);
        Instance instance2 = new Instance(2, 1, featureValues[2]);
        Instance instance3 = new Instance(3, 1, featureValues[3]);
        Instance instance4 = new Instance(4, 1, featureValues[4]);

        List<Instance> query1Instances = new ArrayList<>();
        query1Instances.add(instance0);
        query1Instances.add(instance1);

        List<Instance> query2Instances = new ArrayList<>();
        query2Instances.add(instance2);
        query2Instances.add(instance3);
        query2Instances.add(instance4);

        List<Query> queries = new ArrayList<>();
        queries.add(new Query(query1Instances));
        queries.add(new Query(query2Instances));

        DataSet dataSet = new DataSet(queries, 5);

        Map<Instance, Double> relevanceMap = new HashMap<>();
        relevanceMap.put(instance0, 1.0);
        relevanceMap.put(instance1, 2.0);
        relevanceMap.put(instance2, 3.0);
        relevanceMap.put(instance3, 4.0);
        relevanceMap.put(instance4, 0.0);
        Outputs outputs = new Outputs(relevanceMap);

        VarianceTreeSplitter splitter = new VarianceTreeSplitter();
        splitter.setMinPerLeaf(1);

        TreeEstimator treeEstimator = new LambdaRankEstimator();
        RegressionTreeTrainer regressionTreeTrainer =
                new RegressionTreeTrainer(splitter, treeEstimator, 2);

        Target target = new LambdaRankTarget(1.0, dataSet, outputs);
        GradientBoosting.Builder<RegressionTree> builder =
                new GradientBoosting.Builder<>(regressionTreeTrainer, target);
        GradientBoosting<RegressionTree> gradientBoosting =
                builder.shrinkage(0.1).bootstrapRatio(1).build();

        Ensemble<RegressionTree> model = gradientBoosting.train(dataSet, outputs, 50);
        Outputs predictions = RankingUtils.predictAll(dataSet, model);

        Assert.assertTrue(
                "DCG",
                Double.compare(4.0, Evaluator.calculateDCG(dataSet, outputs, predictions)) == 0
        );
    }

    @Test
    public void test2() {
        DataLoader dataLoader = new TestDataLoader("test-data/1.data", 1091, 10);
        Pair<DataSet, Outputs> pair;
        try {
            pair = dataLoader.loadData();
        } catch (IOException e) {
            logger.fatal("Unexpected IO exception", e);
            throw new AssertionError("Unexpected IO exception", e);
        }
        DataSet dataSet = pair.first();
        Outputs relevance = pair.second();

        VarianceTreeSplitter splitter = new VarianceTreeSplitter();
        splitter.setMinPerLeaf(1);
        TreeEstimator treeEstimator = new LambdaRankEstimator();

        RegressionTreeTrainer regressionTreeTrainer =
                new RegressionTreeTrainer(splitter, treeEstimator, 2);

        Target target = new LambdaRankTarget(1, dataSet, relevance);
        GradientBoosting.Builder<RegressionTree> builder =
                new GradientBoosting.Builder<>(regressionTreeTrainer, target);
        GradientBoosting<RegressionTree> gradientBoosting =
                builder.shrinkage(0.1).bootstrapRatio(1).build();

        Ensemble<RegressionTree> model = gradientBoosting.train(dataSet, relevance, 10);
        Outputs predictions = RankingUtils.predictAll(dataSet, model);

        double d2 = Evaluator.calculateDCG(dataSet, relevance, predictions);
        logger.debug(d2);
        Assert.assertTrue(
                "Final DCG",
                Double.compare(12.654153934544977, d2) == 0
        );

        RegressionTree tree0 = model.getBaseModelList().get(0).second();
        Assert.assertTrue(
                "Split value in the root node",
                Double.compare(0.5833333333333333, tree0.getRoot().splitValue()) == 0
        );

        RegressionTree tree5 = model.getBaseModelList().get(5).second();
        Assert.assertEquals("Split index in the root", 1, tree5.getRoot().splitIndex());
        Assert.assertTrue(
                "Split value in the root",
                Double.compare(0.41666666666666663, tree5.getRoot().splitValue()) == 0
        );

        Assert.assertTrue(
                "Value at the leaf node",
                Double.compare(0.0675038329050605, tree5.getRoot().greaterNode().value()) == 0
        );
        Assert.assertTrue("The leaf node wasn't spilt", tree5.getRoot().greaterNode().isLeaf());

        RegressionTree tree10 = model.getBaseModelList().get(10).second();
        Assert.assertEquals("Split index in the root", 3, tree10.getRoot().splitIndex());
        Assert.assertTrue(
                "Split value in the root",
                Double.compare(0.41666666666666663, tree10.getRoot().splitValue()) == 0
        );

        Assert.assertTrue(
                "Value at the leaf node",
                Double.compare(-0.026699686769640203, tree10.getRoot().lessOrEqualNode().value()) == 0
        );
        Assert.assertTrue("The leaf node wasn't spilt", tree10.getRoot().lessOrEqualNode().isLeaf());

    }
}

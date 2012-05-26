package ru.compscicenter.ranking.ensembles;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import ru.compscicenter.ranking.LambdaRankTarget;
import ru.compscicenter.ranking.Target;
import ru.compscicenter.ranking.data.*;
import ru.compscicenter.ranking.utils.Evaluator;
import ru.compscicenter.ranking.trees.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   19.04.12
 */
public class GradientBoostingTest {

    private static final Logger logger = Logger.getLogger(GradientBoostingTest.class);

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

        WeightCalculator weightCalculator = new WeightCalculator();
        RichData richData = new RichData(dataSet, weightCalculator.calculateWeights(dataSet));

        VarianceSplitter splitter = new VarianceSplitter();
        splitter.setMinPerLeaf(1);

        Estimator estimator = new LambdaRankEstimator();
        RegressionTreeTrainer regressionTreeTrainer =
                new RegressionTreeTrainer(splitter, estimator, 2);

        Target target = new LambdaRankTarget();
        GradientBoosting.Builder<RegressionTree> builder =
                new GradientBoosting.Builder<>(regressionTreeTrainer, target);
        GradientBoosting<RegressionTree> gradientBoosting =
                builder.shrinkage(0.1).bootstrapRatio(1).learningRate(1).build();

        Ensemble<RegressionTree> model = gradientBoosting.train(richData, 100);
        double[] predictions = new double[relevance.length];
        for (int index = 0; index < predictions.length; index++) {
            predictions[index] = model.predict(dataSet.getRow(index));
        }

        Assert.assertTrue("DCG", Double.compare(4.0, Evaluator.calculateDCG(dataSet, predictions)) == 0);
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

        WeightCalculator weightCalculator = new WeightCalculator();
        RichData richData = new RichData(dataSet, weightCalculator.calculateWeights(dataSet));

        VarianceSplitter splitter = new VarianceSplitter();
        splitter.setMinPerLeaf(1);
        Estimator estimator = new LambdaRankEstimator();

        RegressionTreeTrainer regressionTreeTrainer =
                new RegressionTreeTrainer(splitter, estimator, 2);

        Target target = new LambdaRankTarget();
        GradientBoosting.Builder<RegressionTree> builder =
                new GradientBoosting.Builder<>(regressionTreeTrainer, target);
        GradientBoosting<RegressionTree> gradientBoosting =
                builder.shrinkage(0.1).bootstrapRatio(1).learningRate(1).build();

        Ensemble<RegressionTree> model = gradientBoosting.train(richData, 10);
        double[] predictions = new double[dataSet.relevance().length];
        for (int index = 0; index < predictions.length; index++) {
            predictions[index] = model.predict(dataSet.getRow(index));
        }

        Assert.assertTrue(
                "Final DCG",
                Double.compare(12.654153934544977, Evaluator.calculateDCG(dataSet, predictions)) == 0
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

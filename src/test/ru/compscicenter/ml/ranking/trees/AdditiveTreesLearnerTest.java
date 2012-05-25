package ru.compscicenter.ml.ranking.trees;

import org.junit.Assert;
import org.junit.Test;
import ru.compscicenter.ml.ranking.data.DataSet;
import ru.compscicenter.ml.ranking.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   19.04.12
 */
public class AdditiveTreesLearnerTest {

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

        AdditiveTreesLearner additiveTreesLearner = new AdditiveTreesLearner();
        additiveTreesLearner.setMinNumPerLeaf(1);
        additiveTreesLearner.setSampleRatio(1);
        additiveTreesLearner.setShrinkage(0.1);
        additiveTreesLearner.setMaxDepth(2);
        additiveTreesLearner.setLearningRate(1);

        AdditiveTrees predictor = additiveTreesLearner.learn(dataSet, 100);
        double[] predictions = new double[relevance.length];
        for (int index = 0; index < predictions.length; index++) {
            predictions[index] = predictor.predict(dataSet.getRow(index));
        }

        System.out.println(predictor.predict(dataSet.getRowList().get(0)));
        System.out.println(predictor.predict(dataSet.getRowList().get(1)));
        System.out.println(predictor.predict(dataSet.getRowList().get(2)));
        System.out.println(predictor.predict(dataSet.getRowList().get(3)));
        System.out.println(predictor.predict(dataSet.getRowList().get(4)));

        Assert.assertTrue("DCG", Double.compare(4.0, Utils.calculateDCG(dataSet, predictions)) == 0);
    }
}

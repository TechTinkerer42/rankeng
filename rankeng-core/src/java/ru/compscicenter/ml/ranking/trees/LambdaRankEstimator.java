package ru.compscicenter.ml.ranking.trees;

import ru.compscicenter.ml.ranking.data.DataSet;

import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   23.04.12
 */
public class LambdaRankEstimator implements Estimator {

    @Override
    public double estimate(double[] weights, DataSet dataSet) {
        double sumW = 0.0;
        double sumWR = 0.0;
        for (List<Integer> query : dataSet.queries()) {
            for (Integer doc : query) {
                sumW += weights[doc];
                sumWR += weights[doc] * dataSet.relevanceAt(doc);
            }
        }
        return sumWR / sumW;
    }
}

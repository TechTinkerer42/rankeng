package ru.compscicenter.ranking.trees;

import ru.compscicenter.ranking.data.DataSet;

import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
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

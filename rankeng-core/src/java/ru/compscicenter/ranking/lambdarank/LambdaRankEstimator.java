package ru.compscicenter.ranking.lambdarank;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;
import ru.compscicenter.ranking.trees.TreeEstimator;
import ru.compscicenter.ranking.trees.Weights;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class LambdaRankEstimator implements TreeEstimator {

    @Override
    public double estimate(Weights weights, DataSet dataSet, Outputs outputs) {
        double sumW = 0.0;
        double sumWR = 0.0;
        for (Query query : dataSet.queries()) {
            for (Instance instance : query.getInstances()) {
                sumW += weights.weightOf(instance);
                sumWR += weights.weightOf(instance) * outputs.valueOf(instance);
            }
        }
        return sumWR / sumW;
    }
}

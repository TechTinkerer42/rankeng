package ru.compscicenter.ranking.trees;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Outputs;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface TreeEstimator {
    double estimate(Weights weights, DataSet dataSet, Outputs outputs);
}

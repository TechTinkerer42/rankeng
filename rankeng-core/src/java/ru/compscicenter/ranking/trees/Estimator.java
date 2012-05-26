package ru.compscicenter.ranking.trees;

import ru.compscicenter.ranking.data.DataSet;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface Estimator {
    double estimate(double[] weights, DataSet dataSet);
}

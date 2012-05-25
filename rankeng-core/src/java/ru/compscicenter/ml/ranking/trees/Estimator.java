package ru.compscicenter.ml.ranking.trees;

import ru.compscicenter.ml.ranking.data.DataSet;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   23.04.12
 */
public interface Estimator {
    double estimate(double[] weights, DataSet dataSet);
}

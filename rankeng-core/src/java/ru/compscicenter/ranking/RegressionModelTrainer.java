package ru.compscicenter.ranking;

import ru.compscicenter.ranking.data.DataSet;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface RegressionModelTrainer<T extends RegressionModel> {
    T train(double[] weights, DataSet dataSet);
}

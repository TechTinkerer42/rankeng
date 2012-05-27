package ru.compscicenter.ranking;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.trees.Weights;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface RegressionModelTrainer<T extends RegressionModel> {
    T train(Weights weights, DataSet dataSet, Outputs outputs);
}

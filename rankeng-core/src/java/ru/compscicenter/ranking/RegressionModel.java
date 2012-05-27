package ru.compscicenter.ranking;

import ru.compscicenter.ranking.data.Instance;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface RegressionModel {
    double predict(Instance instance);
}

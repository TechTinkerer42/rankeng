package ru.compscicenter.ranking;

import ru.compscicenter.ranking.data.FeatureRow;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface RegressionModel {
    double predict(FeatureRow featureRow);
}

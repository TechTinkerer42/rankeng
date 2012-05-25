package ru.compscicenter.ml.ranking.data;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   18.04.12
 */
public interface FeatureRow {
    double valueAt(int featureIndex);
    int size();
}

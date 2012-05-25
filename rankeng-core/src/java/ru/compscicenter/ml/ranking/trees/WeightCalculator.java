package ru.compscicenter.ml.ranking.trees;

import ru.compscicenter.ml.ranking.data.DataSet;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public interface WeightCalculator {
    double[][][] calculateWeights(DataSet dataSet);
}

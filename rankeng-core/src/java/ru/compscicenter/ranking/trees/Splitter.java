package ru.compscicenter.ranking.trees;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.utils.Pair;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   22.04.12
 */
public interface Splitter {
    Pair<Integer, Double> obtainSplit(double[] weights, DataSet dataSet);
}

package ru.compscicenter.ranking.trees;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.utils.Pair;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   22.04.12
 */
public interface TreeSplitter {
    Pair<Integer, Double> obtainSplit(Weights weights, DataSet dataSet, Outputs outputs);
}

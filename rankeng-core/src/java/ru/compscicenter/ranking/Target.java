package ru.compscicenter.ranking;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.ensembles.CoefficientOptimizer;
import ru.compscicenter.ranking.trees.Weights;
import ru.compscicenter.ranking.utils.Pair;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface Target {
    Pair<Weights, Outputs> approximatePseudoResiduals(DataSet dataSet, Outputs outputs);
    CoefficientOptimizer makeOptimizer();
}

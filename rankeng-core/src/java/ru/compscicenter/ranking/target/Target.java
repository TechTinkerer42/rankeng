package ru.compscicenter.ranking.target;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.ensembles.CoefficientOptimizer;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface Target {

    // TODO: интерфейс нелеп!
    double[][] calculate(DataSet dataSet, double[] predictions);
    CoefficientOptimizer makeOptimizer();
}

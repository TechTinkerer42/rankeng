package ru.compscicenter.ranking.ensembles;

import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Outputs;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface CoefficientOptimizer {
    <T extends RegressionModel> double optimize(DataSet dataSet, Outputs outputs, T model);
}

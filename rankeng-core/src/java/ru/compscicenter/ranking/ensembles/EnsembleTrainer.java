package ru.compscicenter.ranking.ensembles;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.RegressionModel;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface EnsembleTrainer<T extends RegressionModel> {
    Ensemble<T> train(DataSet dataSet, int numberOfIterations);
    void improve(Ensemble<T> ensemble, DataSet dataSet, int numberOfIterations);
}

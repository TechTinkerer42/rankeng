package ru.compscicenter.ranking.utils;

import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class RankingUtils {

    private RankingUtils() {
        // Do nothing
    }

    public static Outputs predictAll(DataSet dataSet, RegressionModel model) {
        Map<Instance, Double> predictions = new HashMap<>();
        for (Instance instance : dataSet) {
            predictions.put(instance, model.predict(instance));
        }
        return new Outputs(predictions);
    }
}

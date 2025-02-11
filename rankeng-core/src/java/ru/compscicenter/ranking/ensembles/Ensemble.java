package ru.compscicenter.ranking.ensembles;

import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class Ensemble<T extends RegressionModel> implements RegressionModel {

    private final List<Pair<Double, T>> baseModels = new ArrayList<>();

    public void addBaseModel(double coefficient, T baseModel) {
        baseModels.add(new Pair<>(coefficient, baseModel));
    }

    @Override
    public double predict(Instance instance) {
        double result = 0;
        for (Pair<Double, T> baseModel : baseModels) {
            result += baseModel.first() * baseModel.second().predict(instance);
        }
        return result;
    }

    public List<Pair<Double, T>> getBaseModelList() {
        return baseModels;
    }
}

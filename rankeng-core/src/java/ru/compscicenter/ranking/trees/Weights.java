package ru.compscicenter.ranking.trees;

import ru.compscicenter.ranking.data.Instance;

import java.util.Map;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class Weights {

    private final Map<Instance, Double> weights;

    public Weights(Map<Instance, Double> weights) {
        this.weights = weights;
    }

    public double weightOf(Instance instance) {
        return weights.get(instance);
    }
}

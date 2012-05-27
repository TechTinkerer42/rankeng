package ru.compscicenter.ranking.data;

import java.util.Map;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class Outputs {

    private final Map<Instance, Double> outputs;

    public Outputs(Map<Instance, Double> outputs) {
        this.outputs = outputs;
    }

    public double valueOf(Instance instance) {
        return outputs.get(instance);
    }
}

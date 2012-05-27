package ru.compscicenter.ranking.lambdarank;

import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.utils.Pair;

import java.util.Map;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class PairWeights {

    private final Map<Pair<Instance, Instance>, Double> pairWeights;

    public PairWeights(Map<Pair<Instance, Instance>, Double> pairWeights) {
        this.pairWeights = pairWeights;
    }

    public double weightOf(Instance i, Instance j) {
        if (i.getQueryId() != j.getQueryId()) {
            return 0;
        }
        return pairWeights.get(new Pair<>(i, j));
    }
}

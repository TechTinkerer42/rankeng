package ru.compscicenter.ranking.lambdarank;

import ru.compscicenter.ranking.Target;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;
import ru.compscicenter.ranking.ensembles.CoefficientOptimizer;
import ru.compscicenter.ranking.trees.Weights;
import ru.compscicenter.ranking.utils.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class LambdaRankTarget implements Target {

    private static final double DEFAULT_RATE = 0.001;

    private final double learningRate; // for gradient descent
    private final PairWeights pairWeights;

    public LambdaRankTarget(DataSet dataSet, Outputs relevance) {
        this(DEFAULT_RATE, dataSet, relevance);
    }

    public LambdaRankTarget(double learningRate, DataSet dataSet, Outputs relevance) {
        this.learningRate = learningRate;
        PairWeightCalculator weightCalculator = new PairWeightCalculator();
        this.pairWeights = weightCalculator.calculateWeights(dataSet, relevance);
    }

    // TODO: complicated logic - must be verified (and probably improved)
    @Override
    public Pair<Weights, Outputs> calculatePseudoResiduals(DataSet dataSet, Outputs outputs) {
        Map<Instance, Double> W = new HashMap<>();
        Map<Instance, Double> V = new HashMap<>();
        Map<Instance, Double> Val = new HashMap<>();

        for (Instance instance : dataSet) {
            W.put(instance, 0.0);
            V.put(instance, 0.0);
            Val.put(instance, 0.0);
        }

        for (Query query : dataSet.queries()) {
            for (Instance i : query.getInstances()) {
                double exi = Math.exp(outputs.valueOf(i));
                for (Instance j : query.getInstances()) {
                    double exj = Math.exp(outputs.valueOf(j));

                    double wij = pairWeights.weightOf(i, j);
                    double wji = pairWeights.weightOf(j, i);

                    W.put(i, W.get(i) + wij + wji);
                    Val.put(i, Val.get(i) + 0.5 * (wij * exj - wji * exi) / (exi + exj));
                }
                if (W.get(i) != 0) {
                    V.put(i, Val.get(i) / W.get(i));
                }
            }
        }

        Weights resultWeights = new Weights(W);
        Outputs resultOutputs = new Outputs(V);

        return new Pair<>(resultWeights, resultOutputs);
    }

    @Override
    public CoefficientOptimizer makeOptimizer() {
        return new LambdaRankOptimizer(learningRate, pairWeights);
    }
}

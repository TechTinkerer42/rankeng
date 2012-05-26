package ru.compscicenter.ranking.target;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.ensembles.CoefficientOptimizer;

import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class LambdaRankTarget implements Target {

    private static final double DEFAULT_RATE = 0.001;

    private final double learningRate; // for gradient descent
    private final double[][][] w;

    public LambdaRankTarget(DataSet dataSet) {
        this(DEFAULT_RATE, dataSet);
    }

    public LambdaRankTarget(double learningRate, DataSet dataSet) {
        this.learningRate = learningRate;
        WeightCalculator weightCalculator = new WeightCalculator();
        this.w = weightCalculator.calculateWeights(dataSet);
    }

    // TODO: какого хера я вообще делаю??? Надо разобраться с тем, что я приближаю!!!
    @Override
    public double[][] calculate(DataSet dataSet, double[] predictions) {
        double[] W = new double[dataSet.numberOfRows()];
        double[] V = new double[dataSet.numberOfRows()];

        double[] Val = new double[dataSet.numberOfRows()];
        int queryIndex = 0;
        for (List<Integer> query : dataSet.queries()) {
            for (int i = 0; i < query.size(); i++) {
                double exi = Math.exp(predictions[query.get(i)]);
                for (int j = 0; j < query.size(); j++) {
                    double exj = Math.exp(predictions[query.get(j)]);
                    double wij = w[queryIndex][i][j];
                    double wji = w[queryIndex][j][i];

                    W[query.get(i)] += wij + wji;
                    Val[query.get(i)] += 0.5 * (wij * exj - wji * exi) / (exi + exj);
                }
                if (W[query.get(i)] != 0) {
                    V[query.get(i)] = Val[query.get(i)] / W[query.get(i)];
                }
            }
            queryIndex++;
        }
        double[][] result = new double[2][];
        result[0] = V;
        result[1] = W;

        return result;
    }

    @Override
    public CoefficientOptimizer makeOptimizer() {
        return new LambdaRankOptimizer(learningRate, w);
    }
}

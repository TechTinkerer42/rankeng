package ru.compscicenter.ranking.target;

import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.ensembles.CoefficientOptimizer;

import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class LambdaRankOptimizer implements CoefficientOptimizer {

    private static final int DESCENT_STEP_NUMBER = 100;
    private static final double DESCENT_START_VALUE = 1.0;

    private double learningRate = 0.001;
    private double[][][] w;


    public LambdaRankOptimizer(double learningRate, double[][][] w) {
        this.learningRate = learningRate;
        this.w = w;
    }

    /**
     * Gradient descent
     */
    @Override
    public <T extends RegressionModel> double optimize(DataSet dataSet, double[] predictions, T baseModel) {

        double gamma = DESCENT_START_VALUE;

        double[] gg = new double[dataSet.numberOfRows()];
        for (int index = 0; index < gg.length; index++) {
            gg[index] = baseModel.predict(dataSet.getRow(index));
        }

        for (int index = 0; index < DESCENT_STEP_NUMBER; index++) {
            double diff = 0;
            int queryIndex = 0;
            for (List<Integer> query : dataSet.queries()) {
                for (int i = 0; i < query.size(); i++) {
                    for (int j = 0; j < query.size(); j++) {
                        double Tji = gg[query.get(j)] - gg[query.get(i)];
                        double Fji = predictions[query.get(j)] - predictions[query.get(i)];
                        diff -= w[queryIndex][i][j] * Tji * (1 / (1 + Math.exp(Fji + gamma * Tji)) - 1);
                    }
                }
                queryIndex++;
            }
            gamma -= learningRate * diff;
        }

        return gamma;
    }
}
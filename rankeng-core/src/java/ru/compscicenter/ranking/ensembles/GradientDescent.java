package ru.compscicenter.ranking.ensembles;

import ru.compscicenter.ranking.data.RichData;
import ru.compscicenter.ranking.RegressionModel;

import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/25/12
 */
public class GradientDescent {

    private static final int GAMMA_STEP_NUMBER = 100;
    private static final double GAMMA_START_VALUE = 1.0;

    private double learningRate = 0.001;

    public GradientDescent(double learningRate) {
        this.learningRate = learningRate;
    }

    public <T extends RegressionModel> double optimize(RichData richData, double[] predictions, T model) {

        double gamma = GAMMA_START_VALUE;

        double[] T = new double[richData.getDataSet().numberOfRows()];
        for (int index = 0; index < T.length; index++) {
            T[index] = model.predict(richData.getDataSet().getRow(index));
        }

        for (int index = 0; index < GAMMA_STEP_NUMBER; index++) {
            double diff = 0;
            int queryIndex = 0;
            for (List<Integer> query : richData.getDataSet().queries()) {
                for (int i = 0; i < query.size(); i++) {
                    for (int j = 0; j < query.size(); j++) {
                        double Tji = T[query.get(j)] - T[query.get(i)];
                        double Fji = predictions[query.get(j)] - predictions[query.get(i)];
                        diff -= richData.getWeights()[queryIndex][i][j] * Tji * (1 / (1 + Math.exp(Fji + gamma * Tji)) - 1);
                    }
                }
                queryIndex++;
            }
            gamma -= learningRate * diff;
        }
        return gamma;
    }
}

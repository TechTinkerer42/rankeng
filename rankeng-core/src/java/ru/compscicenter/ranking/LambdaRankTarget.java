package ru.compscicenter.ranking;

import ru.compscicenter.ranking.data.RichData;

import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class LambdaRankTarget implements Target {

    // TODO: какого хера я вообще делаю??? Надо разобраться с тем, что я приближаю!!!
    public double[][] calculate(RichData richData, double[] predictions) {
        double[] W = new double[richData.getDataSet().numberOfRows()];
        double[] V = new double[richData.getDataSet().numberOfRows()];

        double[] Val = new double[richData.getDataSet().numberOfRows()];
        int queryIndex = 0;
        for (List<Integer> query : richData.getDataSet().queries()) {
            for (int i = 0; i < query.size(); i++) {
                double exi = Math.exp(predictions[query.get(i)]);
                for (int j = 0; j < query.size(); j++) {
                    double exj = Math.exp(predictions[query.get(j)]);
                    double wij = richData.getWeights()[queryIndex][i][j];
                    double wji = richData.getWeights()[queryIndex][j][i];

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
}

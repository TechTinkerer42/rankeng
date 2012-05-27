package ru.compscicenter.ranking.lambdarank;

import org.apache.log4j.Logger;
import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;
import ru.compscicenter.ranking.ensembles.CoefficientOptimizer;
import ru.compscicenter.ranking.utils.RankingUtils;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class LambdaRankOptimizer implements CoefficientOptimizer {

    private static final Logger logger = Logger.getLogger(LambdaRankOptimizer.class);

    private static final int DESCENT_STEP_NUMBER = 100;
    private static final double DESCENT_START_VALUE = 1.0;

    private double learningRate = 0.001;
    private PairWeights pairWeights;


    public LambdaRankOptimizer(double learningRate, PairWeights pairWeights) {
        this.learningRate = learningRate;
        this.pairWeights = pairWeights;
    }

    /**
     * Gradient descent
     * TODO: нихера не понимаю, что тут делаю
     */
    @Override
    public <T extends RegressionModel> double optimize(DataSet dataSet, Outputs outputs, T baseModel) {
        logger.info("Starting optimization (data set size is " + dataSet.size() + ")");

        double gamma = DESCENT_START_VALUE;
        Outputs newPredictions = RankingUtils.predictAll(dataSet, baseModel);

        for (int index = 0; index < DESCENT_STEP_NUMBER; index++) {
            double diff = 0;
            for (Query query : dataSet.queries()) {
                for (Instance i : query.getInstances()) {
                    for (Instance j : query.getInstances()) {
                        double Tji = newPredictions.valueOf(j) - newPredictions.valueOf(i);
                        double Fij = outputs.valueOf(j) - outputs.valueOf(i);

                        diff -= pairWeights.weightOf(i, j)
                                * Tji * (1 / (1 + Math.exp(Fij + gamma * Tji)) - 1);
                    }
                }
            }
            gamma -= learningRate * diff;
        }

        return gamma;
    }
}
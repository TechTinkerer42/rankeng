package ru.compscicenter.ranking.ensembles;

import org.apache.log4j.Logger;
import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.RegressionModelTrainer;
import ru.compscicenter.ranking.Target;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.trees.Weights;
import ru.compscicenter.ranking.utils.Pair;
import ru.compscicenter.ranking.utils.RankingUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */

// http://en.wikipedia.org/wiki/Gradient_boosting
public class GradientBoosting<T extends RegressionModel> implements EnsembleTrainer<T> {

    private static final Logger logger = Logger.getLogger(GradientBoosting.class);

    private final double shrinkage;
    private final double bootstrapRatio;
    private final Random random;

    // Target, which is used to fit model
    private final Target target;

    // Inner models
    private final RegressionModelTrainer<T> baseModelTrainer;

    private GradientBoosting(
            Target target,
            double shrinkage,
            double bootstrapRatio,
            int initSeed,
            RegressionModelTrainer<T> baseModelTrainer
    ) {
        this.target = target;
        this.shrinkage = shrinkage;
        this.bootstrapRatio = bootstrapRatio;
        this.random = new Random(initSeed);
        this.baseModelTrainer = baseModelTrainer;
    }

    @Override
    public Ensemble<T> train(DataSet dataSet, Outputs relevance, int stepNumber) {
        logger.info("Training model (steps = " + stepNumber + ", data set size is " + dataSet.size() + ")");

        Pair<Weights, Outputs> pair = target.approximatePseudoResiduals(dataSet, relevance);
        T baseModel = baseModelTrainer.train(pair.first(), dataSet, pair.second());

        Ensemble<T> result = new Ensemble<>();
        result.addBaseModel(1.0, baseModel);
        improve(result, dataSet, relevance, stepNumber);

        return result;
    }

    @Override
    public void improve(Ensemble<T> ensemble, DataSet dataSet, Outputs relevance, int numberOfIterations) {
        Outputs predictions = RankingUtils.predictAll(dataSet, ensemble);

        for (int step = 1; step <= numberOfIterations; step++) {
            logger.debug("Starting step " + step);

            logger.debug("Bootstrapping (step" + step + ")");
            DataSet subset = bootstrap(dataSet);

            logger.debug("Training base model (step = " + step + ")");
            Pair<Weights, Outputs> pair = target.approximatePseudoResiduals(subset, predictions);
            T newBaseModel = baseModelTrainer.train(pair.first(), subset, pair.second());

            logger.debug("Optimization starting");
            CoefficientOptimizer coefficientOptimizer = target.makeOptimizer();
            double coefficient =
                    coefficientOptimizer.optimize(subset, predictions, newBaseModel);
            logger.debug("step=" + step + ", coefficient=" + coefficient);

            ensemble.addBaseModel(shrinkage * coefficient, newBaseModel);

            logger.debug("Updating vector of predictions");
            Outputs newPredictions = RankingUtils.predictAll(dataSet, newBaseModel);
            predictions = updatePredictions(dataSet, predictions, coefficient * shrinkage, newPredictions);

            logger.debug("Finished step " + step);
        }
    }

    private Outputs updatePredictions(
            DataSet dataSet,
            Outputs predictions,
            double coefficient,
            Outputs newPredictions
    ) {
        Map<Instance, Double> result = new HashMap<>();
        for (Instance instance : dataSet) {
            result.put(
                    instance,
                    predictions.valueOf(instance) + coefficient * newPredictions.valueOf(instance)
            );
        }
        return new Outputs(result);
    }

    private DataSet bootstrap(DataSet dataSet) {
        int randomSeed = random.nextInt();
        return dataSet.split(bootstrapRatio, randomSeed).first();
    }

    public static class Builder<T extends RegressionModel> {

        // Default values
        private int initSeed = 7; // Init seed for random in bootstrapping
        private double shrinkage = 0.1;
        private double bootstrapRatio = 0.5; // for bootstrapping

        private final RegressionModelTrainer<T> baseModelTrainer;
        private final Target target;

        public Builder(RegressionModelTrainer<T> baseModelTrainer, Target target) {
            this.baseModelTrainer = baseModelTrainer;
            this.target = target;
        }

        public GradientBoosting<T> build() {
            return new GradientBoosting<>(
                    target,
                    shrinkage,
                    bootstrapRatio,
                    initSeed,
                    baseModelTrainer
            );
        }

        public Builder<T> shrinkage(double shrinkage) {
            this.shrinkage = shrinkage;
            return this;
        }

        public Builder<T> initSeed(int initSeed) {
            this.initSeed = initSeed;
            return this;
        }

        public Builder<T> bootstrapRatio(double bootstrapRatio) {
            this.bootstrapRatio = bootstrapRatio;
            return this;
        }
    }
}

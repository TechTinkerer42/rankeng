package ru.compscicenter.ranking.ensembles;

import org.apache.log4j.Logger;
import ru.compscicenter.ranking.RegressionModelTrainer;
import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.target.Target;

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

    protected GradientBoosting(
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
    public Ensemble<T> train(DataSet dataSet, int stepNumber) {
        double[][] d = target.calculate(dataSet, dataSet.relevance());
        DataSet newDataSet = new DataSet(dataSet.queries(), dataSet.features(), d[0]);
        T baseModel = baseModelTrainer.train(d[1], newDataSet);

        Ensemble<T> result = new Ensemble<>();
        result.addBaseModel(1.0, baseModel);
        improve(result, dataSet, stepNumber);

        return result;
    }

    @Override
    public void improve(Ensemble<T> ensemble, DataSet dataSet, int numberOfIterations) {

        double[] predictions = new double[dataSet.numberOfRows()];
        for (int index = 0; index < dataSet.numberOfRows(); index++) {
            predictions[index] = ensemble.predict(dataSet.getRow(index));
        }

        for (int stepCounter = 1; stepCounter <= numberOfIterations; stepCounter++) {
            DataSet subset = bootstrap(dataSet);

            double[][] d = target.calculate(subset, predictions);
            DataSet newSubset = new DataSet(dataSet.queries(), dataSet.features(), d[0]);
            T newBaseModel = baseModelTrainer.train(d[1], newSubset);

            CoefficientOptimizer coefficientOptimizer = target.makeOptimizer();
            double coefficient =
                    coefficientOptimizer.optimize(subset, predictions, newBaseModel);
            logger.debug("coefficient=" + coefficient);

            ensemble.addBaseModel(shrinkage * coefficient, newBaseModel);

            for (int index = 0; index < dataSet.numberOfRows(); index++) {
                predictions[index] += coefficient * shrinkage * newBaseModel.predict(dataSet.getRow(index));
            }
        }
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

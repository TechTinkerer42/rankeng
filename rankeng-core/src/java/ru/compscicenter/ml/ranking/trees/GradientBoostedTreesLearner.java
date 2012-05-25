package ru.compscicenter.ml.ranking.trees;

import org.apache.log4j.Logger;
import ru.compscicenter.ml.ranking.data.DataSet;
import ru.compscicenter.ml.ranking.utils.CommonUtils;
import ru.compscicenter.ml.ranking.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   19.04.12
 */
public class GradientBoostedTreesLearner {

    private static Logger logger = Logger.getLogger(GradientBoostedTreesLearner.class);

    private static final int GAMMA_STEP_NUMBER = 100;
    private static final double GAMMA_START_VALUE = 1.0;

    private static final int RANDOM_SEED = 7;

    private double shrinkage = 0.5;   // default: 0.1
    private double sampleRatio = 0.5;
    private int minNumPerLeaf = 10;
    private double learningRate = 0.001;

    private int maxDepth = 4;
    private WeightCalculator weightCalculator = new SimpleWeightCalculator();
    private Estimator estimator = new LambdaRankEstimator();

    public AdditiveTrees learn(DataSet dataSet, int stepNumber) {
        AdditiveTrees model = learnInitialModel(dataSet);
        update(model, dataSet, stepNumber);

        return model;
    }

    private void update(AdditiveTrees model, DataSet dataSet, int stepNumber) {
        Splitter splitter = new VarianceSplitter(minNumPerLeaf);
        RegressionTreeLearner regressionTreeLearner = new RegressionTreeLearner(splitter, estimator, maxDepth);
        double[][][] w = weightCalculator.calculateWeights(dataSet);

        double[] predictions = new double[dataSet.numberOfRows()];
        for (int index = 0; index < dataSet.numberOfRows(); index++) {
            predictions[index] = model.predict(dataSet.getRow(index));
        }

        Random seedGenerator = new Random(RANDOM_SEED);
        for (int stepCounter = 1; stepCounter <= stepNumber; stepCounter++) {
            Pair<DataSet, DataSet> dataSets = dataSet.split(sampleRatio, seedGenerator.nextInt());
            RegressionTree newTree = buildNewTree(dataSets.first(), predictions, regressionTreeLearner, w);

            // TODO: add normal equation and better alignment (see yetirank)

            // какого хера я вообще делаю??? Надо разобраться с тем, что я приближаю!!!

            // gamma (see http://en.wikipedia.org/wiki/Gradient_boosting)
            double gamma = calculateGamma(dataSets.first(), w, predictions, newTree);
            logger.debug("gamma=" + gamma);

            // TODO: refactor - don't use model implicitly
            model.treeEnsemble.add(newTree);
            model.treeWeights.add(gamma * shrinkage);

            for (int index = 0; index < dataSet.numberOfRows(); index++) {
                predictions[index] += gamma * shrinkage * newTree.predict(dataSet.getRow(index));
            }
        }
    }

    private double calculateGamma(DataSet dataSet, double[][][] w, double[] F, RegressionTree tree) {
        double gamma = GAMMA_START_VALUE;

        double[] T = new double[dataSet.numberOfRows()];
        for (int index = 0; index < T.length; index++) {
            T[index] = tree.predict(dataSet.getRow(index));
        }

        for (int index = 0; index < GAMMA_STEP_NUMBER; index++) {
            double diff = 0;
            int queryIndex = 0;
            for (List<Integer> query : dataSet.queries()) {
                for (int i = 0; i < query.size(); i++) {
                    for (int j = 0; j < query.size(); j++) {
                        double Tji = T[query.get(j)] - T[query.get(i)];
                        double Fji = F[query.get(j)] - F[query.get(i)];
                        diff -= w[queryIndex][i][j] * Tji * (1 / (1 + Math.exp(Fji + gamma * Tji)) - 1);
                    }
                }
                queryIndex++;
            }
            gamma -= learningRate * diff;
        }
        return gamma;
    }

    private AdditiveTrees learnInitialModel(DataSet dataSet) {
        Map<Double, Double> labelDistribution = CommonUtils.averageDCGPerLabel(dataSet);

        List<Double> treeWeights = new ArrayList<>();
        List<RegressionTree> treeEnsemble = new ArrayList<>();

        double[] initWeights = new double[dataSet.numberOfRows()];
        double[] initRelevance = new double[dataSet.numberOfRows()];
        for (List<Integer> query : dataSet.queries()) {
            for (Integer doc : query) {
                initWeights[doc] = 1.0;
                initRelevance[doc] = labelDistribution.get(dataSet.relevanceAt(doc));
            }
        }

        DataSet newDataSet = new DataSet(dataSet.queries(), dataSet.features(), initRelevance);
        double value = estimator.estimate(initWeights, newDataSet);
        RegressionTree initTree = new RegressionTree(RegressionTree.makeLeaf(value));

        treeEnsemble.add(initTree);
        treeWeights.add(1.0);

        return new AdditiveTrees(treeWeights, treeEnsemble);
    }

    private RegressionTree buildNewTree(
            DataSet dataSet, double[] predictions,
            RegressionTreeLearner regressionTreeLearner, double[][][] w
    ) {
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
        return regressionTreeLearner.learn(W, new DataSet(dataSet.queries(), dataSet.features(), V));
    }

    public void setShrinkage(double shrinkage) {
        this.shrinkage = shrinkage;
    }

    public void setSampleRatio(double sampleRatio) {
        this.sampleRatio = sampleRatio;
    }

    public void setMinNumPerLeaf(int minNumPerLeaf) {
        this.minNumPerLeaf = minNumPerLeaf;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public void setWeightCalculator(WeightCalculator weightCalculator) {
        this.weightCalculator = weightCalculator;
    }
}

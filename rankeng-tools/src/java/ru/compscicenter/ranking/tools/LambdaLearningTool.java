package ru.compscicenter.ranking.tools;

import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.FeatureRow;
import ru.compscicenter.ranking.ensembles.Ensemble;
import ru.compscicenter.ranking.ensembles.GradientBoosting;
import ru.compscicenter.ranking.target.LambdaRankTarget;
import ru.compscicenter.ranking.target.Target;
import ru.compscicenter.ranking.trees.LambdaRankEstimator;
import ru.compscicenter.ranking.trees.RegressionTree;
import ru.compscicenter.ranking.trees.RegressionTreeTrainer;
import ru.compscicenter.ranking.trees.VarianceSplitter;
import ru.compscicenter.ranking.utils.Evaluator;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public class LambdaLearningTool implements LearningTool {

    private Ensemble<RegressionTree> model;

    @Override
    public String getDescription() {
        return "Gradient tree boosting model";
    }

    @Override
    public void learn(DataSet learningSet, int stepNumber) {
        VarianceSplitter splitter = new VarianceSplitter();
        LambdaRankEstimator estimator = new LambdaRankEstimator();
        RegressionTreeTrainer regressionTreeTrainer =
                new RegressionTreeTrainer(splitter, estimator, 5);

        Target target = new LambdaRankTarget(learningSet);

        GradientBoosting.Builder<RegressionTree> builder =
                new GradientBoosting.Builder<>(regressionTreeTrainer, target);
        GradientBoosting<RegressionTree> gradientBoosting =
                builder.shrinkage(0.1).bootstrapRatio(0.5).build();

        model = gradientBoosting.train(learningSet, stepNumber);

        Evaluator evaluator = new Evaluator(learningSet);
        evaluator.evaluate("Final model (learning): ", model);
    }

    @Override
    public double[] predict(DataSet testSet) {
        double[] predictions = new double[testSet.numberOfRows()];
        int index = 0;
        for (FeatureRow featureRow : testSet.getRowList()) {
            predictions[index] = model.predict(featureRow);
            index++;
        }
        return predictions;
    }
}

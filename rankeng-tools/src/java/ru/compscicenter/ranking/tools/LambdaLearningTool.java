package ru.compscicenter.ranking.tools;

import ru.compscicenter.ranking.Target;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.FeatureRow;
import ru.compscicenter.ranking.data.RichData;
import ru.compscicenter.ranking.data.WeightCalculator;
import ru.compscicenter.ranking.ensembles.Ensemble;
import ru.compscicenter.ranking.trees.*;
import ru.compscicenter.ranking.utils.Evaluator;
import ru.compscicenter.ranking.ensembles.GradientBoosting;

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
        Target target = new Target(new VarianceSplitter());
        RegressionTreeTrainer regressionTreeTrainer =
                new RegressionTreeTrainer(target, 5);
        GradientBoosting.Builder<RegressionTree> builder =
                new GradientBoosting.Builder<>(regressionTreeTrainer);
        GradientBoosting<RegressionTree> gradientBoosting =
                builder.shrinkage(0.1).bootstrapRatio(0.5).build();

        WeightCalculator weightCalculator = new WeightCalculator();
        RichData richData = new RichData(learningSet, weightCalculator.calculateWeights(learningSet));
        model = gradientBoosting.train(richData, stepNumber);

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

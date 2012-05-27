package ru.compscicenter.ranking.tools;

import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.ensembles.Ensemble;
import ru.compscicenter.ranking.ensembles.GradientBoosting;
import ru.compscicenter.ranking.lambdarank.LambdaRankTarget;
import ru.compscicenter.ranking.Target;
import ru.compscicenter.ranking.lambdarank.LambdaRankEstimator;
import ru.compscicenter.ranking.trees.RegressionTree;
import ru.compscicenter.ranking.trees.RegressionTreeTrainer;
import ru.compscicenter.ranking.trees.VarianceTreeSplitter;
import ru.compscicenter.ranking.utils.Evaluator;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public class LambdaRankLearningTool implements LearningTool {

    @Override
    public String getDescription() {
        return "Gradient tree boosting model";
    }

    @Override
    public RegressionModel trainModel(DataSet learningSet, Outputs relevance, int stepNumber) {
        VarianceTreeSplitter splitter = new VarianceTreeSplitter();
        LambdaRankEstimator estimator = new LambdaRankEstimator();
        RegressionTreeTrainer regressionTreeTrainer =
                new RegressionTreeTrainer(splitter, estimator, 5);

        Target target = new LambdaRankTarget(learningSet, relevance);

        GradientBoosting.Builder<RegressionTree> builder =
                new GradientBoosting.Builder<>(regressionTreeTrainer, target);
        GradientBoosting<RegressionTree> gradientBoosting =
                builder.shrinkage(0.1).bootstrapRatio(0.5).build();

        return gradientBoosting.train(learningSet, relevance, stepNumber);
    }
}

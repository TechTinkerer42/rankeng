package ru.compscicenter.ranking.tools;

import ru.compscicenter.ml.ranking.data.DataSet;
import ru.compscicenter.ml.ranking.data.FeatureRow;
import ru.compscicenter.ml.ranking.evaluation.EvaluationTool;
import ru.compscicenter.ml.ranking.trees.AdditiveTrees;
import ru.compscicenter.ml.ranking.trees.AdditiveTreesLearner;
import ru.compscicenter.ml.ranking.trees.ConfusionWeightCalculator;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public class LambdaConfusionLearningTool implements LearningTool {

    private AdditiveTrees model;

    @Override
    public String getDescription() {
        return "Gradient tree boosting model (with confusion matrix)";
    }

    @Override
    public void learn(DataSet learningSet, int stepNumber) {
        AdditiveTreesLearner treesLearner = new AdditiveTreesLearner();
        treesLearner.setMinNumPerLeaf(20);
        treesLearner.setSampleRatio(0.5);
        treesLearner.setShrinkage(0.1);
        treesLearner.setMaxDepth(7);
        treesLearner.setWeightCalculator(new ConfusionWeightCalculator());

        model = treesLearner.learn(learningSet, stepNumber);
        EvaluationTool.evaluate("Final model (learning): ", model, learningSet);
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

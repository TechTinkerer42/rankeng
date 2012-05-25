package ru.compscicenter.ml.ranking.tools;

import ru.compscicenter.ml.ranking.data.DataSet;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public interface LearningTool {
    String getDescription();
    void learn(DataSet learningSet, int stepNumber);
    double[] predict(DataSet testSet);
}

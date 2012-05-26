package ru.compscicenter.ranking.tools;

import ru.compscicenter.ranking.data.DataSet;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public interface LearningTool {
    String getDescription();
    void learn(DataSet learningSet, int stepNumber);
    double[] predict(DataSet testSet);
}

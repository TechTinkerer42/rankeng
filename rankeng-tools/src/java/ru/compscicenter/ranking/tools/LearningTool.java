package ru.compscicenter.ranking.tools;

import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Outputs;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   25.04.12
 */
public interface LearningTool {
    String getDescription();
    RegressionModel trainModel(DataSet dataSet, Outputs relevance, int stepNumber);
}

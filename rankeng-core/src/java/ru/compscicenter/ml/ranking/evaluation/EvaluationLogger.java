package ru.compscicenter.ml.ranking.evaluation;

import org.apache.log4j.Logger;
import ru.compscicenter.ml.ranking.data.DataSet;
import ru.compscicenter.ml.ranking.data.FeatureRow;
import ru.compscicenter.ml.ranking.trees.AdditiveTrees;
import ru.compscicenter.ml.ranking.utils.CommonUtils;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   24.04.12
 */
public class EvaluationLogger {

    private static final Logger logger = Logger.getLogger(EvaluationLogger.class);

    private final DataSet testSet;

    public EvaluationLogger(DataSet testSet) {
        this.testSet = testSet;
    }

    public void evaluate(String message, AdditiveTrees predictor) {
        double[] predictions = new double[testSet.numberOfRows()];
        int index = 0;
        for (FeatureRow featureRow : testSet.getRowList()) {
            predictions[index] = predictor.predict(featureRow);
            index++;
        }
        evaluate(message, predictions);
    }

    public void evaluate(String message, double[] predictions) {
        if (testSet.queries().isEmpty()) {
            logger.error("Evaluation - Test set is empty");
        }

        String text = message + "DCG=" + CommonUtils.calculateDCG(testSet, predictions);
        logger.debug(text);
    }
}

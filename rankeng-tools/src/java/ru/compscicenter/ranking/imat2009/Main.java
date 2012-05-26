package ru.compscicenter.ranking.imat2009;

import org.apache.log4j.Logger;
import ru.compscicenter.ranking.data.DataProvider;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.tools.LearningTool;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date:   24.04.12
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    private static final String packageName = Main.class.getPackage().getName();

    public static void main(String[] args) throws Exception {
        int stepNumber = Integer.parseInt(args[1]);
        String toolName = args[0];

        DataProvider learningDataProvider = new IMat2009DataProvider("data/imat2009-datasets/imat2009_learning.txt");
        DataSet learningData = learningDataProvider.loadData();

        LearningTool learningTool = (LearningTool) Class.forName(packageName + "." + toolName).newInstance();
        learningTool.learn(learningData, stepNumber);

        // TODO: don't use logger - write to file
        logger.info(learningTool.getDescription());

        DataProvider testDataProvider = new IMat2009DataProvider("data/imat2009-datasets/imat2009_test.txt");
        DataSet testSet = testDataProvider.loadData();
        for (double prediction : learningTool.predict(testSet)) {
            logger.info(prediction);
        }
    }
}

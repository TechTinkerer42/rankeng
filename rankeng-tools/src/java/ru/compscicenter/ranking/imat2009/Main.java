package ru.compscicenter.ranking.imat2009;

import org.apache.log4j.Logger;
import ru.compscicenter.ranking.data.DataLoader;
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

        DataLoader learningDataLoader = new IMat2009DataLoader("data/imat2009-datasets/imat2009_learning.txt");
        DataSet learningData = learningDataLoader.loadData();

        LearningTool learningTool = (LearningTool) Class.forName(packageName + "." + toolName).newInstance();
        learningTool.learn(learningData, stepNumber);

        // TODO: don't use logger - write to file
        logger.info(learningTool.getDescription());

        DataLoader testDataLoader = new IMat2009DataLoader("data/imat2009-datasets/imat2009_test.txt");
        DataSet testSet = testDataLoader.loadData();
        for (double prediction : learningTool.predict(testSet)) {
            logger.info(prediction);
        }
    }
}

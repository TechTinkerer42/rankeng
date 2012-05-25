package ru.compscicenter.ranking.tools;

import ru.compscicenter.ranking.imat2009.DataLoader;
import ru.compscicenter.ml.ranking.data.DataSet;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date:   24.04.12
 */
public class Main {

    private static final String packageName = "ru.compscicenter.ranking.tools";

    public static void main(String[] args) throws Exception {
        int stepNumber = Integer.parseInt(args[1]);
        String toolName = args[0];

        DataLoader dataLoader = new DataLoader();
        DataSet learningData = dataLoader.load("data/imat2009-datasets/imat2009_learning.txt");

        LearningTool learningTool = (LearningTool) Class.forName(packageName + "." + toolName).newInstance();
        learningTool.learn(learningData, stepNumber);

        System.out.println(learningTool.getDescription());

        DataSet testSet = dataLoader.load("data/imat2009-datasets/imat2009_test.txt");
        for (double prediction : learningTool.predict(testSet)) {
            System.out.println(prediction);
        }
    }
}

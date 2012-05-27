package ru.compscicenter.ranking.imat2009;

import org.apache.log4j.Logger;
import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.DataLoader;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.tools.LearningTool;
import ru.compscicenter.ranking.utils.Evaluator;
import ru.compscicenter.ranking.utils.Pair;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date:   24.04.12
 */
public class Main {

    private Main() {
        // Do nothing
    }

    private static final Logger logger = Logger.getLogger(Main.class);

    private static final String packageName = "ru.compscicenter.ranking.tools";

    public static void main(String[] args) throws Exception {
        int stepNumber = Integer.parseInt(args[1]);
        String toolName = args[0];

        logger.info("Loading learning data");
        DataLoader dataLoader = new IMat2009DataLoader("data/imat2009-datasets/imat2009_learning.txt");
        Pair<DataSet, Outputs> pair = dataLoader.loadData();

        LearningTool learningTool = (LearningTool) Class.forName(packageName + "." + toolName).newInstance();
        logger.info("Training a model (learning tool: " + toolName + ")");

        RegressionModel model = learningTool.trainModel(pair.first(), pair.second(), stepNumber);
        logger.info("The model was successfully trained");

        logger.info("Loading testing data");
        DataLoader testDataLoader = new IMat2009DataLoader("data/imat2009-datasets/imat2009_test.txt");
        DataSet testSet = testDataLoader.loadData().first();

        logger.info("Making predictions using trained model");
        List<Double> predictions = new ArrayList<>();
        for (int index = 0; index < testSet.size(); index++) {
            predictions.add(null);
        }
        for (Instance instance : testSet) {
            predictions.set(instance.getId(), model.predict(instance));
        }

        String outputFileName = "data/predictions/" + System.currentTimeMillis() + ".predictions";
        logger.info("Save predictions to file: " + outputFileName);

        try (PrintWriter writer = new PrintWriter(outputFileName)) {
            writer.println(learningTool.getDescription());
            writer.println("Predictions:");

            for (double prediction : predictions) {
                writer.println(prediction);
            }
        }
        logger.info("Predictions were saved");

        Evaluator evaluator = new Evaluator(pair.first(), pair.second());
        evaluator.evaluate("Final model (learning): ", model);
    }
}

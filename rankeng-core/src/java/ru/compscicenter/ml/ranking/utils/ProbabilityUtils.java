package ru.compscicenter.ml.ranking.utils;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   22.04.12
 */
public class ProbabilityUtils {

    private ProbabilityUtils() {
        // Do nothing
    }

    public static double mean(double[] data) {
        double sum = 0.0;
        for (double d : data) {
            sum += d;
        }
        return sum / data.length;
    }

    public static double variance(double[] data) {
        double[] newData = new double[data.length];
        double mean = mean(data);
        for (int index = 0; index < data.length; index++) {
            newData[index] = (data[index] - mean) * (data[index] - mean);
        }
        return mean(newData);
    }
}

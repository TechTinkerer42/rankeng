package ru.compscicenter.ranking.data;

import ru.compscicenter.ranking.utils.Pair;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/25/12
 */
public class RichData {

    private final DataSet dataSet;
    private final double[][][] weights;

    public RichData(DataSet dataSet, double[][][] weights) {
        this.dataSet = dataSet;
        this.weights = weights;
    }

    public Pair<RichData, RichData> split(double ratio, int RANDOM_SEED) {
        Pair<DataSet, DataSet> dataSets = dataSet.split(ratio, RANDOM_SEED);
        return new Pair<>(
                new RichData(dataSets.first(), weights),
                new RichData(dataSets.second(), weights)
        );
    }

    public Pair<RichData, RichData> split(Integer splitIndex, Double splitValue) {
        Pair<DataSet, DataSet> dataSets = dataSet.split(splitIndex, splitValue);
        return new Pair<>(
                new RichData(dataSets.first(), weights),
                new RichData(dataSets.second(), weights)
        );
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public double[][][] getWeights() {
        return weights;
    }
}

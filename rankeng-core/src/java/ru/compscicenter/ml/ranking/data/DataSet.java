package ru.compscicenter.ml.ranking.data;

import ru.compscicenter.ml.ranking.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   18.04.12
 */
public class DataSet {

    private final List<List<Integer>> queries;
    private final double[][] features;
    private final double[] relevance;

    public DataSet(List<List<Integer>> queries, double[][] features, double[] relevance) {
        this.features = features;
        this.queries = queries;
        this.relevance = relevance;
    }

    public Pair<DataSet, DataSet> split(double ratio, int RANDOM_SEED) {
        List<List<Integer>> first = new ArrayList<>();
        List<List<Integer>> second = new ArrayList<>();

        Random random = new Random(RANDOM_SEED);
        for (List<Integer> query : queries) {
            if (random.nextDouble() < ratio) {
                first.add(query);
                second.add(Collections.<Integer>emptyList());
            } else {
                first.add(Collections.<Integer>emptyList());
                second.add(query);
            }
        }
        return new Pair<>(new DataSet(first, features, relevance), new DataSet(second, features, relevance));
    }

    public Pair<DataSet, DataSet> split(Integer splitIndex, Double splitValue) {
        List<List<Integer>> lessOrEquals = new ArrayList<>();
        List<List<Integer>> greater = new ArrayList<>();

        for (List<Integer> query : queries()) {
            List<Integer> currentLOE = new ArrayList<>();
            List<Integer> currentGreater = new ArrayList<>();
            for (Integer doc : query) {
                if (getRow(doc).valueAt(splitIndex) <= splitValue) {
                    currentLOE.add(doc);
                } else {
                    currentGreater.add(doc);
                }
            }
            lessOrEquals.add(currentLOE);
            greater.add(currentGreater);
        }
        DataSet resultFirst = new DataSet(lessOrEquals, features, relevance);
        DataSet resultSecond = new DataSet(greater, features, relevance);

        return new Pair<>(resultFirst, resultSecond);
    }

    public double[][] features() {
        return features;
    }

    public double[] relevance() {
        return relevance;
    }

    public List<FeatureRow> getRowList() {
        List<FeatureRow> result = new ArrayList<>();
        for (int index = 0; index < features.length; index++) {
            result.add(new FeatureRowImpl(index));
        }
        return result;
    }

    public FeatureRow getRow(int rowIndex) {
        return new FeatureRowImpl(rowIndex);
    }

    public List<List<Integer>> queries() {
        return queries;
    }

    public double relevanceAt(int rowIndex) {
        return relevance[rowIndex];
    }

    public int numberOfFeatures() {
        return features[0].length;
    }

    public int numberOfRows() {
        return features.length;
    }

    private class FeatureRowImpl implements FeatureRow {

        private final int rowIndex;

        private FeatureRowImpl(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        @Override
        public double valueAt(int featureIndex) {
            return features[rowIndex][featureIndex];
        }

        @Override
        public int size() {
            return features[rowIndex].length;
        }
    }
}

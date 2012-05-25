package ru.compscicenter.ml.ranking.utils;

import ru.compscicenter.ml.ranking.data.DataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   19.04.12
 */
public class Utils {

    private Utils() {
        // Do nothing
    }

    public static List<Integer> sortByPredictions(List<Integer> query, double[] predictions) {
        List<Pair<Integer, Double>> pairs = new ArrayList<>();
        for (int doc : query) {
            pairs.add(new Pair<>(doc, -predictions[doc]));
        }
        PairUtils.sortBySecond(pairs);
        return PairUtils.firsts(pairs);
    }

    public static double calculateDCG(DataSet dataSet, double[] predictions) {
        double sum = 0.0;
        double size = 0;
        for (List<Integer> query : dataSet.queries()) {
            int i = 1;
            for (Integer doc : sortByPredictions(query, predictions)) {
                sum += dataSet.relevanceAt(doc) / (Math.log(i) / Math.log(2) + 1);
                i++;
            }
            if (!query.isEmpty()) {
                size++;
            }
        }
        return sum / size;
    }

    public static double calculateL(double[][][] w, DataSet dataSet, double[] predictions) {
        double sum = 0.0;
        double size = 0;
        int queryIndex = 0;
        for (List<Integer> query : dataSet.queries()) {
            for (int i = 0; i < query.size(); i++) {
                for (int j = 0; j < query.size(); j++) {
                    double exi = Math.exp(predictions[query.get(i)]);
                    double exj = Math.exp(predictions[query.get(j)]);
                    sum += w[queryIndex][i][j] * Math.log(exi / (exi + exj));
                }
            }
            if (!query.isEmpty()) {
                size++;
            }
            queryIndex++;
        }
        return -sum;
//        return -sum / size;
    }

    public static Map<Double, Double> averageDCGPerLabel(DataSet dataSet) {
        Map<Double, List<Double>> dcgLists = new HashMap<>();
        for (List<Integer> query : dataSet.queries()) {
            List<Integer> sortedQuery = sortByPredictions(query, dataSet.relevance());
            int index = 1;
            for (Integer doc : sortedQuery) {
                List<Double> queryDcgList = dcgLists.get(dataSet.relevanceAt(doc));
                if (queryDcgList == null) {
                    queryDcgList = new ArrayList<>();
                    dcgLists.put(dataSet.relevanceAt(doc), queryDcgList);
                }
                double value = dataSet.relevanceAt(doc) / (Math.log(index) / Math.log(2) + 1);
                queryDcgList.add(value);
                index++;
            }
        }
        Map<Double, Double> result = new HashMap<>();
        for (Map.Entry<Double, List<Double>> entry : dcgLists.entrySet()) {
            double sumDcg = 0;
            for (Double dcg : entry.getValue()) {
                sumDcg += dcg;
            }
            result.put(entry.getKey(), sumDcg / entry.getValue().size());
        }
        return result;
    }
}

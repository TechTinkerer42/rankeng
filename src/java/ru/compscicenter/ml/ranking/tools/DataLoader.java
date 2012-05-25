package ru.compscicenter.ml.ranking.tools;

import ru.compscicenter.ml.ranking.data.DataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   24.04.12
 */
public class DataLoader {

    public DataSet load(String inputFile) throws IOException {
        List<List<Double>> valuesList = new ArrayList<>();
        List<Double> relevance = new ArrayList<>();
        List<Integer> queries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                valuesList.add(parseFeatures(line));
                relevance.add(parseRelevance(line));
                queries.add(parseQueries(line));
            }
        }

        List<List<Integer>> queriesDocs = queriesDocs(queries);
        double[][] features = featuresAsArray(valuesList);
        double[] relevanceArray = relevanceAsArray(relevance);

        return new DataSet(queriesDocs, features, relevanceArray);
    }

    Set<Double> relevanceValues(DataSet dataSet) {
        Set<Double> result = new HashSet<>();
        for (double r : dataSet.relevance()){
            result.add(r);
        }
        return result;
    }


    public List<List<Integer>> queriesDocs(List<Integer> queryList) {
        Map<Integer, List<Integer>> queriesDocs = new LinkedHashMap<>();
        for (int index = 0; index < queryList.size(); index++) {
            List<Integer> queryDocs = queriesDocs.get(queryList.get(index));
            if (queryDocs == null) {
                queryDocs = new ArrayList<>();
                queriesDocs.put(queryList.get(index), queryDocs);
            }
            queryDocs.add(index);
        }

        List<List<Integer>> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : queriesDocs.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    private double[] relevanceAsArray(List<Double> relevance) {
        double[] result = new double[relevance.size()];
        int index = 0;
        for (double d : relevance) {
            result[index] = d;
            index++;
        }
        return result;
    }

    public static Integer parseQueries(String line) {
        String[] tokens = line.split(" ");
        Integer query = null;
        boolean isQuery = false;
        for (String token : tokens) {
            if ("#".equals(token)) {
                isQuery = true;
                continue;
            }
            if (isQuery) {
                query = Integer.parseInt(token);
            }
        }
        assert query != null;

        return query;
    }

    public static Double parseRelevance(String line) {
        return Double.parseDouble(line.split(" ")[0]);
    }

    public static List<Double> parseFeatures(String line) {
        List<Double> featureValues = new ArrayList<>();
        for (int index = 0; index < 245; index++) {
            featureValues.add(0.0);
        }
        String[] tokens = line.split(" ");
        for (String token : tokens) {
            if (token.contains(":")) {
                String[] featureDetails = token.split(":");
                featureValues.set(Integer.parseInt(featureDetails[0]) - 1, Double.parseDouble(featureDetails[1]));
            }
        }
        return featureValues;
    }

    private static double[][] featuresAsArray(List<List<Double>> valuesList) {
        double[][] result = new double[valuesList.size()][];
        int i = 0;
        for (List<Double> valuesRaw : valuesList) {
            result[i] = new double[valuesRaw.size()];
            int j = 0;
            for (double value : valuesRaw) {
                result[i][j] = value;
                j++;
            }
            i++;
        }
        return result;
    }
}
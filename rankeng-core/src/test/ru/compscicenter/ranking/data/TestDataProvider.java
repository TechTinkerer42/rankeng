package ru.compscicenter.ranking.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/25/12
 */
public class TestDataProvider implements DataProvider {

    private final String inputFile;
    private final int numberOfLines;
    private final int numberOfFeatures;

    public TestDataProvider(String inputFile, int numberOfLines, int numberOfFeatures) {
        this.inputFile = inputFile;
        this.numberOfLines = numberOfLines;
        this.numberOfFeatures = numberOfFeatures;
    }

    @Override
    public DataSet loadData() throws IOException {

        double[] relevance = new double[numberOfLines];
        double[][] features = new double[numberOfLines][numberOfFeatures];
        Map<Integer, List<Integer>> queries = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            int index = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                relevance[index] = Double.parseDouble(tokens[0]);
                for (int featureIndex = 0; featureIndex < numberOfFeatures; featureIndex++) {
                    features[index][featureIndex] = Double.parseDouble(tokens[featureIndex + 1].split(":")[1]);
                }
                Integer queryIndex = Integer.parseInt(tokens[tokens.length - 1]);
                List<Integer> queryDocs = queries.get(queryIndex);
                if (queryDocs == null) {
                    queryDocs = new ArrayList<>();
                    queries.put(queryIndex, queryDocs);
                }
                queryDocs.add(index);

                index++;
            }
        }
        return new DataSet(getQueriesAsList(queries), features, relevance);
    }

    private List<List<Integer>> getQueriesAsList(Map<Integer, List<Integer>> queriesMap) {
        List<List<Integer>> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> queryDocs : queriesMap.entrySet()) {
            result.add(queryDocs.getValue());
        }
        return result;
    }
}

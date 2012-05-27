package ru.compscicenter.ranking.data;

import ru.compscicenter.ranking.utils.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/25/12
 */
public class TestDataLoader implements DataLoader {

    private final String inputFile;
    private final int numberOfLines;
    private final int numberOfFeatures;

    public TestDataLoader(String inputFile, int numberOfLines, int numberOfFeatures) {
        this.inputFile = inputFile;
        this.numberOfLines = numberOfLines;
        this.numberOfFeatures = numberOfFeatures;
    }

    @Override
    public Pair<DataSet, Outputs> loadData() throws IOException {

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
        DataSet dataSet = new DataSet(getQueriesAsList(queries, features), numberOfFeatures);
        Map<Instance, Double> relevanceMap = new HashMap<>();
        for (Instance instance : dataSet) {
            relevanceMap.put(instance, relevance[instance.getId()]);
        }

        Outputs outputs = new Outputs(relevanceMap);

        return new Pair<>(dataSet, outputs);
    }

    private List<Query> getQueriesAsList(Map<Integer, List<Integer>> queriesMap, double[][] features) {
        List<Query> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : queriesMap.entrySet()) {
            List<Instance> queryInstances = new ArrayList<>();
            for (Integer instanceId : entry.getValue()) {
                queryInstances.add(new Instance(instanceId, entry.getKey(), features[instanceId]));
            }
            result.add(new Query(queryInstances));
        }
        return result;
    }
}

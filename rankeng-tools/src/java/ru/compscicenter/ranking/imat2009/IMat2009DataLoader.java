package ru.compscicenter.ranking.imat2009;

import ru.compscicenter.ranking.data.DataLoader;
import ru.compscicenter.ranking.data.DataSet;
import ru.compscicenter.ranking.data.Instance;
import ru.compscicenter.ranking.data.Outputs;
import ru.compscicenter.ranking.data.Query;
import ru.compscicenter.ranking.utils.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   24.04.12
 */
public class IMat2009DataLoader implements DataLoader {

    private final String inputFile;

    public IMat2009DataLoader(String inputFile) {
        this.inputFile = inputFile;
    }

    @Override
    public Pair<DataSet, Outputs> loadData() throws IOException {
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

        double[][] features = featuresAsArray(valuesList);
        List<Query> queriesDocs = queriesDocs(queries, features);

        DataSet dataSet = new DataSet(queriesDocs, features[0].length);
        Map<Instance, Double> relevanceAsMap = new HashMap<>();
        for (Instance instance : dataSet) {
            relevanceAsMap.put(instance, relevance.get(instance.getId()));
        }

        Outputs outputs = new Outputs(relevanceAsMap);

        return new Pair<>(dataSet, outputs);
    }

    private List<Query> queriesDocs(List<Integer> queryList, double[][] features) {
        Map<Integer, List<Instance>> queriesInstances = new LinkedHashMap<>();
        for (int index = 0; index < queryList.size(); index++) {
            Integer queryIndex = queryList.get(index);
            List<Instance> queryInstances = queriesInstances.get(queryIndex);
            if (queryInstances == null) {
                queryInstances = new ArrayList<>();
                queriesInstances.put(queryIndex, queryInstances);
            }
            queryInstances.add(new Instance(index, queryIndex, features[index]));
        }

        List<Query> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Instance>> entry : queriesInstances.entrySet()) {
            result.add(new Query(entry.getValue()));
        }

        return result;
    }

    private Integer parseQueries(String line) {
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

    private Double parseRelevance(String line) {
        return Double.parseDouble(line.split(" ")[0]);
    }

    private List<Double> parseFeatures(String line) {
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

    private double[][] featuresAsArray(List<List<Double>> valuesList) {
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
package ru.compscicenter.ranking.data;

import ru.compscicenter.ranking.utils.Pair;

import java.util.*;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   18.04.12
 */
public class DataSet implements Iterable<Instance> {

    private final List<Query> queries;
    private final int size;
    private final int numberOfFeatures;

    public DataSet(List<Query> queries, int numberOfFeatures) {
        this.queries = queries;
        int counter = 0;
        for (Query query : queries) {
            counter += query.size();
        }
        this.size = counter;
        this.numberOfFeatures = numberOfFeatures;
    }

    public Pair<DataSet, DataSet> split(double ratio, int RANDOM_SEED) {
        List<Query> first = new ArrayList<>();
        List<Query> second = new ArrayList<>();

        Random random = new Random(RANDOM_SEED);
        for (Query query : queries) {
            if (random.nextDouble() < ratio) {
                first.add(query);
                second.add(new Query(Collections.<Instance>emptyList()));
            } else {
                first.add(new Query(Collections.<Instance>emptyList()));
                second.add(query);
            }
        }

        return new Pair<>(new DataSet(first, numberOfFeatures), new DataSet(second, numberOfFeatures));
    }

    public Pair<DataSet, DataSet> split(Integer splitIndex, Double splitValue) {
        List<Query> lessOrEquals = new ArrayList<>();
        List<Query> greater = new ArrayList<>();

        for (Query query : queries()) {
            List<Instance> currentLOE = new ArrayList<>();
            List<Instance> currentGreater = new ArrayList<>();
            for (Instance instance : query.getInstances()) {
                if (instance.featureValue(splitIndex) <= splitValue) {
                    currentLOE.add(instance);
                } else {
                    currentGreater.add(instance);
                }
            }
            lessOrEquals.add(new Query(currentLOE));
            greater.add(new Query(currentGreater));
        }
        DataSet resultFirst = new DataSet(lessOrEquals, numberOfFeatures);
        DataSet resultSecond = new DataSet(greater, numberOfFeatures);

        return new Pair<>(resultFirst, resultSecond);
    }

    public List<Query> queries() {
        return queries;
    }

    public int numberOfFeatures() {
        return numberOfFeatures;
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator<Instance> iterator() {
        return new InstanceIterator();
    }

    private class InstanceIterator implements Iterator<Instance> {

        private boolean hasNext;
        private Instance next;

        private int queryIndex = 0;
        private Iterator<Instance> innerIterator;

        private InstanceIterator() {
            updateIterator();
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Instance next() {
            Instance result = next;
            updateNext();
            return result;
        }

        private void updateNext() {
            if (innerIterator != null && innerIterator.hasNext()) {
                next = innerIterator.next();
                hasNext = true;

                return;
            }
            queryIndex++;
            updateIterator();
        }

        private void updateIterator() {
            while (queryIndex < queries.size() && queries.get(queryIndex).isEmpty()) {
                queryIndex++;
            }
            if (queryIndex == queries.size()) {
                next = null;
                hasNext = false;

                return;
            }
            innerIterator = queries.get(queryIndex).getInstances().iterator();
            next = innerIterator.next();
            hasNext = true;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

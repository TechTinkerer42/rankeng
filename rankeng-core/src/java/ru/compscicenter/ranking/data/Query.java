package ru.compscicenter.ranking.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class Query {

    private final List<Instance> instances;

    public Query(List<Instance> instances) {
        this.instances = instances;
    }

    public int size() {
        return instances.size();
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public boolean isEmpty() {
        return instances.isEmpty();
    }
}

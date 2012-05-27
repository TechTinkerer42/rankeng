package ru.compscicenter.ranking.data;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public class Instance {

    private final int instanceId;
    private final int queryId;
    private final double[] features;

    public Instance(int instanceId, int queryId, double[] features) {
        this.instanceId = instanceId;
        this.queryId = queryId;
        this.features = features;
    }

    public int getId() {
        return instanceId;
    }

    public int getQueryId() {
        return queryId;
    }

    public double featureValue(int featureIndex) {
        return features[featureIndex];
    }
}

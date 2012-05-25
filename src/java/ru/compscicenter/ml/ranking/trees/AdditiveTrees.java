package ru.compscicenter.ml.ranking.trees;

import ru.compscicenter.ml.ranking.data.FeatureRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   19.04.12
 */
public class AdditiveTrees implements Serializable {

    // TODO: must be private
    List<RegressionTree> treeEnsemble;
    List<Double> treeWeights;

    public AdditiveTrees(List<Double> treeWeights, List<RegressionTree> treeEnsemble) {
        this.treeEnsemble = new ArrayList<>(treeEnsemble);
        this.treeWeights = new ArrayList<>(treeWeights);
    }

    public Double predict(FeatureRow featureRow) {
        double result = 0;
        int index = 0;
        for (RegressionTree innerModel : treeEnsemble) {
            result += treeWeights.get(index) * innerModel.predict(featureRow);
            index++;
        }
        return result;
    }
}

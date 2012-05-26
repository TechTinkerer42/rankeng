package ru.compscicenter.ranking.trees;

import ru.compscicenter.ranking.RegressionModel;
import ru.compscicenter.ranking.data.FeatureRow;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   23.04.12
 */
public class RegressionTree implements RegressionModel {

    private final RegressionNode root;

    public RegressionTree(RegressionNode root) {
        this.root = root;
    }

    public static RegressionNode makeLeaf(double value) {
        return new RegressionNode(value, -1, Double.NaN, null, null);
    }

    public static RegressionNode makeInnerNode(
            int splitIndex, double splitValue,
            RegressionNode lessOrEqual, RegressionNode greater
    ) {
        return new RegressionNode(Double.NaN, splitIndex, splitValue, lessOrEqual, greater);
    }

    @Override
    public double predict(FeatureRow featureRow) {
        RegressionNode current = root;
        while (!current.isLeaf()) {
            if (featureRow.valueAt(current.splitIndex) <= current.splitValue) {
                current = current.lessOrEqualNode();
            } else {
                current = current.greaterNode();
            }
        }
        return current.value();
    }

    public RegressionNode getRoot() {
        return root;
    }

    public static class RegressionNode {

        private final double value;
        private final int splitIndex;
        private final double splitValue;

        private final RegressionNode lessOrEqual;
        private final RegressionNode greater;

        private RegressionNode(
                double value, int splitIndex, double splitValue,
                RegressionNode lessOrEqual, RegressionNode greater
        ) {
            this.value = value;
            this.splitIndex = splitIndex;
            this.splitValue = splitValue;
            this.lessOrEqual = lessOrEqual;
            this.greater = greater;
        }

        public double value() {
            return value;
        }

        public boolean isLeaf() {
            return lessOrEqual == null;
        }

        public int splitIndex() {
            return splitIndex;
        }

        public double splitValue() {
            return splitValue;
        }

        public RegressionNode lessOrEqualNode() {
            return lessOrEqual;
        }

        public RegressionNode greaterNode() {
            return greater;
        }
    }
}

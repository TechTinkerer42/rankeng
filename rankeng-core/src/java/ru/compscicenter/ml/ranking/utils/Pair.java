package ru.compscicenter.ml.ranking.utils;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   19.04.12
 */
public class Pair<A, B> {

    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }
}

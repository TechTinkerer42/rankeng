package ru.compscicenter.ranking.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date:   23.04.12
 */
public class PairUtils {

    private PairUtils() {
        // Do nothing
    }

    public static <A, B> List<A> firsts(List<Pair<A, B>> pairs) {
        List<A> result = new ArrayList<>();
        for (Pair<A, B> pair : pairs) {
            result.add(pair.first());
        }
        return result;
    }

    public static <A, B> List<B> seconds(List<Pair<A, B>> pairs) {
        List<B> result = new ArrayList<>();
        for (Pair<A, B> pair : pairs) {
            result.add(pair.second());
        }
        return result;
    }

    public static <A, B extends Comparable<B>> int minIndexOfSecond(List<Pair<A, B>> pairs) {
        int result = 0;
        B minValue = pairs.get(0).second();
        int index = 0;
        for (Pair<A, B> pair : pairs) {
            if (minValue.compareTo(pair.second()) > 0) {
                minValue = pair.second();
                result = index;
            }
            index++;
        }
        return result;
    }

    public static <A, B extends Comparable<B>> void sortBySecond(List<Pair<A, B>> pairs) {
        Collections.sort(pairs, new Comparator<Pair<A, B>>() {
            @Override
            public int compare(Pair<A, B> o1, Pair<A, B> o2) {
                return o1.second.compareTo(o2.second());
            }
        });
    }

}

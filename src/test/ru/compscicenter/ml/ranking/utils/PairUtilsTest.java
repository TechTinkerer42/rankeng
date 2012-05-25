package ru.compscicenter.ml.ranking.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   23.04.12
 */
public class PairUtilsTest {

    @Test
    public void testSortBySecond() {
        List<Pair<String, Integer>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("ABC", 3));
        pairs.add(new Pair<>("B", 1));
        pairs.add(new Pair<>("AB", 2));
        pairs.add(new Pair<>("TODO", 4));
        pairs.add(new Pair<>("DBE", 3));
        pairs.add(new Pair<>("A", 1));

        List<Integer> seconds = new ArrayList<>();
        seconds.add(1);
        seconds.add(1);
        seconds.add(2);
        seconds.add(3);
        seconds.add(3);
        seconds.add(4);

        List<String> firsts = new ArrayList<>();
        firsts.add("B");
        firsts.add("A");
        firsts.add("AB");
        firsts.add("ABC");
        firsts.add("DBE");
        firsts.add("TODO");

        PairUtils.sortBySecond(pairs);
        Assert.assertEquals("Sorted first values", firsts, PairUtils.firsts(pairs));
        Assert.assertEquals("Sorted second values", seconds, PairUtils.seconds(pairs));
    }
}

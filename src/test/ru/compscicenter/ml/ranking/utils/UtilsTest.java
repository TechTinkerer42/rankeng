package ru.compscicenter.ml.ranking.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Vasiliy Homutov - vasiliy.homutov@gmail.com
 * Date:   19.04.12
 */
public class UtilsTest {

    @Test
    public void testSort(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        list.add(4);
        list.add(2);
        list.add(3);

        double[] p = new double[] {1, 2, 3, 4, 5};

        List<Integer> sortedList = Utils.sortByPredictions(list, p);
        System.out.println(sortedList.get(0));
        System.out.println(sortedList.get(1));
        System.out.println(sortedList.get(2));
        System.out.println(sortedList.get(3));
        System.out.println(sortedList.get(4));
    }
}

package ru.compscicenter.ranking;

import ru.compscicenter.ranking.data.RichData;

import java.util.List;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/26/12
 */
public interface Target {

    // TODO: интерфейс нелеп!
     double[][] calculate(RichData richData, double[] predictions);
}

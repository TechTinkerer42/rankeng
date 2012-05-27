package ru.compscicenter.ranking.data;

import ru.compscicenter.ranking.utils.Pair;

import java.io.IOException;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/25/12
 */
public interface DataLoader {
    Pair<DataSet, Outputs> loadData() throws IOException;
}

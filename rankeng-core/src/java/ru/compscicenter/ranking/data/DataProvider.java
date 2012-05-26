package ru.compscicenter.ranking.data;

import java.io.IOException;

/**
 * Author: Vasiliy Khomutov - vasiliy.khomutov@gmail.com
 * Date: 5/25/12
 */
public interface DataProvider {
    DataSet loadData() throws IOException;
}

package com.alibaba.druid.pool.ha.selector;


import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.pool.ha.MockDataSource;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;


public class RandomDataSourceSelectorTest {
    private Map<String, DataSource> dataSourceMap;

    private HighAvailableDataSource dataSource;

    @Test
    public void testRandomGet() throws Exception {
        RandomDataSourceSelector selector = new RandomDataSourceSelector(dataSource);
        int[] count = new int[10];
        for (int i = 0; i < 100; i++) {
            MockDataSource dataSource = ((MockDataSource) (selector.get()));
            (count[Integer.parseInt(dataSource.getName())])++;
        }
        for (int i : count) {
            Assert.assertTrue((i > 0));
        }
    }
}


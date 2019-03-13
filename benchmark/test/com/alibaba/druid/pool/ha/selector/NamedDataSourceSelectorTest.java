package com.alibaba.druid.pool.ha.selector;


import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.pool.ha.MockDataSource;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;


public class NamedDataSourceSelectorTest {
    private Map<String, DataSource> dataSourceMap;

    private HighAvailableDataSource dataSource;

    @Test
    public void testEmptyMap() {
        dataSourceMap.clear();
        NamedDataSourceSelector selector = new NamedDataSourceSelector(null);
        Assert.assertNull(selector.get());
        selector = new NamedDataSourceSelector(dataSource);
        Assert.assertNull(selector.get());
    }

    @Test
    public void testOnlyOne() {
        dataSourceMap.remove("foo");
        NamedDataSourceSelector selector = new NamedDataSourceSelector(dataSource);
        for (int i = 0; i < 50; i++) {
            Assert.assertEquals("bar", ((MockDataSource) (selector.get())).getName());
        }
    }

    @Test
    public void testGetByName() throws Exception {
        NamedDataSourceSelector selector = new NamedDataSourceSelector(dataSource);
        Assert.assertNull(selector.get());
        selector.setTarget("foo");
        Assert.assertEquals("foo", ((MockDataSource) (selector.get())).getName());
        selector.resetDataSourceName();
        selector.setDefaultName("bar");
        Assert.assertEquals("bar", ((MockDataSource) (selector.get())).getName());
    }
}


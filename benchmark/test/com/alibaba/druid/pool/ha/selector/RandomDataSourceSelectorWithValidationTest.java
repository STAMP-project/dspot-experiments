package com.alibaba.druid.pool.ha.selector;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;


public class RandomDataSourceSelectorWithValidationTest {
    private HighAvailableDataSource highAvailableDataSource;

    @Test
    public void testOneDataSourceFailAndRecover() throws Exception {
        getValidateThread().setSleepSeconds(3);
        getRecoverThread().setSleepSeconds(3);
        Thread.sleep((30 * 1000));
        DruidDataSource dataSource = ((DruidDataSource) (highAvailableDataSource.getDataSourceMap().get("foo")));
        dataSource.setValidationQuery("select xxx from yyy");
        Thread.sleep((10 * 1000));
        Assert.assertTrue(dataSource.isTestOnReturn());
        for (int i = 0; i < 100; i++) {
            Assert.assertNotEquals(dataSource, highAvailableDataSource.getDataSourceSelector().get());
        }
        dataSource.setValidationQuery(null);
        Thread.sleep((4 * 1000));
        Assert.assertFalse(dataSource.isTestOnReturn());
        int count = 0;
        for (int i = 0; i < 100; i++) {
            if (dataSource == (highAvailableDataSource.getDataSourceSelector().get())) {
                count++;
            }
        }
        Assert.assertTrue((count > 0));
    }

    @Test
    public void testAllDataSourceFail() throws Exception {
        getValidateThread().setSleepSeconds(3);
        getRecoverThread().setSleepSeconds(3);
        Thread.sleep((30 * 1000));
        DruidDataSource foo = ((DruidDataSource) (highAvailableDataSource.getDataSourceMap().get("foo")));
        DruidDataSource bar = ((DruidDataSource) (highAvailableDataSource.getDataSourceMap().get("bar")));
        foo.setValidationQuery("select xxx from yyy");
        bar.setValidationQuery("select xxx from yyy");
        Thread.sleep((10 * 1000));
        Assert.assertTrue(foo.isTestOnReturn());
        Assert.assertTrue(bar.isTestOnReturn());
        int[] count = new int[2];
        for (int i = 0; i < 100; i++) {
            DataSource dataSource = highAvailableDataSource.getDataSourceSelector().get();
            if (foo == dataSource) {
                (count[0])++;
            } else
                if (bar == dataSource) {
                    (count[1])++;
                }

        }
        Assert.assertTrue(((count[0]) > 0));
        Assert.assertTrue(((count[1]) > 0));
    }
}


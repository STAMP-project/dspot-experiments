package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidDataSourceTest7 extends TestCase {
    private DruidDataSource dataSource;

    public void testInitError() throws Exception {
        Assert.assertEquals(0, dataSource.getCreateErrorCount());
        Throwable error = null;
        try {
            dataSource.init();
        } catch (RuntimeException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertTrue(((dataSource.getCreateErrorCount()) > 0));
        dataSource.getCompositeData();
    }
}


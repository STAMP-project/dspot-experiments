package com.alibaba.druid.bvt.pool.basic;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class MaxEvictableIdleTimeMillisTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        Exception error = null;
        try {
            dataSource.setMaxEvictableIdleTimeMillis(1);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(100, dataSource.getMaxEvictableIdleTimeMillis());
    }

    public void test_error2() throws Exception {
        Exception error = null;
        try {
            dataSource.setMaxEvictableIdleTimeMillis(1);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(100, dataSource.getMaxEvictableIdleTimeMillis());
    }

    public void test_max() throws Exception {
        connect(10);
        Assert.assertEquals(10, dataSource.getPoolingCount());
        Thread.sleep(20);
        dataSource.shrink(true);
        Assert.assertEquals(5, dataSource.getPoolingCount());
        Thread.sleep(100);
        dataSource.shrink(true);
        Assert.assertEquals(0, dataSource.getPoolingCount());
    }
}


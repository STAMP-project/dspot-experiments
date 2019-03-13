package com.alibaba.druid.bvt.pool.basic;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class MaxPhyTimeMillisTest extends TestCase {
    private DruidDataSource dataSource;

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


package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class TimeBetweenLogStatsMillisTest2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        dataSource.init();
        Assert.assertEquals(1000, dataSource.getTimeBetweenLogStatsMillis());
    }
}


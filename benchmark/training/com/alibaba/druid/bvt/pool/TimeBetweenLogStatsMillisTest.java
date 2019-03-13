package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class TimeBetweenLogStatsMillisTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        Assert.assertEquals(true, dataSource.isResetStatEnable());
        dataSource.init();
        Assert.assertEquals(1000, dataSource.getTimeBetweenLogStatsMillis());
        Assert.assertEquals(false, dataSource.isResetStatEnable());
        dataSource.resetStat();
        Assert.assertEquals(0, dataSource.getResetCount());
        dataSource.setConnectionProperties("druid.resetStatEnable=true");
        Assert.assertEquals(true, dataSource.isResetStatEnable());
        dataSource.setConnectionProperties("druid.resetStatEnable=false");
        Assert.assertEquals(false, dataSource.isResetStatEnable());
        dataSource.setConnectionProperties("druid.resetStatEnable=xxx");
    }
}


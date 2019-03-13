package com.alibaba.druid.bvt.pool.property;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class PropertyTest_useGlobalDataSourceStat extends TestCase {
    private DruidDataSource dataSource;

    public void test_true() {
        System.setProperty("druid.useGlobalDataSourceStat", "true");
        dataSource = new DruidDataSource();
        Assert.assertTrue(dataSource.isUseGlobalDataSourceStat());
    }

    public void test_false() {
        System.setProperty("druid.useGlobalDataSourceStat", "false");
        dataSource = new DruidDataSource();
        Assert.assertFalse(dataSource.isUseGlobalDataSourceStat());
    }
}


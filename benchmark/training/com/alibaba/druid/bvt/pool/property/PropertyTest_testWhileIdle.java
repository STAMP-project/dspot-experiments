package com.alibaba.druid.bvt.pool.property;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class PropertyTest_testWhileIdle extends TestCase {
    private DruidDataSource dataSource;

    public void test_true() {
        System.setProperty("druid.testWhileIdle", "true");
        dataSource = new DruidDataSource();
        Assert.assertTrue(dataSource.isTestWhileIdle());
    }

    public void test_false() {
        System.setProperty("druid.testWhileIdle", "false");
        dataSource = new DruidDataSource();
        Assert.assertFalse(dataSource.isTestWhileIdle());
    }
}


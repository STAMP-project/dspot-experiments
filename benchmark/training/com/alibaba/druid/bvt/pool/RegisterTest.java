package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class RegisterTest extends TestCase {
    public void test() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.init();
        Assert.assertTrue(dataSource.isMbeanRegistered());
        dataSource.registerMbean();
        Assert.assertTrue(dataSource.isMbeanRegistered());
        dataSource.unregisterMbean();
        Assert.assertFalse(dataSource.isMbeanRegistered());
        Assert.assertFalse(dataSource.isMbeanRegistered());
        dataSource.close();
    }
}


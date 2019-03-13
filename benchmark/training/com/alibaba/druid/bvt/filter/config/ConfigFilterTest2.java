package com.alibaba.druid.bvt.filter.config;


import ConfigFilter.CONFIG_DECRYPT;
import ConfigFilter.CONFIG_FILE;
import ConfigFilter.SYS_PROP_CONFIG_DECRYPT;
import ConfigFilter.SYS_PROP_CONFIG_FILE;
import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConfigFilterTest2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_decrypt() throws Exception {
        dataSource.addConnectionProperty(CONFIG_FILE, "bvt/config/config-0.properties");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        dataSource.init();
        Assert.assertEquals("jdbc:mock:config-0", dataSource.getUrl());
        Assert.assertEquals(false, dataSource.isTestOnBorrow());
        Assert.assertEquals(10, dataSource.getMaxActive());
    }

    public void test_decrypt1() throws Exception {
        dataSource.addConnectionProperty(CONFIG_FILE, "bvt/config/config-1.properties");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        dataSource.init();
        Assert.assertEquals("jdbc:mock:config-1", dataSource.getUrl());
        Assert.assertEquals(false, dataSource.isTestOnBorrow());
        Assert.assertEquals(11, dataSource.getMaxActive());
        Assert.assertEquals(3, dataSource.getProxyFilters().size());
    }

    public void test_decrypt2() throws Exception {
        dataSource.addConnectionProperty(CONFIG_FILE, "bvt/config/config-2.properties");
        dataSource.addConnectionProperty(CONFIG_DECRYPT, "true");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        dataSource.init();
        Assert.assertEquals("jdbc:mock:config-2", dataSource.getUrl());
        Assert.assertEquals(false, dataSource.isTestOnBorrow());
        Assert.assertEquals(12, dataSource.getMaxActive());
        Assert.assertEquals("abcdefg1234567890", dataSource.getPassword());
    }

    public void test_decrypt3() throws Exception {
        dataSource.addConnectionProperty(CONFIG_FILE, "bvt/config/config-3.properties");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        dataSource.init();
        Assert.assertEquals("jdbc:mock:config-3", dataSource.getUrl());
        Assert.assertEquals(false, dataSource.isTestOnBorrow());
        Assert.assertEquals(13, dataSource.getMaxActive());
        Assert.assertEquals("abcdefg1234567890", dataSource.getPassword());
    }

    public void test_decrypt4() throws Exception {
        String file = Thread.currentThread().getContextClassLoader().getResource("bvt/config/config-3.properties").getFile();
        dataSource.addConnectionProperty(CONFIG_FILE, ("file://" + file));
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        dataSource.init();
        Assert.assertEquals("jdbc:mock:config-3", dataSource.getUrl());
        Assert.assertEquals(false, dataSource.isTestOnBorrow());
        Assert.assertEquals(13, dataSource.getMaxActive());
        Assert.assertEquals("abcdefg1234567890", dataSource.getPassword());
    }

    public void test_decrypt5() throws Exception {
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        try {
            String file = Thread.currentThread().getContextClassLoader().getResource("bvt/config/config-2.properties").getFile();
            System.setProperty(SYS_PROP_CONFIG_FILE, ("file://" + file));
            System.setProperty(SYS_PROP_CONFIG_DECRYPT, "true");
            dataSource.init();
            Assert.assertEquals("jdbc:mock:config-2", dataSource.getUrl());
            Assert.assertEquals(false, dataSource.isTestOnBorrow());
            Assert.assertEquals(12, dataSource.getMaxActive());
            Assert.assertEquals("abcdefg1234567890", dataSource.getPassword());
        } finally {
            System.clearProperty(SYS_PROP_CONFIG_FILE);
            System.clearProperty(SYS_PROP_CONFIG_DECRYPT);
        }
    }
}


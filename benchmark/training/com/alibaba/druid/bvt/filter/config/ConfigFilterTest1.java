package com.alibaba.druid.bvt.filter.config;


import ConfigFilter.CONFIG_DECRYPT;
import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConfigFilterTest1 extends TestCase {
    private DruidDataSource dataSource;

    public void test_decrypt() throws Exception {
        String plainPassword = "abcdefg1234567890";
        dataSource.setPassword(ConfigTools.encrypt(plainPassword));
        Assert.assertFalse(plainPassword.equals(dataSource.getPassword()));
        dataSource.addConnectionProperty(CONFIG_DECRYPT, "true");
        dataSource.init();
        Assert.assertEquals(plainPassword, dataSource.getPassword());
    }
}


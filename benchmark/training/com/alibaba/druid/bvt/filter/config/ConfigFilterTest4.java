package com.alibaba.druid.bvt.filter.config;


import ConfigFilter.CONFIG_DECRYPT;
import ConfigFilter.CONFIG_FILE;
import ConfigFilter.CONFIG_KEY;
import ConfigFilter.SYS_PROP_CONFIG_FILE;
import ConfigFilter.SYS_PROP_CONFIG_KEY;
import DruidDataSourceFactory.PROP_PASSWORD;
import DruidDataSourceFactory.PROP_URL;
import DruidDataSourceFactory.PROP_USERNAME;
import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConfigFilterTest4 extends TestCase {
    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        String password = "abcdefg1234";
        String[] keys = ConfigTools.genKeyPair(1024);
        File file = File.createTempFile("MyTest", Long.toString(System.nanoTime()));
        Properties properties = new Properties();
        properties.put(PROP_URL, "jdbc:mock:xx0");
        properties.put(PROP_USERNAME, "sa");
        properties.put(PROP_PASSWORD, ConfigTools.encrypt(keys[0], password));
        properties.put(CONFIG_DECRYPT, "true");
        properties.store(new FileOutputStream(file), "");
        dataSource.getConnectProperties().put(CONFIG_KEY, keys[1]);
        dataSource.getConnectProperties().put(CONFIG_FILE, ("file://" + (file.getAbsolutePath())));
        dataSource.init();
        Assert.assertEquals("jdbc:mock:xx0", dataSource.getUrl());
        Assert.assertEquals("sa", dataSource.getUsername());
        Assert.assertEquals(password, dataSource.getPassword());
    }

    public void test_sys_property() throws Exception {
        String password = "abcdefg1234";
        String[] keys = ConfigTools.genKeyPair(1024);
        File file = File.createTempFile("MyTest", Long.toString(System.nanoTime()));
        Properties properties = new Properties();
        properties.put(PROP_URL, "jdbc:mock:xx0");
        properties.put(PROP_USERNAME, "sa");
        properties.put(PROP_PASSWORD, ConfigTools.encrypt(keys[0], password));
        properties.put(CONFIG_DECRYPT, "true");
        properties.store(new FileOutputStream(file), "");
        System.getProperties().put(SYS_PROP_CONFIG_KEY, keys[1]);
        System.getProperties().put(SYS_PROP_CONFIG_FILE, ("file://" + (file.getAbsolutePath())));
        try {
            dataSource.init();
            Assert.assertEquals("jdbc:mock:xx0", dataSource.getUrl());
            Assert.assertEquals("sa", dataSource.getUsername());
            Assert.assertEquals(password, dataSource.getPassword());
        } finally {
            System.clearProperty(SYS_PROP_CONFIG_KEY);
            System.clearProperty(SYS_PROP_CONFIG_FILE);
        }
    }
}


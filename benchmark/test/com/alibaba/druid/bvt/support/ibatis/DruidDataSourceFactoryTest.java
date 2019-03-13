package com.alibaba.druid.bvt.support.ibatis;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.ibatis.DruidDataSourceFactory;
import java.util.Properties;
import junit.framework.TestCase;


public class DruidDataSourceFactoryTest extends TestCase {
    public void test_facttory() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty("url", "jdbc:mock:xx");
        factory.initialize(properties);
        DruidDataSource dataSource = ((DruidDataSource) (factory.getDataSource()));
        dataSource.close();
    }
}


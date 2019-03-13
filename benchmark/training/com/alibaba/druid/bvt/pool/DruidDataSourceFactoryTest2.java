package com.alibaba.druid.bvt.pool;


import DruidDataSourceFactory.PROP_MAXOPENPREPAREDSTATEMENTS;
import DruidDataSourceFactory.PROP_POOLPREPAREDSTATEMENTS;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import java.util.Properties;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidDataSourceFactoryTest2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_factory() throws Exception {
        Properties properties = new Properties();
        properties.put(PROP_POOLPREPAREDSTATEMENTS, "false");
        properties.put(PROP_MAXOPENPREPAREDSTATEMENTS, "100");
        dataSource = ((DruidDataSource) (DruidDataSourceFactory.createDataSource(properties)));
        Assert.assertFalse(dataSource.isPoolPreparedStatements());
    }
}


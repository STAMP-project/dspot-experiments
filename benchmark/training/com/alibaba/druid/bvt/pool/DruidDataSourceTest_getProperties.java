package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getProperties extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        Assert.assertEquals((-1), dataSource.getProperties().indexOf("xxx"));
        Assert.assertEquals(true, ((dataSource.getProperties().indexOf("******")) != (-1)));
    }
}


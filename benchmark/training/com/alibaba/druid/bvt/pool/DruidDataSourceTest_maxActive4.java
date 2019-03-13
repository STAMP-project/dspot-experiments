package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_maxActive4 extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        dataSource.init();
        Exception error = null;
        try {
            dataSource.setMaxActive(2);
        } catch (IllegalArgumentException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}


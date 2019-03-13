package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????maxActive < 0
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_maxActive2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        Exception error = null;
        try {
            dataSource.init();
        } catch (IllegalArgumentException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}


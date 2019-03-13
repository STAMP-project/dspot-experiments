package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_lastCreateError extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        Assert.assertNull(dataSource.getLastCreateError());
        Assert.assertNull(dataSource.getLastCreateErrorTime());
        Assert.assertEquals(0, dataSource.getLastCreateErrorTimeMillis());
        Exception error = null;
        try {
            dataSource.getConnection(100);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertNotNull(dataSource.getLastCreateError());
        Assert.assertNotNull(dataSource.getLastCreateErrorTime());
        Assert.assertEquals(true, ((dataSource.getLastCreateErrorTimeMillis()) > 0));
    }
}


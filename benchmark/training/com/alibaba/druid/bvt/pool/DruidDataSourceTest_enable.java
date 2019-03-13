package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DataSourceDisableException;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_enable extends TestCase {
    private DruidDataSource dataSource;

    public void test_disable() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        Assert.assertTrue(dataSource.isEnable());
        dataSource.setEnable(false);
        Assert.assertFalse(dataSource.isEnable());
        dataSource.shrink();
        Exception error = null;
        try {
            Connection conn = dataSource.getConnection();
            conn.close();
        } catch (DataSourceDisableException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_disable_() throws Exception {
        dataSource.setEnable(false);
        Assert.assertFalse(dataSource.isEnable());
        Exception error = null;
        try {
            Connection conn = dataSource.getConnection();
            conn.close();
        } catch (DataSourceDisableException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}


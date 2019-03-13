package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_lastError extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        Assert.assertNull(dataSource.getLastError());
        Assert.assertNull(dataSource.getLastErrorTime());
        Assert.assertEquals(0, dataSource.getLastErrorTimeMillis());
        Connection conn = dataSource.getConnection();
        Exception error = null;
        try {
            conn.setAutoCommit(false);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertNotNull(dataSource.getLastError());
        Assert.assertNotNull(dataSource.getLastErrorTime());
        Assert.assertEquals(true, ((dataSource.getLastErrorTimeMillis()) > 0));
    }
}


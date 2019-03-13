package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Connection;
import java.sql.Statement;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_recycle2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_recycle() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("select 1");
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        conn.close();
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_recycle_error() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.setReadOnly(false);
        Statement stmt = conn.createStatement();
        stmt.execute("select 1");
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Exception error = null;
        try {
            conn.close();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNull(error);
        {
            Connection conn2 = dataSource.getConnection();
            conn2.close();
        }
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
}


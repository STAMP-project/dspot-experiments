package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Connection;
import java.sql.Savepoint;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidPooledConnectionTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_rollback() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.rollback();
        Savepoint savepoint = conn.setSavepoint("xx");
        conn.rollback(savepoint);
        conn.close();
    }

    public void test_rollback_1() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.close();
        conn.rollback();
        conn.rollback(null);
    }

    public void test_getOwnerThread() throws Exception {
        DruidPooledConnection conn = ((DruidPooledConnection) (dataSource.getConnection()));
        Assert.assertEquals(Thread.currentThread(), conn.getOwnerThread());
        conn.close();
    }

    public void test_isDiable() throws Exception {
        DruidPooledConnection conn = ((DruidPooledConnection) (dataSource.getConnection()));
        Assert.assertEquals(false, conn.isDisable());
        conn.close();
        Assert.assertEquals(true, conn.isDisable());
    }

    public void test_dupClose() throws Exception {
        DruidPooledConnection conn = ((DruidPooledConnection) (dataSource.getConnection()));
        conn.close();
        conn.close();
    }

    public void test_disable() throws Exception {
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
        DruidPooledConnection conn = ((DruidPooledConnection) (dataSource.getConnection()));
        conn.disable();
        Assert.assertEquals(true, conn.isDisable());
        Assert.assertEquals(1, dataSource.getActiveCount());
        conn.close();
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
    }
}


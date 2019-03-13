package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidDataSourceTest6 extends TestCase {
    private DruidDataSource dataSource;

    private final AtomicInteger errorCount = new AtomicInteger();

    private final AtomicInteger returnEmptyCount = new AtomicInteger();

    public void testValidate() throws Exception {
        returnEmptyCount.set(1);
        Exception error = null;
        try {
            dataSource.init();
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        {
            returnEmptyCount.set(1);
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        {
            returnEmptyCount.set(1);
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        {
            errorCount.set(1);
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        Assert.assertEquals(100, stmt.getQueryTimeout());
        stmt.close();
        conn.close();
    }
}


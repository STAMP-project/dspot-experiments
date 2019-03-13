package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidPooledPreparedStatementTest1 extends TestCase {
    private DruidDataSource dataSource;

    public void test_execute_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            stmt.execute();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
        stmt.close();
        conn.close();
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_executeQuery_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            stmt.executeQuery();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
        stmt.close();
        conn.close();
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_executeUpdate_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            stmt.executeUpdate();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
        stmt.close();
        conn.close();
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_clearParameter_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            stmt.clearParameters();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
        stmt.close();
        conn.close();
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_executeBatch_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            stmt.executeBatch();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
        stmt.close();
        conn.close();
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_getParameterMetaData_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            stmt.getParameterMetaData();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
        stmt.close();
        conn.close();
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
}


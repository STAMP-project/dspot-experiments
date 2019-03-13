package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidPooledConnectionTest_prepareError extends TestCase {
    private DruidDataSource dataSource;

    public void test_prepare_error() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            conn.prepareStatement("select 1");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
    }

    public void test_prepare_error_1() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            conn.prepareStatement("select 1", 0, 0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
    }

    public void test_prepare_error_2() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            conn.prepareStatement("select 1", 0, 0, 0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
    }

    public void test_prepare_error_3() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            conn.prepareStatement("select 1", 0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
    }

    public void test_prepare_error_4() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            conn.prepareStatement("select 1", new int[0]);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
    }

    public void test_prepare_error_5() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            conn.prepareStatement("select 1", new String[0]);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
    }

    public void test_prepareCall_error_1() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            conn.prepareCall("select 1");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
    }

    public void test_prepareCall_error_2() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            conn.prepareCall("select 1", 0, 0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
    }

    public void test_prepareCall_error_3() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(0, dataSource.getErrorCount());
        Exception error = null;
        try {
            conn.prepareCall("select 1", 0, 0, 0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getErrorCount());
    }
}


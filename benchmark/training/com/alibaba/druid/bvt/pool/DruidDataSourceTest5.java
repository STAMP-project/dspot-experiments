package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidDataSourceTest5 extends TestCase {
    private DruidDataSource dataSource;

    private final AtomicBoolean validate = new AtomicBoolean(true);

    public void testValidate() throws Exception {
        validate.set(false);
        Exception error = null;
        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        validate.set(true);
        Connection conn = dataSource.getConnection();
        conn.close();
    }

    public void testValidate_1() throws Exception {
        validate.set(false);
        Exception error = null;
        try {
            dataSource.init();
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        validate.set(true);
        Connection conn = dataSource.getConnection();
        conn.close();
    }

    public void testValidate_3() throws Exception {
        validate.set(false);
        Exception error = null;
        try {
            dataSource.init();
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        validate.set(true);
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}


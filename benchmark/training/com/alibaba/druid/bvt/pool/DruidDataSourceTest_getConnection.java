package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getConnection extends TestCase {
    private DruidDataSource dataSource;

    public void test_conn_ok() throws Exception {
        Connection conn = dataSource.getConnection(null, null);
        conn.close();
    }

    public void test_conn_user_error() throws Exception {
        Exception error = null;
        try {
            dataSource.getConnection("a", null);
        } catch (UnsupportedOperationException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_conn_password_error() throws Exception {
        Exception error = null;
        try {
            dataSource.getConnection(null, "a");
        } catch (UnsupportedOperationException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}


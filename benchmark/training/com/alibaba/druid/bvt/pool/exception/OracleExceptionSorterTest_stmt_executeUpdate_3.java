package com.alibaba.druid.bvt.pool.exception;


import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import junit.framework.TestCase;
import org.junit.Assert;


public class OracleExceptionSorterTest_stmt_executeUpdate_3 extends TestCase {
    private DruidDataSource dataSource;

    public void test_connect() throws Exception {
        String sql = "SELECT 1";
        {
            DruidPooledConnection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            pstmt.close();
            conn.close();
        }
        DruidPooledConnection conn = dataSource.getConnection();
        MockConnection mockConn = conn.unwrap(MockConnection.class);
        Assert.assertNotNull(mockConn);
        Statement stmt = conn.createStatement();
        SQLException exception = new SQLException("xx", "xxx", 28);
        mockConn.setError(exception);
        SQLException execErrror = null;
        try {
            stmt.executeUpdate(sql, new String[0]);
        } catch (SQLException ex) {
            execErrror = ex;
        }
        Assert.assertNotNull(execErrror);
        Assert.assertSame(exception, execErrror);
        SQLException commitError = null;
        try {
            conn.commit();
        } catch (SQLException ex) {
            commitError = ex;
        }
        Assert.assertNotNull(commitError);
        Assert.assertSame(exception, commitError.getCause());
        conn.close();
    }
}


package com.alibaba.druid.bvt.pool.exception;


import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import junit.framework.TestCase;
import org.junit.Assert;


public class OracleExceptionSorterTest_stmt_getFetchSize extends TestCase {
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
        SQLException stmtErrror = null;
        try {
            stmt.getFetchSize();
        } catch (SQLException ex) {
            stmtErrror = ex;
        }
        Assert.assertNotNull(stmtErrror);
        Assert.assertSame(exception, stmtErrror);
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


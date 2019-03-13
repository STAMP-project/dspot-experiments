package com.alibaba.druid.bvt.pool.exception;


import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.junit.Assert;


public class OracleExceptionSorterTest_closeStmt extends TestCase {
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
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setFetchSize(1000);
        SQLException exception = new SQLException("xx", "xxx", 28);
        mockConn.setError(exception);
        SQLException closedErrror = null;
        try {
            pstmt.close();
        } catch (SQLException ex) {
            closedErrror = ex;
        }
        Assert.assertNotNull(closedErrror);
        Assert.assertSame(exception, closedErrror);
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


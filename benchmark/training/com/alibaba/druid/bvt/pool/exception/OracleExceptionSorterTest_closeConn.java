package com.alibaba.druid.bvt.pool.exception;


import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.junit.Assert;


public class OracleExceptionSorterTest_closeConn extends TestCase {
    private DruidDataSource dataSource;

    public void test_connect() throws Exception {
        String sql = "SELECT 1";
        {
            DruidPooledConnection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            pstmt.close();
            conn.close();
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(1, dataSource.getPoolingCount());
            Assert.assertEquals(1, dataSource.getCreateCount());
        }
        DruidPooledConnection conn = dataSource.getConnection();
        MockConnection mockConn = conn.unwrap(MockConnection.class);
        Assert.assertNotNull(mockConn);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setFetchSize(1000);
        SQLException exception = new SQLException("xx", "xxx", 28);
        mockConn.setError(exception);
        Assert.assertEquals(1, dataSource.getActiveCount());
        conn.close();
        Assert.assertEquals(0, dataSource.getActiveCount());
        {
            Connection conn2 = dataSource.getConnection();
            conn2.close();
        }
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(2, dataSource.getCreateCount());
    }
}


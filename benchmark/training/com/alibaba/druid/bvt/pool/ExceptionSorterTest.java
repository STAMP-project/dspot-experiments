package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.MockExceptionSorter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;
import org.junit.Assert;


public class ExceptionSorterTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_exceptionSorter() throws Exception {
        Assert.assertTrue(dataSource.getExceptionSorterClassName(), ((dataSource.getExceptionSorter()) instanceof MockExceptionSorter));
        Connection conn = dataSource.getConnection();
        MockConnection mockConn = conn.unwrap(MockConnection.class);
        PreparedStatement stmt = conn.prepareStatement("select 1");
        stmt.execute();
        mockConn.close();
        Exception stmtClosedError = null;
        try {
            stmt.close();
        } catch (Exception ex) {
            stmtClosedError = ex;
        }
        Assert.assertNotNull(stmtClosedError);
        conn.close();
    }
}


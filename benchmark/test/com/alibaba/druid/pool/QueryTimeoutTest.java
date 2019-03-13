package com.alibaba.druid.pool;


import com.alibaba.druid.util.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import junit.framework.TestCase;


public class QueryTimeoutTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_queryTimeout() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            String sql = "SELECT * FROM ws_product WHERE HAVE_IMAGE = 'N' AND ROWNUM < 1000";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setQueryTimeout(1);
            ResultSet rs = stmt.executeQuery();
            JdbcUtils.printResultSet(rs);
            rs.close();
            stmt.close();
            conn.close();
        }
        Connection conn = dataSource.getConnection();
        String sql = "SELECT 'x' FROM DUAL";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setQueryTimeout(1);
        ResultSet rs = stmt.executeQuery();
        JdbcUtils.printResultSet(rs);
        rs.close();
        stmt.close();
        conn.close();
    }
}


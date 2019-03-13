package com.alibaba.druid.pool.oracle;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;


public class QueryTimeoutTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_queryTimeout() throws Exception {
        try {
            final Connection conn = dataSource.getConnection();
            String sql = "SELECT sleep(1)";
            final CountDownLatch latch = new CountDownLatch(1);
            Thread thread = new Thread() {
                public void run() {
                    try {
                        latch.countDown();
                        Thread.sleep(100);
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            latch.await();
            final PreparedStatement stmt = conn.prepareStatement(sql);
            // stmt.setQueryTimeout(1);
            final ResultSet rs = stmt.executeQuery();
            JdbcUtils.printResultSet(rs);
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            // 
            e.printStackTrace();
        }
        Connection conn = dataSource.getConnection();
        String sql = "SELECT 'x'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setQueryTimeout(1);
        ResultSet rs = stmt.executeQuery();
        JdbcUtils.printResultSet(rs);
        rs.close();
        stmt.close();
        conn.close();
    }
}


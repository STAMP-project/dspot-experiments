package com.alibaba.druid.pool.postgres;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.PGValidConnectionChecker;
import com.alibaba.druid.util.JdbcUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class PG_500_connection_Test extends TestCase {
    private DruidDataSource dataSource;

    public void test_conect_500() throws Exception {
        dataSource.init();
        Assert.assertFalse(dataSource.isOracle());
        Assert.assertTrue(((dataSource.getValidConnectionChecker()) instanceof PGValidConnectionChecker));
        int taskCount = 1000 * 100;
        final CountDownLatch endLatch = new CountDownLatch(taskCount);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    conn = dataSource.getConnection();
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("SELECT 1");
                    while (rs.next()) {
                    } 
                } catch (SQLException ex) {
                    // skip
                } finally {
                    endLatch.countDown();
                }
                JdbcUtils.close(rs);
                JdbcUtils.close(stmt);
                JdbcUtils.close(conn);
            }
        };
        ExecutorService executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < taskCount; ++i) {
            executor.submit(task);
        }
        endLatch.await();
    }
}


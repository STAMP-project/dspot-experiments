package com.alibaba.druid.pvt.pool;


import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;
import org.junit.Assert;


public class DiscardTest extends TestCase {
    private DruidDataSource dataSource;

    private MockDriver driver;

    private volatile boolean failed = false;

    public void test_db_fail() throws Exception {
        exec();
        final int THREAD_COUNT = 10;
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        Thread[] threads = new Thread[THREAD_COUNT];
        {
            Exception error = null;
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            for (int i = 0; i < THREAD_COUNT; ++i) {
                threads[i] = new Thread() {
                    public void run() {
                        try {
                            exec();
                        } finally {
                            endLatch.countDown();
                        }
                    }
                };
                threads[i].start();
            }
            this.failed = true;
            try {
                ResultSet rs = stmt.executeQuery("select 1");
                rs.close();
            } catch (SQLException e) {
                error = e;
            }
            stmt.close();
            conn.close();
            Assert.assertNotNull(error);
        }
        this.failed = false;
        endLatch.await();
    }
}


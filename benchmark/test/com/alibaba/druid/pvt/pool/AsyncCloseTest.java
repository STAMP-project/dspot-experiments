package com.alibaba.druid.pvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;


public class AsyncCloseTest extends TestCase {
    protected DruidDataSource dataSource;

    private ExecutorService connExecutor;

    private ExecutorService closeExecutor;

    final AtomicInteger errorCount = new AtomicInteger();

    public void test_0() throws Exception {
        for (int i = 0; i < 16; ++i) {
            loop();
            System.out.println((("loop " + i) + " done."));
        }
    }

    class CloseTask implements Runnable {
        private Connection conn;

        private CountDownLatch latch;

        public CloseTask(Connection conn, CountDownLatch latch) {
            this.conn = conn;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                conn.close();
            } catch (SQLException e) {
                errorCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        }
    }
}


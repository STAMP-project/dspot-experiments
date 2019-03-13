package com.alibaba.druid.pvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import javax.sql.DataSource;
import junit.framework.TestCase;
import org.junit.Assert;


public class Large10ConcurrentTest extends TestCase {
    private DruidDataSource[] dataSources;

    private ScheduledExecutorService scheduler;

    private ExecutorService executor;

    public void test_large() throws Exception {
        final Connection[] connections = new Connection[(dataSources.length) * 8];
        final CountDownLatch connLatch = new CountDownLatch(connections.length);
        final AtomicLong connErrorCount = new AtomicLong();
        for (int i = 0; i < (dataSources.length); ++i) {
            for (int j = 0; j < 8; ++j) {
                final DataSource dataSource = dataSources[i];
                final int index = (i * 8) + j;
                Runnable task = new Runnable() {
                    public void run() {
                        try {
                            connections[index] = dataSource.getConnection();
                        } catch (SQLException e) {
                            connErrorCount.incrementAndGet();
                            e.printStackTrace();
                        } finally {
                            connLatch.countDown();
                        }
                    }
                };
                executor.execute(task);
            }
        }
        connLatch.await();
        for (int i = 0; i < (dataSources.length); ++i) {
            Assert.assertEquals(8, dataSources[i].getActiveCount());
        }
        for (int i = 0; i < (dataSources.length); ++i) {
            Assert.assertEquals(0, dataSources[i].getPoolingCount());
        }
        final CountDownLatch closeLatch = new CountDownLatch(connections.length);
        for (int i = 0; i < (dataSources.length); ++i) {
            for (int j = 0; j < 8; ++j) {
                final int index = (i * 8) + j;
                Runnable task = new Runnable() {
                    public void run() {
                        JdbcUtils.close(connections[index]);
                        closeLatch.countDown();
                    }
                };
                executor.execute(task);
            }
        }
        closeLatch.await();
    }
}


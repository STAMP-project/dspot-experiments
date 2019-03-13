package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????maxActive < 0
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_notEmptyWait extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
            Assert.assertEquals(1, dataSource.getNotEmptyWaitCount());
        }
        {
            Connection conn = dataSource.getConnection();
            conn.close();
            Assert.assertEquals(1, dataSource.getNotEmptyWaitCount());// notEmptyWaitCount????

        }
        Connection conn = dataSource.getConnection();
        final int THREAD_COUNT = 10;
        final CountDownLatch startLatch = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i) {
            threads[i] = new Thread(("thread-" + i)) {
                public void run() {
                    startLatch.countDown();
                    try {
                        Connection conn = dataSource.getConnection();
                        Thread.sleep(1);
                        conn.close();
                    } catch (Exception e) {
                        // e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            threads[i].start();
        }
        startLatch.await(10, TimeUnit.MILLISECONDS);
        for (int i = 0; i < 100; ++i) {
            if ((dataSource.getNotEmptyWaitThreadCount()) == 10) {
                break;
            }
            Thread.sleep(10);
        }
        Assert.assertEquals(10, dataSource.getNotEmptyWaitThreadCount());
        Assert.assertEquals(10, dataSource.getNotEmptyWaitThreadPeak());
        conn.close();
        endLatch.await(100, TimeUnit.MILLISECONDS);
        Thread.sleep(10);
        // Assert.assertEquals(0, dataSource.getNotEmptyWaitThreadCount());
        Assert.assertEquals(10, dataSource.getNotEmptyWaitThreadPeak());
    }
}


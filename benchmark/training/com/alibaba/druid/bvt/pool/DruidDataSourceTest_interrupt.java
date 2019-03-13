package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidDataSourceTest_interrupt extends TestCase {
    private DruidDataSource dataSource;

    public void test_autoCommit() throws Exception {
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);
        final AtomicInteger errorCount = new AtomicInteger();
        Thread thread = new Thread() {
            public void run() {
                try {
                    startLatch.countDown();
                    dataSource.init();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        startLatch.await();
        Thread.sleep(10);
        Assert.assertEquals(0, errorCount.get());
        thread.interrupt();
        endLatch.await();
        Assert.assertEquals(1, errorCount.get());
    }
}


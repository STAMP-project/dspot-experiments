package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ???????????
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest3 extends TestCase {
    private DruidDataSource dataSource;

    private volatile Exception error;

    private volatile Exception errorB;

    public void test_error() throws Exception {
        final CountDownLatch startedLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);
        Thread threadA = new Thread("A") {
            public void run() {
                try {
                    startedLatch.countDown();
                    dataSource.init();
                } catch (SQLException e) {
                    error = e;
                } finally {
                    endLatch.countDown();
                }
            }
        };
        threadA.start();
        startedLatch.await();
        Thread.sleep(10);
        Assert.assertFalse(dataSource.isInited());
        final CountDownLatch startedLatchB = new CountDownLatch(1);
        final CountDownLatch endLatchB = new CountDownLatch(1);
        Thread threadB = new Thread("B") {
            public void run() {
                try {
                    startedLatchB.countDown();
                    dataSource.init();
                } catch (SQLException e) {
                    errorB = e;
                } finally {
                    endLatchB.countDown();
                }
            }
        };
        threadB.start();
        startedLatchB.await();
        threadB.interrupt();
        endLatchB.await();
        Assert.assertNotNull(errorB);
        Assert.assertTrue(((errorB.getCause()) instanceof InterruptedException));
        threadA.interrupt();
        endLatch.await();
        endLatchB.await();
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getCreateErrorCount());
    }
}


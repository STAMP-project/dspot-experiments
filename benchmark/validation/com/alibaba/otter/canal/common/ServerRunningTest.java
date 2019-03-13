package com.alibaba.otter.canal.common;


import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import com.alibaba.otter.canal.common.zookeeper.running.ServerRunningMonitor;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Test;


public class ServerRunningTest extends AbstractZkTest {
    private ZkClientx zkclientx = new ZkClientx((((cluster1) + ";") + (cluster2)));

    @Test
    public void testOneServer() {
        final CountDownLatch countLatch = new CountDownLatch(2);
        ServerRunningMonitor runningMonitor = buildServerRunning(countLatch, 1L, "127.0.0.1", 2088);
        runningMonitor.start();
        sleep(2000L);
        runningMonitor.stop();
        sleep(2000L);
        if ((countLatch.getCount()) != 0) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiServer() {
        final CountDownLatch countLatch = new CountDownLatch(30);
        final ServerRunningMonitor runningMonitor1 = buildServerRunning(countLatch, 1L, "127.0.0.1", 2088);
        final ServerRunningMonitor runningMonitor2 = buildServerRunning(countLatch, 2L, "127.0.0.1", 2089);
        final ServerRunningMonitor runningMonitor3 = buildServerRunning(countLatch, 3L, "127.0.0.1", 2090);
        final ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    if (!(runningMonitor1.isStart())) {
                        runningMonitor1.start();
                    }
                    sleep((2000L + (RandomUtils.nextInt(500))));
                    if (runningMonitor1.check()) {
                        runningMonitor1.stop();
                    }
                    sleep((2000L + (RandomUtils.nextInt(500))));
                }
            }
        });
        executor.submit(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    if (!(runningMonitor2.isStart())) {
                        runningMonitor2.start();
                    }
                    sleep((2000L + (RandomUtils.nextInt(500))));
                    if (runningMonitor2.check()) {
                        runningMonitor2.stop();
                    }
                    sleep((2000L + (RandomUtils.nextInt(500))));
                }
            }
        });
        executor.submit(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    if (!(runningMonitor3.isStart())) {
                        runningMonitor3.start();
                    }
                    sleep((2000L + (RandomUtils.nextInt(500))));
                    if (runningMonitor3.check()) {
                        runningMonitor3.stop();
                    }
                    sleep((2000L + (RandomUtils.nextInt(500))));
                }
            }
        });
        sleep(30000L);
    }
}


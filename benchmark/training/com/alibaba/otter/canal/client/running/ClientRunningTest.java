package com.alibaba.otter.canal.client.running;


import com.alibaba.otter.canal.client.impl.running.ClientRunningMonitor;
import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Test;


public class ClientRunningTest extends AbstractZkTest {
    private ZkClientx zkclientx = new ZkClientx((((cluster1) + ";") + (cluster2)));

    private short clientId = 1001;

    @Test
    public void testOneServer() {
        final CountDownLatch countLatch = new CountDownLatch(2);
        ClientRunningMonitor runningMonitor = buildClientRunning(countLatch, clientId, 2088);
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
        final ClientRunningMonitor runningMonitor1 = buildClientRunning(countLatch, clientId, 2088);
        final ClientRunningMonitor runningMonitor2 = buildClientRunning(countLatch, clientId, 2089);
        final ClientRunningMonitor runningMonitor3 = buildClientRunning(countLatch, clientId, 2090);
        final ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    if (!(runningMonitor1.isStart())) {
                        runningMonitor1.start();
                    }
                    sleep((2000L + (RandomUtils.nextInt(500))));
                    runningMonitor1.stop();
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
                    runningMonitor2.stop();
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
                    runningMonitor3.stop();
                    sleep((2000L + (RandomUtils.nextInt(500))));
                }
            }
        });
        sleep(30000L);
    }
}


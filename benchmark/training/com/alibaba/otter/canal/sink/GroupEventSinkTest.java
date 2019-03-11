package com.alibaba.otter.canal.sink;


import com.alibaba.otter.canal.sink.entry.group.GroupEventSink;
import com.alibaba.otter.canal.sink.stub.DummyEventStore;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;


public class GroupEventSinkTest {
    private final InetSocketAddress address = new InetSocketAddress("127.0.0.1", 3306);

    @Test
    public void testGroupTwo() {
        final DummyEventStore eventStore = new DummyEventStore();
        final GroupEventSink eventSink = new GroupEventSink(3);
        eventSink.setFilterTransactionEntry(true);
        eventSink.setEventStore(eventStore);
        eventSink.start();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        final CountDownLatch latch = new CountDownLatch(1);
        executor.submit(new Runnable() {
            public void run() {
                for (int i = 0; i < 50; i++) {
                    try {
                        eventSink.sink(Arrays.asList(GroupEventSinkTest.buildEntry("1", (1L + i), (1L + i))), address, "ljhtest1");
                        Thread.sleep((50L + (RandomUtils.nextInt(50))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < 50; i++) {
                    try {
                        eventSink.sink(Arrays.asList(GroupEventSinkTest.buildEntry("1", (1L + i), (30L + i))), address, "ljhtest1");
                        Thread.sleep((50L + (RandomUtils.nextInt(50))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("one sink finished!");
                latch.countDown();
            }
        });
        executor.submit(new Runnable() {
            public void run() {
                for (int i = 0; i < 50; i++) {
                    try {
                        eventSink.sink(Arrays.asList(GroupEventSinkTest.buildEntry("1", (1L + i), (10L + i))), address, "ljhtest2");
                        Thread.sleep((50L + (RandomUtils.nextInt(50))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < 50; i++) {
                    try {
                        eventSink.sink(Arrays.asList(GroupEventSinkTest.buildEntry("1", (1L + i), (40L + i))), address, "ljhtest2");
                        Thread.sleep((50L + (RandomUtils.nextInt(50))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("tow sink finished!");
                latch.countDown();
            }
        });
        executor.submit(new Runnable() {
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        eventSink.sink(Arrays.asList(GroupEventSinkTest.buildEntry("1", (1L + i), (30L + i))), address, "ljhtest3");
                        Thread.sleep((50L + (RandomUtils.nextInt(50))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("tow sink finished!");
                latch.countDown();
            }
        });
        try {
            latch.await();
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }
        eventSink.stop();
        executor.shutdownNow();
    }
}


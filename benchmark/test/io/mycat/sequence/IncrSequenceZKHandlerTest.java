package io.mycat.sequence;


import io.mycat.route.sequence.handler.IncrSequenceZKHandler;
import io.mycat.route.util.PropertiesUtil;
import java.util.Properties;
import java.util.concurrent.ConcurrentSkipListSet;
import junit.framework.Assert;
import org.apache.curator.test.TestingServer;
import org.junit.Test;


/**
 * zookeeper ???????
 * ??????60?????????20??????????50????GLOBAL?nextid
 * ??GLOBAL.MINID=1
 * ??GLOBAL.MAXID=10
 * ???????id????????GLOBAL.MINID-GLOBAL.MAXID9?ID
 *
 * @author Hash Zhang
 * @version 1.0
 * @unknown 23:35 2016/5/6
 */
public class IncrSequenceZKHandlerTest {
    private static final int MAX_CONNECTION = 5;

    private static final int threadCount = 5;

    private static final int LOOP = 5;

    TestingServer testingServer = null;

    IncrSequenceZKHandler[] incrSequenceZKHandler;

    ConcurrentSkipListSet<Long> results;

    @Test
    public void testCorrectnessAndEfficiency() throws InterruptedException {
        final Thread[] threads = new Thread[IncrSequenceZKHandlerTest.MAX_CONNECTION];
        for (int i = 0; i < (IncrSequenceZKHandlerTest.MAX_CONNECTION); i++) {
            final int a = i;
            threads[i] = new Thread() {
                @Override
                public void run() {
                    incrSequenceZKHandler[a] = new IncrSequenceZKHandler();
                    Properties props = PropertiesUtil.loadProps("sequence_conf.properties");
                    try {
                        incrSequenceZKHandler[a].initializeZK(props, testingServer.getConnectString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread[] threads = new Thread[IncrSequenceZKHandlerTest.threadCount];
                    for (int j = 0; j < (IncrSequenceZKHandlerTest.threadCount); j++) {
                        threads[j] = new Thread() {
                            @Override
                            public void run() {
                                for (int k = 0; k < (IncrSequenceZKHandlerTest.LOOP); k++) {
                                    long key = incrSequenceZKHandler[a].nextId("GLOBAL");
                                    results.add(key);
                                }
                            }
                        };
                        threads[j].start();
                    }
                    for (int j = 0; j < (IncrSequenceZKHandlerTest.threadCount); j++) {
                        try {
                            threads[j].join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < (IncrSequenceZKHandlerTest.MAX_CONNECTION); i++) {
            threads[i].start();
        }
        for (int i = 0; i < (IncrSequenceZKHandlerTest.MAX_CONNECTION); i++) {
            threads[i].join();
        }
        long end = System.currentTimeMillis();
        Assert.assertEquals((((IncrSequenceZKHandlerTest.MAX_CONNECTION) * (IncrSequenceZKHandlerTest.LOOP)) * (IncrSequenceZKHandlerTest.threadCount)), results.size());
        // Assert.assertTrue(results.pollLast().equals(MAX_CONNECTION * LOOP * threadCount + 1L));
        // Assert.assertTrue(results.pollFirst().equals(2L));
        System.out.println((((("Time elapsed:" + (((double) ((end - start) + 1)) / 1000.0)) + "s\n TPS:") + ((((double) (((IncrSequenceZKHandlerTest.MAX_CONNECTION) * (IncrSequenceZKHandlerTest.LOOP)) * (IncrSequenceZKHandlerTest.threadCount))) / ((double) ((end - start) + 1))) * 1000.0)) + "/s"));
    }
}


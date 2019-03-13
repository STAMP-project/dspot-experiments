package com.baidu.fsg.uid;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test for {@link CachedUidGenerator}
 *
 * @author yutianbao
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:uid/cached-uid-spring.xml" })
public class CachedUidGeneratorTest {
    private static final int SIZE = 7000000;// 700w


    private static final boolean VERBOSE = false;

    private static final int THREADS = (Runtime.getRuntime().availableProcessors()) << 1;

    @Resource
    private UidGenerator uidGenerator;

    /**
     * Test for serially generate
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSerialGenerate() throws IOException {
        // Generate UID serially
        Set<Long> uidSet = new HashSet<>(CachedUidGeneratorTest.SIZE);
        for (int i = 0; i < (CachedUidGeneratorTest.SIZE); i++) {
            doGenerate(uidSet, i);
        }
        // Check UIDs are all unique
        checkUniqueID(uidSet);
    }

    /**
     * Test for parallel generate
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testParallelGenerate() throws IOException, InterruptedException {
        AtomicInteger control = new AtomicInteger((-1));
        Set<Long> uidSet = new ConcurrentSkipListSet<>();
        // Initialize threads
        List<Thread> threadList = new ArrayList<>(CachedUidGeneratorTest.THREADS);
        for (int i = 0; i < (CachedUidGeneratorTest.THREADS); i++) {
            Thread thread = new Thread(() -> workerRun(uidSet, control));
            thread.setName(("UID-generator-" + i));
            threadList.add(thread);
            thread.start();
        }
        // Wait for worker done
        for (Thread thread : threadList) {
            thread.join();
        }
        // Check generate 700w times
        Assert.assertEquals(CachedUidGeneratorTest.SIZE, control.get());
        // Check UIDs are all unique
        checkUniqueID(uidSet);
    }
}


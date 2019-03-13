/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.concurrent.util;


import co.paralleluniverse.common.test.TestUtil;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


/**
 *
 *
 * @author pron
 */
public class ScheduledSingleThreadExecutorTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    private ScheduledSingleThreadExecutor exec;

    public ScheduledSingleThreadExecutorTest() {
    }

    @Test
    public void testFixedRate() throws Exception {
        final AtomicInteger counter = new AtomicInteger();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }, 0, 30, TimeUnit.MILLISECONDS);
        Thread.sleep(2000);
        final int count = counter.get();
        Assert.assertTrue(("count: " + count), ((count > 30) && (count < 75)));
    }

    @Test
    public void testMultipleProducers() throws Exception {
        final int nThread = 5;
        final int nSchedules = 50000;
        final AtomicInteger counter2 = new AtomicInteger();
        final AtomicInteger counter = new AtomicInteger();
        for (int t = 0; t < nThread; t++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < nSchedules; i++) {
                            counter2.incrementAndGet();
                            exec.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    counter.incrementAndGet();
                                }
                            }, 0, TimeUnit.MILLISECONDS);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(3000);
        Assert.assertThat(counter2.get(), CoreMatchers.is((nThread * nSchedules)));
        Assert.assertThat(counter.get(), CoreMatchers.is((nThread * nSchedules)));
    }
}


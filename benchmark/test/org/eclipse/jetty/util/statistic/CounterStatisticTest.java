/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.statistic;


import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/* ------------------------------------------------------------ */
public class CounterStatisticTest {
    @Test
    public void testCounter() throws Exception {
        CounterStatistic count = new CounterStatistic();
        MatcherAssert.assertThat(count.getCurrent(), Matchers.equalTo(0L));
        MatcherAssert.assertThat(count.getMax(), Matchers.equalTo(0L));
        MatcherAssert.assertThat(count.getTotal(), Matchers.equalTo(0L));
        count.increment();
        count.increment();
        count.decrement();
        count.add(4);
        count.add((-2));
        MatcherAssert.assertThat(count.getCurrent(), Matchers.equalTo(3L));
        MatcherAssert.assertThat(count.getMax(), Matchers.equalTo(5L));
        MatcherAssert.assertThat(count.getTotal(), Matchers.equalTo(6L));
        count.reset();
        MatcherAssert.assertThat(count.getCurrent(), Matchers.equalTo(3L));
        MatcherAssert.assertThat(count.getMax(), Matchers.equalTo(3L));
        MatcherAssert.assertThat(count.getTotal(), Matchers.equalTo(3L));
        count.increment();
        count.decrement();
        count.add((-2));
        count.decrement();
        MatcherAssert.assertThat(count.getCurrent(), Matchers.equalTo(0L));
        MatcherAssert.assertThat(count.getMax(), Matchers.equalTo(4L));
        MatcherAssert.assertThat(count.getTotal(), Matchers.equalTo(4L));
        count.decrement();
        MatcherAssert.assertThat(count.getCurrent(), Matchers.equalTo((-1L)));
        MatcherAssert.assertThat(count.getMax(), Matchers.equalTo(4L));
        MatcherAssert.assertThat(count.getTotal(), Matchers.equalTo(4L));
        count.increment();
        MatcherAssert.assertThat(count.getCurrent(), Matchers.equalTo(0L));
        MatcherAssert.assertThat(count.getMax(), Matchers.equalTo(4L));
        MatcherAssert.assertThat(count.getTotal(), Matchers.equalTo(5L));
    }

    @Test
    public void testCounterContended() throws Exception {
        final CounterStatistic counter = new CounterStatistic();
        final int N = 100;
        final int L = 1000;
        final Thread[] threads = new Thread[N];
        final CyclicBarrier incBarrier = new CyclicBarrier(N);
        final CountDownLatch decBarrier = new CountDownLatch((N / 2));
        for (int i = N; (i--) > 0;) {
            threads[i] = (i >= (N / 2)) ? new Thread() {
                @Override
                public void run() {
                    try {
                        incBarrier.await();
                        decBarrier.await();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Random random = new Random();
                    for (int l = L; (l--) > 0;) {
                        counter.decrement();
                        if ((random.nextInt(5)) == 0)
                            Thread.yield();

                    }
                }
            } : new Thread() {
                @Override
                public void run() {
                    try {
                        incBarrier.await();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Random random = new Random();
                    for (int l = L; (l--) > 0;) {
                        counter.increment();
                        if (l == (L / 2))
                            decBarrier.countDown();

                        if ((random.nextInt(5)) == 0)
                            Thread.yield();

                    }
                }
            };
            threads[i].start();
        }
        for (int i = N; (i--) > 0;)
            threads[i].join();

        MatcherAssert.assertThat(counter.getCurrent(), Matchers.equalTo(0L));
        MatcherAssert.assertThat(counter.getTotal(), Matchers.equalTo(((N * L) / 2L)));
        MatcherAssert.assertThat(counter.getMax(), Matchers.greaterThanOrEqualTo(((N / 2) * (L / 2L))));
    }
}


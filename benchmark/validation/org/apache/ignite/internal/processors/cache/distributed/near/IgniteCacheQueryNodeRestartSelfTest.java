/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache.distributed.near;


import Cache.Entry;
import EventType.EVT_CACHE_REBALANCE_STOPPED;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.events.Event;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.cache.GridCacheAbstractSelfTest;
import org.apache.ignite.internal.util.typedef.CAX;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgnitePredicate;
import org.junit.Test;


/**
 * Test for distributed queries with node restarts.
 */
public class IgniteCacheQueryNodeRestartSelfTest extends GridCacheAbstractSelfTest {
    /**
     *
     */
    private static final int GRID_CNT = 3;

    /**
     *
     */
    private static final int KEY_CNT = 1000;

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testRestarts() throws Exception {
        int duration = 60 * 1000;
        int qryThreadNum = 10;
        final int logFreq = 50;
        final IgniteCache<Integer, Integer> cache = grid(0).cache(DEFAULT_CACHE_NAME);
        assert cache != null;
        for (int i = 0; i < (IgniteCacheQueryNodeRestartSelfTest.KEY_CNT); i++)
            cache.put(i, i);

        assertEquals(IgniteCacheQueryNodeRestartSelfTest.KEY_CNT, cache.size());
        final AtomicInteger qryCnt = new AtomicInteger();
        final AtomicBoolean done = new AtomicBoolean();
        IgniteInternalFuture<?> fut1 = multithreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                while (!(done.get())) {
                    Collection<Entry<Integer, Integer>> res = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "true")).getAll();
                    Set<Integer> keys = new HashSet<>();
                    for (Entry<Integer, Integer> entry : res)
                        keys.add(entry.getKey());

                    if ((IgniteCacheQueryNodeRestartSelfTest.KEY_CNT) > (keys.size())) {
                        for (int i = 0; i < (IgniteCacheQueryNodeRestartSelfTest.KEY_CNT); i++) {
                            if (!(keys.contains(i)))
                                assertEquals(Integer.valueOf(i), cache.get(i));

                        }
                        fail(("res size: " + (res.size())));
                    }
                    assertEquals(IgniteCacheQueryNodeRestartSelfTest.KEY_CNT, keys.size());
                    int c = qryCnt.incrementAndGet();
                    if ((c % logFreq) == 0)
                        info(("Executed queries: " + c));

                } 
            }
        }, qryThreadNum, "query-thread");
        final AtomicInteger restartCnt = new AtomicInteger();
        IgniteCacheQueryNodeRestartSelfTest.CollectingEventListener lsnr = new IgniteCacheQueryNodeRestartSelfTest.CollectingEventListener();
        for (int i = 0; i < (IgniteCacheQueryNodeRestartSelfTest.GRID_CNT); i++)
            grid(i).events().localListen(lsnr, EVT_CACHE_REBALANCE_STOPPED);

        IgniteInternalFuture<?> fut2 = createRestartAction(done, restartCnt);
        Thread.sleep(duration);
        info("Stopping..");
        done.set(true);
        fut2.get();
        info("Restarts stopped.");
        fut1.get();
        info("Queries stopped.");
        info((("Awaiting rebalance events [restartCnt=" + (restartCnt.get())) + ']'));
        boolean success = lsnr.awaitEvents((((IgniteCacheQueryNodeRestartSelfTest.GRID_CNT) * 2) * (restartCnt.get())), 15000);
        for (int i = 0; i < (IgniteCacheQueryNodeRestartSelfTest.GRID_CNT); i++)
            grid(i).events().stopLocalListen(lsnr, EVT_CACHE_REBALANCE_STOPPED);

        assert success;
    }

    /**
     * Listener that will wait for specified number of events received.
     */
    private class CollectingEventListener implements IgnitePredicate<Event> {
        /**
         * Registered events count.
         */
        private int evtCnt;

        /**
         * {@inheritDoc }
         */
        @Override
        public synchronized boolean apply(Event evt) {
            (evtCnt)++;
            info((((("Processed event [evt=" + evt) + ", evtCnt=") + (evtCnt)) + ']'));
            notifyAll();
            return true;
        }

        /**
         * Waits until total number of events processed is equal or greater then argument passed.
         *
         * @param cnt
         * 		Number of events to wait.
         * @param timeout
         * 		Timeout to wait.
         * @return {@code True} if successfully waited, {@code false} if timeout happened.
         * @throws InterruptedException
         * 		If thread is interrupted.
         */
        public synchronized boolean awaitEvents(int cnt, long timeout) throws InterruptedException {
            long start = U.currentTimeMillis();
            long now = start;
            while ((start + timeout) > now) {
                if ((evtCnt) >= cnt)
                    return true;

                wait(((start + timeout) - now));
                now = U.currentTimeMillis();
            } 
            return false;
        }
    }
}


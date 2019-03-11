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
package org.apache.ignite.internal.processors.cache;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cluster.ClusterTopologyException;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Test to validate https://issues.apache.org/jira/browse/IGNITE-2310
 */
public class IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest extends IgniteCacheLockPartitionOnAffinityRunAbstractTest {
    /**
     * Atomic cache.
     */
    private static final String ATOMIC_CACHE = "atomic";

    /**
     * Transact cache.
     */
    private static final String TRANSACT_CACHE = "transact";

    /**
     * Transact cache.
     */
    private static final long TEST_TIMEOUT = 10 * 60000;

    /**
     * Keys count.
     */
    private static int KEYS_CNT = 100;

    /**
     * Keys count.
     */
    private static int PARTS_CNT = 16;

    /**
     * Key.
     */
    private static AtomicInteger key = new AtomicInteger(0);

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNotReservedAtomicCacheOp() throws Exception {
        notReservedCacheOp(IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest.ATOMIC_CACHE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNotReservedTxCacheOp() throws Exception {
        notReservedCacheOp(IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest.TRANSACT_CACHE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReservedPartitionCacheOp() throws Exception {
        // Workaround for initial update job metadata.
        grid(0).cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()).clear();
        grid(0).compute().affinityRun(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName()), 0, new IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest.ReservedPartitionCacheOpAffinityRun(0, 0));
        // Run restart threads: start re-balancing
        beginNodesRestart();
        IgniteInternalFuture<Long> affFut = null;
        try {
            affFut = GridTestUtils.runMultiThreadedAsync(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < (IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest.PARTS_CNT); ++i) {
                        if ((System.currentTimeMillis()) >= (IgniteCacheLockPartitionOnAffinityRunAbstractTest.endTime))
                            break;

                        try {
                            grid(0).compute().affinityRun(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(i), new IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest.ReservedPartitionCacheOpAffinityRun(i, ((IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest.key.getAndIncrement()) * (IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest.KEYS_CNT))));
                        } catch (IgniteException e) {
                            checkException(e, ClusterTopologyException.class);
                        }
                    }
                }
            }, IgniteCacheLockPartitionOnAffinityRunAbstractTest.AFFINITY_THREADS_CNT, "affinity-run");
        } finally {
            if (affFut != null)
                affFut.get();

            stopRestartThread.set(true);
            IgniteCacheLockPartitionOnAffinityRunAbstractTest.nodeRestartFut.get();
            Thread.sleep(5000);
            log.info("Final await. Timed out if failed");
            awaitPartitionMapExchange();
            IgniteCache cache = grid(0).cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName());
            cache.clear();
        }
    }

    /**
     *
     */
    private static class NotReservedCacheOpAffinityRun implements IgniteRunnable {
        /**
         * Org id.
         */
        int orgId;

        /**
         * Begin of key.
         */
        int keyBegin;

        /**
         * Cache name.
         */
        private String cacheName;

        /**
         *
         */
        @IgniteInstanceResource
        private IgniteEx ignite;

        /**
         *
         */
        @LoggerResource
        private IgniteLogger log;

        /**
         *
         */
        public NotReservedCacheOpAffinityRun() {
            // No-op.
        }

        /**
         *
         *
         * @param orgId
         * 		Organization.
         * @param keyBegin
         * 		Begin key value.
         * @param cacheName
         * 		Cache name.
         */
        public NotReservedCacheOpAffinityRun(int orgId, int keyBegin, String cacheName) {
            this.orgId = orgId;
            this.keyBegin = keyBegin;
            this.cacheName = cacheName;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            log.info(("Begin run " + (keyBegin)));
            IgniteCache cache = ignite.cache(cacheName);
            for (int i = 0; i < (IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest.KEYS_CNT); ++i)
                cache.put((i + (keyBegin)), (i + (keyBegin)));

            log.info(("End run " + (keyBegin)));
        }
    }

    /**
     *
     */
    private static class ReservedPartitionCacheOpAffinityRun implements IgniteRunnable {
        /**
         * Org id.
         */
        int orgId;

        /**
         * Begin of key.
         */
        int keyBegin;

        /**
         *
         */
        @IgniteInstanceResource
        private IgniteEx ignite;

        /**
         *
         */
        @LoggerResource
        private IgniteLogger log;

        /**
         *
         */
        public ReservedPartitionCacheOpAffinityRun() {
            // No-op.
        }

        /**
         *
         *
         * @param orgId
         * 		Organization Id.
         * @param keyBegin
         * 		Begin key value;
         */
        public ReservedPartitionCacheOpAffinityRun(int orgId, int keyBegin) {
            this.orgId = orgId;
            this.keyBegin = keyBegin;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            log.info(("Begin run " + (keyBegin)));
            IgniteCache cache = ignite.cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName());
            for (int i = 0; i < (IgniteCacheLockPartitionOnAffinityRunAtomicCacheOpTest.KEYS_CNT); ++i) {
                IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person p = new IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person((i + (keyBegin)), orgId);
                cache.put(p.createKey(), p);
            }
        }
    }
}


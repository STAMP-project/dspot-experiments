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
package org.apache.ignite.internal.processors.cache.persistence.db;


import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.visor.verify.ValidateIndexesClosure;
import org.apache.ignite.internal.visor.verify.VisorValidateIndexesJobResult;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests that continuous non-graceful node stop under load doesn't break SQL indexes.
 */
public class IndexingMultithreadedLoadContinuousRestartTest extends GridCommonAbstractTest {
    /**
     * Cache name.
     */
    private static final String CACHE_NAME = "test-cache-name";

    /**
     * Restarts.
     */
    private static final int RESTARTS = 5;

    /**
     * Load threads.
     */
    private static final int THREADS = 5;

    /**
     * Load loop cycles.
     */
    private static final int LOAD_LOOP = 5000;

    /**
     * Key bound.
     */
    private static final int KEY_BOUND = 1000;

    /**
     * Tests that continuous non-graceful node stop under load doesn't break SQL indexes.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void test() throws Exception {
        for (int i = 0; i < (IndexingMultithreadedLoadContinuousRestartTest.RESTARTS); i++) {
            IgniteEx ignite = startGrid(0);
            ignite.cluster().active(true);
            // Ensure that checkpoint isn't running - otherwise validate indexes task may fail.
            forceCheckpoint();
            // Validate indexes on start.
            ValidateIndexesClosure clo = new ValidateIndexesClosure(Collections.singleton(IndexingMultithreadedLoadContinuousRestartTest.CACHE_NAME), 0, 0);
            ignite.context().resource().injectGeneric(clo);
            VisorValidateIndexesJobResult res = clo.call();
            assertFalse(res.hasIssues());
            IgniteInternalFuture<Long> fut = GridTestUtils.runMultiThreadedAsync(new Runnable() {
                @Override
                public void run() {
                    IgniteCache<IndexingMultithreadedLoadContinuousRestartTest.UserKey, IndexingMultithreadedLoadContinuousRestartTest.UserValue> cache = ignite.cache(IndexingMultithreadedLoadContinuousRestartTest.CACHE_NAME);
                    int i = 0;
                    try {
                        for (; i < (IndexingMultithreadedLoadContinuousRestartTest.LOAD_LOOP); i++) {
                            ThreadLocalRandom r = ThreadLocalRandom.current();
                            Integer keySeed = r.nextInt(IndexingMultithreadedLoadContinuousRestartTest.KEY_BOUND);
                            IndexingMultithreadedLoadContinuousRestartTest.UserKey key = new IndexingMultithreadedLoadContinuousRestartTest.UserKey(keySeed);
                            if (r.nextBoolean())
                                cache.put(key, new IndexingMultithreadedLoadContinuousRestartTest.UserValue(r.nextLong()));
                            else
                                cache.remove(key);

                        }
                        ignite.close();// Intentionally stop grid while another loaders are still in progress.

                    } catch (Exception e) {
                        log.warning((("Failed to update cache after " + i) + " loop cycles"), e);
                    }
                }
            }, IndexingMultithreadedLoadContinuousRestartTest.THREADS, "loader");
            fut.get();
            ignite.close();
        }
    }

    /**
     * User key.
     */
    private static class UserKey {
        /**
         * A.
         */
        private int a;

        /**
         * B.
         */
        private int b;

        /**
         * C.
         */
        private int c;

        /**
         *
         *
         * @param a
         * 		A.
         * @param b
         * 		B.
         * @param c
         * 		C.
         */
        public UserKey(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        /**
         *
         *
         * @param seed
         * 		Seed.
         */
        public UserKey(long seed) {
            a = ((int) (seed % 17));
            b = ((int) (seed % 257));
            c = ((int) (seed % 3001));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (((((("UserKey{" + "a=") + (a)) + ", b=") + (b)) + ", c=") + (c)) + '}';
        }
    }

    /**
     * User value.
     */
    private static class UserValue {
        /**
         * X.
         */
        private int x;

        /**
         * Y.
         */
        private int y;

        /**
         * Z.
         */
        private int z;

        /**
         *
         *
         * @param x
         * 		X.
         * @param y
         * 		Y.
         * @param z
         * 		Z.
         */
        public UserValue(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         *
         *
         * @param seed
         * 		Seed.
         */
        public UserValue(long seed) {
            x = ((int) (seed % 6991));
            y = ((int) (seed % 18679));
            z = ((int) (seed % 31721));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (((((("UserValue{" + "x=") + (x)) + ", y=") + (y)) + ", z=") + (z)) + '}';
        }
    }
}


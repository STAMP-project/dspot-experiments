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


import Cache.Entry;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.processors.cache.query.GridCacheQueryManager;
import org.apache.ignite.internal.util.typedef.CAX;
import org.junit.Test;


/**
 * Multi-threaded tests for cache queries.
 */
public class IgniteCacheQueryMultiThreadedSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final boolean TEST_INFO = true;

    /**
     * Number of test grids (nodes). Should not be less than 2.
     */
    private static final int GRID_CNT = 3;

    /**
     *
     */
    private static AtomicInteger idxSwapCnt = new AtomicInteger();

    /**
     *
     */
    private static AtomicInteger idxUnswapCnt = new AtomicInteger();

    /**
     *
     */
    private static final long DURATION = 30 * 1000;

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testMultiThreadedSwapUnswapString() throws Exception {
        int threadCnt = 50;
        final int keyCnt = 2000;
        final int valCnt = 10000;
        final Ignite g = grid(0);
        // Put test values into cache.
        final IgniteCache<Integer, String> c = cache(Integer.class, String.class);
        assertEquals(0, g.cache(DEFAULT_CACHE_NAME).localSize());
        assertEquals(0, c.query(new SqlQuery(String.class, "1 = 1")).getAll().size());
        Random rnd = new Random();
        for (int i = 0; i < keyCnt; i += 1 + (rnd.nextInt(3))) {
            c.put(i, String.valueOf(rnd.nextInt(valCnt)));
            if ((evictsEnabled()) && (rnd.nextBoolean()))
                c.localEvict(Arrays.asList(i));

        }
        final AtomicBoolean done = new AtomicBoolean();
        IgniteInternalFuture<?> fut = multithreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                Random rnd = new Random();
                while (!(done.get())) {
                    switch (rnd.nextInt(5)) {
                        case 0 :
                            c.put(rnd.nextInt(keyCnt), String.valueOf(rnd.nextInt(valCnt)));
                            break;
                        case 1 :
                            if (evictsEnabled())
                                c.localEvict(Arrays.asList(rnd.nextInt(keyCnt)));

                            break;
                        case 2 :
                            c.remove(rnd.nextInt(keyCnt));
                            break;
                        case 3 :
                            c.get(rnd.nextInt(keyCnt));
                            break;
                        case 4 :
                            int from = rnd.nextInt(valCnt);
                            c.query(new SqlQuery(String.class, "_val between ? and ?").setArgs(String.valueOf(from), String.valueOf((from + 250)))).getAll();
                    }
                } 
            }
        }, threadCnt);
        Thread.sleep(IgniteCacheQueryMultiThreadedSelfTest.DURATION);
        done.set(true);
        fut.get();
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testMultiThreadedSwapUnswapLong() throws Exception {
        int threadCnt = 50;
        final int keyCnt = 2000;
        final int valCnt = 10000;
        final Ignite g = grid(0);
        // Put test values into cache.
        final IgniteCache<Integer, Long> c = cache(Integer.class, Long.class);
        assertEquals(0, g.cache(DEFAULT_CACHE_NAME).localSize());
        assertEquals(0, c.query(new SqlQuery(Long.class, "1 = 1")).getAll().size());
        Random rnd = new Random();
        for (int i = 0; i < keyCnt; i += 1 + (rnd.nextInt(3))) {
            c.put(i, ((long) (rnd.nextInt(valCnt))));
            if ((evictsEnabled()) && (rnd.nextBoolean()))
                c.localEvict(Arrays.asList(i));

        }
        final AtomicBoolean done = new AtomicBoolean();
        IgniteInternalFuture<?> fut = multithreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                Random rnd = new Random();
                while (!(done.get())) {
                    int key = rnd.nextInt(keyCnt);
                    switch (rnd.nextInt(5)) {
                        case 0 :
                            c.put(key, ((long) (rnd.nextInt(valCnt))));
                            break;
                        case 1 :
                            if (evictsEnabled())
                                c.localEvict(Arrays.asList(key));

                            break;
                        case 2 :
                            c.remove(key);
                            break;
                        case 3 :
                            c.get(key);
                            break;
                        case 4 :
                            int from = rnd.nextInt(valCnt);
                            c.query(new SqlQuery(Long.class, "_val between ? and ?").setArgs(from, (from + 250))).getAll();
                    }
                } 
            }
        }, threadCnt);
        Thread.sleep(IgniteCacheQueryMultiThreadedSelfTest.DURATION);
        done.set(true);
        fut.get();
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testMultiThreadedSwapUnswapLongString() throws Exception {
        int threadCnt = 50;
        final int keyCnt = 2000;
        final int valCnt = 10000;
        final Ignite g = grid(0);
        // Put test values into cache.
        final IgniteCache<Integer, Object> c = cache(Integer.class, Object.class);
        assertEquals(0, g.cache(DEFAULT_CACHE_NAME).size());
        assertEquals(0, c.query(new SqlQuery(Object.class, "1 = 1")).getAll().size());
        Random rnd = new Random();
        for (int i = 0; i < keyCnt; i += 1 + (rnd.nextInt(3))) {
            c.put(i, (rnd.nextBoolean() ? ((long) (rnd.nextInt(valCnt))) : String.valueOf(rnd.nextInt(valCnt))));
            if ((evictsEnabled()) && (rnd.nextBoolean()))
                c.localEvict(Arrays.asList(i));

        }
        final AtomicBoolean done = new AtomicBoolean();
        IgniteInternalFuture<?> fut = multithreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                Random rnd = new Random();
                while (!(done.get())) {
                    int key = rnd.nextInt(keyCnt);
                    switch (rnd.nextInt(5)) {
                        case 0 :
                            c.put(key, (rnd.nextBoolean() ? ((long) (rnd.nextInt(valCnt))) : String.valueOf(rnd.nextInt(valCnt))));
                            break;
                        case 1 :
                            if (evictsEnabled())
                                c.localEvict(Arrays.asList(key));

                            break;
                        case 2 :
                            c.remove(key);
                            break;
                        case 3 :
                            c.get(key);
                            break;
                        case 4 :
                            int from = rnd.nextInt(valCnt);
                            c.query(new SqlQuery(Object.class, "_val between ? and ?").setArgs(from, (from + 250))).getAll();
                    }
                } 
            }
        }, threadCnt);
        Thread.sleep(IgniteCacheQueryMultiThreadedSelfTest.DURATION);
        done.set(true);
        fut.get();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testMultiThreadedSwapUnswapObject() throws Exception {
        int threadCnt = 50;
        final int keyCnt = 4000;
        final int valCnt = 10000;
        final Ignite g = grid(0);
        // Put test values into cache.
        final IgniteCache<Integer, IgniteCacheQueryMultiThreadedSelfTest.TestValue> c = cache(Integer.class, IgniteCacheQueryMultiThreadedSelfTest.TestValue.class);
        assertEquals(0, g.cache(DEFAULT_CACHE_NAME).localSize());
        assertEquals(0, c.query(new SqlQuery(IgniteCacheQueryMultiThreadedSelfTest.TestValue.class, "1 = 1")).getAll().size());
        Random rnd = new Random();
        for (int i = 0; i < keyCnt; i += 1 + (rnd.nextInt(3))) {
            c.put(i, new IgniteCacheQueryMultiThreadedSelfTest.TestValue(rnd.nextInt(valCnt)));
            if ((evictsEnabled()) && (rnd.nextBoolean()))
                c.localEvict(Arrays.asList(i));

        }
        final AtomicBoolean done = new AtomicBoolean();
        IgniteInternalFuture<?> fut = multithreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                Random rnd = new Random();
                while (!(done.get())) {
                    int key = rnd.nextInt(keyCnt);
                    switch (rnd.nextInt(5)) {
                        case 0 :
                            c.put(key, new IgniteCacheQueryMultiThreadedSelfTest.TestValue(rnd.nextInt(valCnt)));
                            break;
                        case 1 :
                            if (evictsEnabled())
                                c.localEvict(Arrays.asList(key));

                            break;
                        case 2 :
                            c.remove(key);
                            break;
                        case 3 :
                            c.get(key);
                            break;
                        case 4 :
                            int from = rnd.nextInt(valCnt);
                            c.query(new SqlQuery(IgniteCacheQueryMultiThreadedSelfTest.TestValue.class, "TestValue.val between ? and ?").setArgs(from, (from + 250))).getAll();
                    }
                } 
            }
        }, threadCnt);
        Thread.sleep(IgniteCacheQueryMultiThreadedSelfTest.DURATION);
        done.set(true);
        fut.get();
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testMultiThreadedSameQuery() throws Exception {
        int threadCnt = 50;
        final int keyCnt = 10;
        final int logMod = 5000;
        final Ignite g = grid(0);
        // Put test values into cache.
        final IgniteCache<Integer, Integer> c = cache(Integer.class, Integer.class);
        for (int i = 0; i < keyCnt; i++) {
            c.put(i, i);
            c.localEvict(Arrays.asList(i));
        }
        final AtomicInteger cnt = new AtomicInteger();
        final AtomicBoolean done = new AtomicBoolean();
        IgniteInternalFuture<?> fut = multithreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                int iter = 0;
                while ((!(done.get())) && (!(Thread.currentThread().isInterrupted()))) {
                    iter++;
                    Collection<Entry<Integer, Integer>> entries = c.query(new SqlQuery(Integer.class, "_val >= 0")).getAll();
                    assert entries != null;
                    assertEquals((((((("Query results [entries=" + entries) + ", aff=") + (affinityNodes(entries, g))) + ", iteration=") + iter) + ']'), keyCnt, entries.size());
                    if (((cnt.incrementAndGet()) % logMod) == 0) {
                        GridCacheQueryManager<Object, Object> qryMgr = ((IgniteKernal) (g)).internalCache(c.getName()).context().queries();
                        assert qryMgr != null;
                        qryMgr.printMemoryStats();
                    }
                } 
            }
        }, threadCnt);
        Thread.sleep(IgniteCacheQueryMultiThreadedSelfTest.DURATION);
        info("Finishing test...");
        done.set(true);
        fut.get();
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testMultiThreadedNewQueries() throws Exception {
        int threadCnt = 50;
        final int keyCnt = 10;
        final int logMod = 5000;
        final Ignite g = grid(0);
        // Put test values into cache.
        final IgniteCache<Integer, Integer> c = cache(Integer.class, Integer.class);
        for (int i = 0; i < keyCnt; i++) {
            c.put(i, i);
            c.localEvict(Arrays.asList(i));
        }
        final AtomicInteger cnt = new AtomicInteger();
        final AtomicBoolean done = new AtomicBoolean();
        IgniteInternalFuture<?> fut = multithreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                int iter = 0;
                while ((!(done.get())) && (!(Thread.currentThread().isInterrupted()))) {
                    iter++;
                    Collection<Entry<Integer, Integer>> entries = c.query(new SqlQuery(Integer.class, "_val >= 0")).getAll();
                    assert entries != null;
                    assertEquals(("Entries count is not as expected on iteration: " + iter), keyCnt, entries.size());
                    if (((cnt.incrementAndGet()) % logMod) == 0) {
                        GridCacheQueryManager<Object, Object> qryMgr = ((IgniteKernal) (g)).internalCache(c.getName()).context().queries();
                        assert qryMgr != null;
                        qryMgr.printMemoryStats();
                    }
                } 
            }
        }, threadCnt);
        Thread.sleep(IgniteCacheQueryMultiThreadedSelfTest.DURATION);
        done.set(true);
        fut.get();
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testMultiThreadedScanQuery() throws Exception {
        int threadCnt = 50;
        final int keyCnt = 500;
        final int logMod = 5000;
        final Ignite g = grid(0);
        // Put test values into cache.
        final IgniteCache<Integer, Integer> c = cache(Integer.class, Integer.class);
        for (int i = 0; i < keyCnt; i++)
            c.put(i, i);

        final AtomicInteger cnt = new AtomicInteger();
        final AtomicBoolean done = new AtomicBoolean();
        IgniteInternalFuture<?> fut = multithreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                int iter = 0;
                while ((!(done.get())) && (!(Thread.currentThread().isInterrupted()))) {
                    iter++;
                    // Scan query.
                    Collection<Entry<Integer, Integer>> entries = c.query(new org.apache.ignite.cache.query.ScanQuery<Integer, Integer>()).getAll();
                    assert entries != null;
                    assertEquals(("Entries count is not as expected on iteration: " + iter), keyCnt, entries.size());
                    if (((cnt.incrementAndGet()) % logMod) == 0) {
                        GridCacheQueryManager<Object, Object> qryMgr = ((IgniteKernal) (g)).internalCache(c.getName()).context().queries();
                        assert qryMgr != null;
                        qryMgr.printMemoryStats();
                    }
                } 
            }
        }, threadCnt);
        Thread.sleep(IgniteCacheQueryMultiThreadedSelfTest.DURATION);
        done.set(true);
        fut.get();
    }

    /**
     * SqlFieldsQuery paging mechanics stress test
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testMultiThreadedSqlFieldsQuery() throws Throwable {
        int threadCnt = 16;
        final int keyCnt = 1100;// set resultSet size bigger than page size

        final int logMod = 5000;
        final Ignite g = grid(0);
        // Put test values into cache.
        final IgniteCache<Integer, IgniteCacheQueryMultiThreadedSelfTest.TestValue> c = cache(Integer.class, IgniteCacheQueryMultiThreadedSelfTest.TestValue.class);
        for (int i = 0; i < keyCnt; i++)
            c.put(i, new IgniteCacheQueryMultiThreadedSelfTest.TestValue(i));

        final AtomicInteger cnt = new AtomicInteger();
        final AtomicBoolean done = new AtomicBoolean();
        IgniteInternalFuture<?> fut = multithreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                int iter = 0;
                while ((!(done.get())) && (!(Thread.currentThread().isInterrupted()))) {
                    iter++;
                    List<List<?>> entries = c.query(new SqlFieldsQuery("SELECT * from TestValue").setPageSize(100)).getAll();
                    assert entries != null;
                    assertEquals(("Entries count is not as expected on iteration: " + iter), keyCnt, entries.size());
                    if (((cnt.incrementAndGet()) % logMod) == 0) {
                        GridCacheQueryManager<Object, Object> qryMgr = ((IgniteKernal) (g)).internalCache(DEFAULT_CACHE_NAME).context().queries();
                        assert qryMgr != null;
                        qryMgr.printMemoryStats();
                    }
                } 
            }
        }, threadCnt);
        Thread.sleep(IgniteCacheQueryMultiThreadedSelfTest.DURATION);
        done.set(true);
        fut.get();
    }

    /**
     * Test value.
     */
    private static class TestValue implements Serializable {
        /**
         * Value.
         */
        @QuerySqlField(index = true)
        private int val;

        /**
         *
         *
         * @param val
         * 		Value.
         */
        private TestValue(int val) {
            this.val = val;
        }

        /**
         *
         *
         * @return Value.
         */
        public int value() {
            return val;
        }
    }
}


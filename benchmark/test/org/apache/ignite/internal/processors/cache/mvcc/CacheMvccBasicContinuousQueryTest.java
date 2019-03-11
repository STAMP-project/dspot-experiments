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
package org.apache.ignite.internal.processors.cache.mvcc;


import Cache.Entry;
import CacheMode.PARTITIONED;
import CacheMode.REPLICATED;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.cache.CacheException;
import javax.cache.event.CacheEntryEvent;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 * Basic continuous queries test with enabled mvcc.
 */
public class CacheMvccBasicContinuousQueryTest extends CacheMvccAbstractTest {
    /**
     *
     */
    private static final long LATCH_TIMEOUT = 5000;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAllEntries() throws Exception {
        Ignite node = startGrids(3);
        final IgniteCache cache = node.createCache(cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 1, 2).setCacheMode(REPLICATED).setIndexedTypes(Integer.class, Integer.class));
        ContinuousQuery<Integer, Integer> qry = new ContinuousQuery();
        final Map<Integer, List<Integer>> map = new HashMap<>();
        final CountDownLatch latch = new CountDownLatch(5);
        qry.setLocalListener(new javax.cache.event.CacheEntryUpdatedListener<Integer, Integer>() {
            @Override
            public void onUpdated(Iterable<CacheEntryEvent<? extends Integer, ? extends Integer>> evts) {
                for (CacheEntryEvent<? extends Integer, ? extends Integer> e : evts) {
                    synchronized(map) {
                        List<Integer> vals = map.get(e.getKey());
                        if (vals == null) {
                            vals = new ArrayList<>();
                            map.put(e.getKey(), vals);
                        }
                        vals.add(e.getValue());
                    }
                    latch.countDown();
                }
            }
        });
        try (QueryCursor<Entry<Integer, Integer>> ignored = cache.query(qry)) {
            try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
                String dml = "INSERT INTO Integer (_key, _val) values (1,1),(2,2)";
                cache.query(new SqlFieldsQuery(dml)).getAll();
                tx.commit();
            }
            try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
                String dml1 = "MERGE INTO Integer (_key, _val) values (3,3)";
                cache.query(new SqlFieldsQuery(dml1)).getAll();
                String dml2 = "DELETE FROM Integer WHERE _key = 2";
                cache.query(new SqlFieldsQuery(dml2)).getAll();
                String dml3 = "UPDATE Integer SET _val = 10 WHERE _key = 1";
                cache.query(new SqlFieldsQuery(dml3)).getAll();
                tx.commit();
            }
            try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
                String dml = "INSERT INTO Integer (_key, _val) values (4,4),(5,5)";
                cache.query(new SqlFieldsQuery(dml)).getAll();
                tx.rollback();
            }
            assert latch.await(CacheMvccBasicContinuousQueryTest.LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
            assertEquals(3, map.size());
            List<Integer> vals = map.get(1);
            assertNotNull(vals);
            assertEquals(2, vals.size());
            assertEquals(1, ((int) (vals.get(0))));
            assertEquals(10, ((int) (vals.get(1))));
            vals = map.get(2);
            assertNotNull(vals);
            assertEquals(2, vals.size());
            assertEquals(2, ((int) (vals.get(0))));
            assertEquals(2, ((int) (vals.get(1))));
            vals = map.get(3);
            assertNotNull(vals);
            assertEquals(1, vals.size());
            assertEquals(3, ((int) (vals.get(0))));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCachingMaxSize() throws Exception {
        Ignite node = startGrids(1);
        final IgniteCache cache = node.createCache(cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 1, 2).setCacheMode(PARTITIONED).setIndexedTypes(Integer.class, Integer.class));
        ContinuousQuery<Integer, Integer> qry = new ContinuousQuery();
        qry.setLocalListener(new javax.cache.event.CacheEntryUpdatedListener<Integer, Integer>() {
            @Override
            public void onUpdated(Iterable<CacheEntryEvent<? extends Integer, ? extends Integer>> evts) {
                // No-op.
            }
        });
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try (QueryCursor<Entry<Integer, Integer>> ignored = cache.query(qry)) {
                    try (Transaction tx = node.transactions().txStart(PESSIMISTIC, REPEATABLE_READ)) {
                        for (int i = 0; i < ((MvccCachingManager.TX_SIZE_THRESHOLD) + 1); i++)
                            cache.query(new SqlFieldsQuery((("INSERT INTO Integer (_key, _val) values (" + i) + ", 1)"))).getAll();

                        tx.commit();
                    }
                }
                return null;
            }
        }, CacheException.class, "Transaction is too large. Consider reducing transaction size");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdateCountersGapClosedSimpleReplicated() throws Exception {
        checkUpdateCountersGapIsProcessedSimple(REPLICATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdateCountersGapClosedPartitioned() throws Exception {
        checkUpdateCountersGapsClosed(PARTITIONED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdateCountersGapClosedReplicated() throws Exception {
        checkUpdateCountersGapsClosed(REPLICATED);
    }
}


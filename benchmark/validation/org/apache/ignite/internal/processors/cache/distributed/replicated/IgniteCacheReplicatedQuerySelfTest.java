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
package org.apache.ignite.internal.processors.cache.distributed.replicated;


import Cache.Entry;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.events.Event;
import org.apache.ignite.internal.processors.cache.IgniteCacheAbstractQuerySelfTest;
import org.apache.ignite.internal.util.lang.GridAbsPredicate;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 * Tests replicated query.
 */
public class IgniteCacheReplicatedQuerySelfTest extends IgniteCacheAbstractQuerySelfTest {
    /**
     *
     */
    private static final boolean TEST_DEBUG = false;

    /**
     * Grid1.
     */
    private static Ignite ignite1;

    /**
     * Grid2.
     */
    private static Ignite ignite2;

    /**
     * Grid3.
     */
    private static Ignite ignite3;

    /**
     * Cache1.
     */
    private static IgniteCache<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue> cache1;

    /**
     * Cache2.
     */
    private static IgniteCache<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue> cache2;

    /**
     * Cache3.
     */
    private static IgniteCache<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue> cache3;

    /**
     * Key serialization cnt.
     */
    private static volatile int keySerCnt;

    /**
     * Key deserialization count.
     */
    private static volatile int keyDesCnt;

    /**
     * Value serialization count.
     */
    private static volatile int valSerCnt;

    /**
     * Value deserialization count.
     */
    private static volatile int valDesCnt;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientOnlyNode() throws Exception {
        try {
            Ignite g = startGrid("client");
            IgniteCache<Integer, Integer> c = jcache(g, Integer.class, Integer.class);
            for (int i = 0; i < 10; i++)
                c.put(i, i);

            // Client cache should be empty.
            assertEquals(0, c.localSize());
            Collection<Entry<Integer, Integer>> res = c.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "_key >= 5 order by _key")).getAll();
            assertEquals(5, res.size());
            int i = 5;
            for (Entry<Integer, Integer> e : res) {
                assertEquals(i, e.getKey().intValue());
                assertEquals(i, e.getValue().intValue());
                i++;
            }
        } finally {
            stopGrid("client");
        }
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIterator() throws Exception {
        int keyCnt = 100;
        for (int i = 0; i < keyCnt; i++)
            IgniteCacheReplicatedQuerySelfTest.cache1.put(new IgniteCacheReplicatedQuerySelfTest.CacheKey(i), new IgniteCacheReplicatedQuerySelfTest.CacheValue(("val" + i)));

        assertEquals(keyCnt, IgniteCacheReplicatedQuerySelfTest.cache1.localSize(CachePeekMode.ALL));
        assertEquals(keyCnt, IgniteCacheReplicatedQuerySelfTest.cache2.localSize(CachePeekMode.ALL));
        assertEquals(keyCnt, IgniteCacheReplicatedQuerySelfTest.cache3.localSize(CachePeekMode.ALL));
        QueryCursor<Entry<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue>> qry = IgniteCacheReplicatedQuerySelfTest.cache1.query(new org.apache.ignite.cache.query.SqlQuery<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue>(IgniteCacheReplicatedQuerySelfTest.CacheValue.class, "true"));
        Iterator<Entry<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue>> iter = qry.iterator();
        assert iter.hasNext();
        int cnt = 0;
        while (iter.hasNext()) {
            iter.next();
            cnt++;
        } 
        assertEquals(keyCnt, cnt);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testLocalQueryWithExplicitFlag() throws Exception {
        doTestLocalQuery(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testLocalQueryWithoutExplicitFlag() throws Exception {
        doTestLocalQuery(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDistributedQuery() throws Exception {
        final int keyCnt = 4;
        Transaction tx = IgniteCacheReplicatedQuerySelfTest.ignite1.transactions().txStart();
        try {
            for (int i = 1; i <= keyCnt; i++)
                IgniteCacheReplicatedQuerySelfTest.cache1.put(new IgniteCacheReplicatedQuerySelfTest.CacheKey(i), new IgniteCacheReplicatedQuerySelfTest.CacheValue(String.valueOf(i)));

            tx.commit();
            info(("Committed transaction: " + tx));
        } catch (IgniteException e) {
            tx.rollback();
            throw e;
        }
        GridTestUtils.waitForCondition(new GridAbsPredicate() {
            @Override
            public boolean apply() {
                return ((IgniteCacheReplicatedQuerySelfTest.cache2.size()) == keyCnt) && ((IgniteCacheReplicatedQuerySelfTest.cache3.size()) == keyCnt);
            }
        }, 5000);
        QueryCursor<Entry<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue>> qry = IgniteCacheReplicatedQuerySelfTest.cache1.query(new org.apache.ignite.cache.query.SqlQuery<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue>(IgniteCacheReplicatedQuerySelfTest.CacheValue.class, "val > 1 and val < 4"));
        // Distributed query.
        assertEquals(2, qry.getAll().size());
        // Create new query, old query cannot be modified after it has been executed.
        qry = IgniteCacheReplicatedQuerySelfTest.cache3.query(new org.apache.ignite.cache.query.SqlQuery<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue>(IgniteCacheReplicatedQuerySelfTest.CacheValue.class, "val > 1 and val < 4").setLocal(true));
        // Tests execute on node.
        Iterator<Entry<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue>> iter = qry.iterator();
        assert iter != null;
        assert iter.hasNext();
        iter.next();
        assert iter.hasNext();
        iter.next();
        assert !(iter.hasNext());
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testToString() throws Exception {
        int keyCnt = 4;
        for (int i = 1; i <= keyCnt; i++)
            IgniteCacheReplicatedQuerySelfTest.cache1.put(new IgniteCacheReplicatedQuerySelfTest.CacheKey(i), new IgniteCacheReplicatedQuerySelfTest.CacheValue(String.valueOf(i)));

        // Create query with key filter.
        QueryCursor<Entry<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue>> qry = IgniteCacheReplicatedQuerySelfTest.cache1.query(new org.apache.ignite.cache.query.SqlQuery<IgniteCacheReplicatedQuerySelfTest.CacheKey, IgniteCacheReplicatedQuerySelfTest.CacheValue>(IgniteCacheReplicatedQuerySelfTest.CacheValue.class, "val > 0"));
        assertEquals(keyCnt, qry.getAll().size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLostIterator() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        for (int i = 0; i < 1000; i++)
            cache.put(i, i);

        QueryCursor<Entry<Integer, Integer>> fut = null;
        for (int i = 0; i < ((cache.getConfiguration(CacheConfiguration.class).getMaxQueryIteratorsCount()) + 1); i++) {
            QueryCursor<Entry<Integer, Integer>> q = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "_key >= 0 order by _key"));
            assertEquals(0, ((int) (q.iterator().next().getKey())));
            if (fut == null)
                fut = q;

        }
        final QueryCursor<Entry<Integer, Integer>> fut0 = fut;
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                int i = 0;
                Entry<Integer, Integer> e;
                while ((e = fut0.iterator().next()) != null)
                    assertEquals((++i), ((int) (e.getKey())));

                return null;
            }
        }, IgniteException.class, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeLeft() throws Exception {
        Ignite g = startGrid("client");
        try {
            assertTrue(g.configuration().isClientMode());
            IgniteCache<Integer, Integer> cache = jcache(g, Integer.class, Integer.class);
            for (int i = 0; i < 1000; i++)
                cache.put(i, i);

            // Client cache should be empty.
            assertEquals(0, cache.localSize());
            QueryCursor<Entry<Integer, Integer>> q = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "_key >= 0 order by _key").setPageSize(10));
            assertEquals(0, ((int) (q.iterator().next().getKey())));
            // Query for replicated cache was run on one of nodes.
            ConcurrentMap<?, ?> mapNode1 = queryResultMap(0);
            ConcurrentMap<?, ?> mapNode2 = queryResultMap(1);
            ConcurrentMap<?, ?> mapNode3 = queryResultMap(2);
            assertEquals(1, (((mapNode1.size()) + (mapNode2.size())) + (mapNode3.size())));
            final UUID nodeId = g.cluster().localNode().id();
            final CountDownLatch latch = new CountDownLatch(1);
            grid(0).events().localListen(new org.apache.ignite.lang.IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    if (eventNode().id().equals(nodeId))
                        latch.countDown();

                    return true;
                }
            }, EVT_NODE_LEFT, EVT_NODE_FAILED);
            stopGrid("client");
            latch.await();
            assertEquals(0, mapNode1.size());
            assertEquals(0, mapNode2.size());
            assertEquals(0, mapNode3.size());
        } finally {
            stopGrid("client");
        }
    }

    /**
     * Cache key.
     */
    public static class CacheKey implements Externalizable {
        /**
         * Key.
         */
        private int key;

        /**
         *
         *
         * @param key
         * 		Key.
         */
        CacheKey(int key) {
            this.key = key;
        }

        /**
         *
         */
        public CacheKey() {
            /* No-op. */
        }

        /**
         *
         *
         * @return Key.
         */
        public int getKey() {
            return key;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            key = in.readInt();
            (IgniteCacheReplicatedQuerySelfTest.keyDesCnt)++;
            if (IgniteCacheReplicatedQuerySelfTest.TEST_DEBUG)
                X.println((((("Deserialized demo key [keyDesCnt=" + (IgniteCacheReplicatedQuerySelfTest.keyDesCnt)) + ", key=") + (this)) + ']'));

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(key);
            (IgniteCacheReplicatedQuerySelfTest.keySerCnt)++;
            if (IgniteCacheReplicatedQuerySelfTest.TEST_DEBUG)
                X.println((((("Serialized demo key [serCnt=" + (IgniteCacheReplicatedQuerySelfTest.keySerCnt)) + ", key=") + (this)) + ']'));

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            IgniteCacheReplicatedQuerySelfTest.CacheKey cacheKey;
            if (o instanceof IgniteCacheReplicatedQuerySelfTest.CacheKey)
                cacheKey = ((IgniteCacheReplicatedQuerySelfTest.CacheKey) (o));
            else
                return false;

            return (key) == (cacheKey.key);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return key;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheReplicatedQuerySelfTest.CacheKey.class, this);
        }
    }

    /**
     * Cache value..
     */
    public static class CacheValue implements Externalizable {
        /**
         * Value.
         */
        @QuerySqlField
        private String val;

        /**
         *
         *
         * @param val
         * 		Value.
         */
        CacheValue(String val) {
            this.val = val;
        }

        /**
         *
         */
        public CacheValue() {
            /* No-op. */
        }

        /**
         *
         *
         * @return Value.
         */
        public String getValue() {
            return val;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            val = U.readString(in);
            (IgniteCacheReplicatedQuerySelfTest.valDesCnt)++;
            if (IgniteCacheReplicatedQuerySelfTest.TEST_DEBUG)
                X.println((((("Deserialized demo value [valDesCnt=" + (IgniteCacheReplicatedQuerySelfTest.valDesCnt)) + ", val=") + (this)) + ']'));

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            U.writeString(out, val);
            (IgniteCacheReplicatedQuerySelfTest.valSerCnt)++;
            if (IgniteCacheReplicatedQuerySelfTest.TEST_DEBUG)
                X.println((((("Serialized demo value [serCnt=" + (IgniteCacheReplicatedQuerySelfTest.valSerCnt)) + ", val=") + (this)) + ']'));

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            IgniteCacheReplicatedQuerySelfTest.CacheValue val = ((IgniteCacheReplicatedQuerySelfTest.CacheValue) (o));
            return !((this.val) != null ? !(this.val.equals(val.val)) : (val.val) != null);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return (val) != null ? val.hashCode() : 0;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheReplicatedQuerySelfTest.CacheValue.class, this);
        }
    }
}


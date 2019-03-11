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
import TransactionConcurrency.PESSIMISTIC;
import TransactionIsolation.REPEATABLE_READ;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 * Tests off heap storage when both offheaped and swapped entries exists.
 */
public class GridCacheOffheapIndexGetSelfTest extends GridCommonAbstractTest {
    /**
     * Tests behavior on offheaped entries.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGet() throws Exception {
        IgniteCache<Long, Long> cache = jcache(grid(0), cacheConfiguration(), Long.class, Long.class);
        for (long i = 0; i < 100; i++)
            cache.put(i, i);

        for (long i = 0; i < 100; i++)
            assertEquals(((Long) (i)), cache.get(i));

        SqlQuery<Long, Long> qry = new SqlQuery(Long.class, "_val >= 90");
        List<Entry<Long, Long>> res = cache.query(qry).getAll();
        assertEquals(10, res.size());
        for (Entry<Long, Long> e : res) {
            assertNotNull(e.getKey());
            assertNotNull(e.getValue());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutGet() throws Exception {
        IgniteCache<Object, Object> cache = jcache(grid(0), cacheConfiguration(), Object.class, Object.class);
        Map map = new HashMap();
        try (Transaction tx = grid(0).transactions().txStart(PESSIMISTIC, REPEATABLE_READ, 100000, 1000)) {
            for (int i = 4; i < 400; i++) {
                map.put(("key" + i), new GridCacheOffheapIndexGetSelfTest.TestEntity("value"));
                map.put(i, "value");
            }
            cache.putAll(map);
            tx.commit();
        }
        for (int i = 0; i < 100; i++) {
            cache.get(("key" + i));
            cache.get(i);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testWithExpiryPolicy() throws Exception {
        IgniteCache<Long, Long> cache = jcache(grid(0), cacheConfiguration(), Long.class, Long.class);
        cache = cache.withExpiryPolicy(new GridCacheOffheapIndexGetSelfTest.TestExiryPolicy());
        for (long i = 0; i < 100; i++)
            cache.put(i, i);

        for (long i = 0; i < 100; i++)
            assertEquals(((Long) (i)), cache.get(i));

        SqlQuery<Long, Long> qry = new SqlQuery(Long.class, "_val >= 90");
        List<Entry<Long, Long>> res = cache.query(qry).getAll();
        assertEquals(10, res.size());
        for (Entry<Long, Long> e : res) {
            assertNotNull(e.getKey());
            assertNotNull(e.getValue());
        }
    }

    /**
     *
     */
    private static class TestExiryPolicy implements ExpiryPolicy {
        /**
         * {@inheritDoc }
         */
        @Override
        public Duration getExpiryForCreation() {
            return Duration.ONE_MINUTE;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Duration getExpiryForAccess() {
            return Duration.FIVE_MINUTES;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Duration getExpiryForUpdate() {
            return Duration.TWENTY_MINUTES;
        }
    }

    /**
     * Test entry class.
     */
    private static class TestEntity implements Serializable {
        /**
         * Value.
         */
        @QuerySqlField(index = true)
        private String val;

        /**
         *
         *
         * @param value
         * 		Value.
         */
        public TestEntity(String value) {
            this.val = value;
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
         *
         *
         * @param val
         * 		Value
         */
        public void setValue(String val) {
            this.val = val;
        }
    }
}


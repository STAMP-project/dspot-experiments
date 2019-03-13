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
package org.apache.ignite.internal.processors.query;


import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.cache.CacheException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.events.CacheQueryExecutedEvent;
import org.apache.ignite.events.Event;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.processors.cache.query.SqlFieldsQueryEx;
import org.apache.ignite.internal.util.lang.GridAbsPredicate;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Tests for distributed DML.
 */
@SuppressWarnings({ "unchecked" })
public class IgniteSqlSkipReducerOnUpdateDmlSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static int NODE_COUNT = 4;

    /**
     *
     */
    private static String NODE_CLIENT = "client";

    /**
     *
     */
    private static String CACHE_ORG = "org";

    /**
     *
     */
    private static String CACHE_PERSON = "person";

    /**
     *
     */
    private static String CACHE_POSITION = "pos";

    /**
     *
     */
    private static Ignite client;

    /**
     *
     */
    private static CountDownLatch latch;

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testSimpleUpdateDistributedReplicated() throws Exception {
        fillCaches();
        IgniteCache<Integer, IgniteSqlSkipReducerOnUpdateDmlSelfTest.Position> cache = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).cache(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_POSITION);
        IgniteSqlSkipReducerOnUpdateDmlSelfTest.Position p = cache.get(1);
        List<List<?>> r = cache.query(new SqlFieldsQueryEx("UPDATE Position p SET name = CONCAT('A ', name)", false).setSkipReducerOnUpdate(true)).getAll();
        assertEquals(((long) (cache.size())), r.get(0).get(0));
        assertEquals(cache.get(1).name, ("A " + (p.name)));
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testSimpleUpdateDistributedPartitioned() throws Exception {
        fillCaches();
        IgniteCache<IgniteSqlSkipReducerOnUpdateDmlSelfTest.PersonKey, IgniteSqlSkipReducerOnUpdateDmlSelfTest.Person> cache = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).cache(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_PERSON);
        List<List<?>> r = cache.query(new SqlFieldsQueryEx("UPDATE Person SET position = CASEWHEN(position = 1, 1, position - 1)", false).setSkipReducerOnUpdate(true)).getAll();
        assertEquals(((long) (cache.size())), r.get(0).get(0));
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDistributedUpdateFailedKeys() throws Exception {
        // UPDATE can produce failed keys due to concurrent modification
        fillCaches();
        final IgniteCache<Integer, IgniteSqlSkipReducerOnUpdateDmlSelfTest.Organization> cache = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).cache(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_ORG);
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() {
                return cache.query(new SqlFieldsQueryEx("UPDATE Organization SET rate = Modify(_key, rate - 1)", false).setSkipReducerOnUpdate(true));
            }
        }, CacheException.class, "Failed to update some keys because they had been modified concurrently");
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDistributedUpdateFail() throws Exception {
        fillCaches();
        final IgniteCache cache = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).cache(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_PERSON);
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() {
                return cache.query(new SqlFieldsQueryEx("UPDATE Person SET name = Fail(name)", false).setSkipReducerOnUpdate(true));
            }
        }, CacheException.class, "Failed to execute SQL query");
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @SuppressWarnings("ConstantConditions")
    @Test
    public void testQueryParallelism() throws Exception {
        String cacheName = (IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_ORG) + "x4";
        CacheConfiguration cfg = buildCacheConfiguration(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_ORG).setQueryParallelism(4).setName(cacheName);
        IgniteCache<Integer, IgniteSqlSkipReducerOnUpdateDmlSelfTest.Organization> cache = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).createCache(cfg);
        for (int i = 0; i < 1024; i++)
            cache.put(i, new IgniteSqlSkipReducerOnUpdateDmlSelfTest.Organization(("Acme Inc #" + i), 0));

        List<List<?>> r = cache.query(new SqlFieldsQueryEx((("UPDATE \"" + cacheName) + "\".Organization o SET name = UPPER(name)"), false).setSkipReducerOnUpdate(true)).getAll();
        assertEquals(((long) (cache.size())), r.get(0).get(0));
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testEvents() throws Exception {
        final CountDownLatch latch = new CountDownLatch(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_COUNT);
        final IgnitePredicate<Event> pred = new IgnitePredicate<Event>() {
            @Override
            public boolean apply(Event evt) {
                assert evt instanceof CacheQueryExecutedEvent;
                CacheQueryExecutedEvent qe = ((CacheQueryExecutedEvent) (evt));
                assertNotNull(qe.clause());
                latch.countDown();
                return true;
            }
        };
        for (int idx = 0; idx < (IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_COUNT); idx++)
            grid(idx).events().localListen(pred, EVT_CACHE_QUERY_EXECUTED);

        IgniteCache<Integer, IgniteSqlSkipReducerOnUpdateDmlSelfTest.Organization> cache = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).cache(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_ORG);
        for (int i = 0; i < 1024; i++)
            cache.put(i, new IgniteSqlSkipReducerOnUpdateDmlSelfTest.Organization(("Acme Inc #" + i), 0));

        cache.query(new SqlFieldsQueryEx("UPDATE \"org\".Organization o SET name = UPPER(name)", false).setSkipReducerOnUpdate(true)).getAll();
        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        for (int idx = 0; idx < (IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_COUNT); idx++)
            grid(idx).events().stopLocalListen(pred);

    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testSpecificPartitionsUpdate() throws Exception {
        fillCaches();
        Affinity aff = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).affinity(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_PERSON);
        int numParts = aff.partitions();
        int[] parts = new int[numParts / 2];
        for (int idx = 0; idx < (numParts / 2); idx++)
            parts[idx] = idx * 2;

        IgniteCache<IgniteSqlSkipReducerOnUpdateDmlSelfTest.PersonKey, IgniteSqlSkipReducerOnUpdateDmlSelfTest.Person> cache = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).cache(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_PERSON);
        // UPDATE over even partitions
        cache.query(new SqlFieldsQueryEx("UPDATE Person SET position = 0", false).setSkipReducerOnUpdate(true).setPartitions(parts));
        List<List<?>> rows = cache.query(new SqlFieldsQuery("SELECT _key, position FROM Person")).getAll();
        for (List<?> row : rows) {
            IgniteSqlSkipReducerOnUpdateDmlSelfTest.PersonKey personKey = ((IgniteSqlSkipReducerOnUpdateDmlSelfTest.PersonKey) (row.get(0)));
            int pos = ((Number) (row.get(1))).intValue();
            int part = aff.partition(personKey);
            assertTrue((((part % 2) == 0) ^ (pos != 0)));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCancel() throws Exception {
        IgniteSqlSkipReducerOnUpdateDmlSelfTest.latch = new CountDownLatch(((IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_COUNT) + 1));
        fillCaches();
        final IgniteCache<Integer, IgniteSqlSkipReducerOnUpdateDmlSelfTest.Organization> cache = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).cache(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_ORG);
        final IgniteInternalFuture<Object> fut = GridTestUtils.runAsync(new Callable<Object>() {
            @Override
            public Object call() {
                return cache.query(new SqlFieldsQueryEx("UPDATE Organization SET name = WAIT(name)", false).setSkipReducerOnUpdate(true));
            }
        });
        GridTestUtils.waitForCondition(new GridAbsPredicate() {
            @Override
            public boolean apply() {
                Collection<GridRunningQueryInfo> qCol = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).context().query().runningQueries(0);
                if (qCol.isEmpty())
                    return false;

                for (GridRunningQueryInfo queryInfo : qCol)
                    queryInfo.cancel();

                return true;
            }
        }, 5000);
        IgniteSqlSkipReducerOnUpdateDmlSelfTest.latch.await(5000, TimeUnit.MILLISECONDS);
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws IgniteCheckedException {
                return fut.get();
            }
        }, IgniteCheckedException.class, "Future was cancelled");
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testNodeStopDuringUpdate() throws Exception {
        startGrid(((IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_COUNT) + 1));
        awaitPartitionMapExchange();
        fillCaches();
        IgniteSqlSkipReducerOnUpdateDmlSelfTest.latch = new CountDownLatch((((IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_COUNT) + 1) + 1));
        final IgniteCache<Integer, IgniteSqlSkipReducerOnUpdateDmlSelfTest.Organization> cache = grid(IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_CLIENT).cache(IgniteSqlSkipReducerOnUpdateDmlSelfTest.CACHE_ORG);
        final IgniteInternalFuture<Object> fut = GridTestUtils.runAsync(new Callable<Object>() {
            @Override
            public Object call() {
                return cache.query(new SqlFieldsQueryEx("UPDATE Organization SET name = WAIT(name)", false).setSkipReducerOnUpdate(true));
            }
        });
        final CountDownLatch finalLatch = IgniteSqlSkipReducerOnUpdateDmlSelfTest.latch;
        assertTrue(GridTestUtils.waitForCondition(new GridAbsPredicate() {
            @Override
            public boolean apply() {
                return (finalLatch.getCount()) == 1;
            }
        }, 5000));
        IgniteSqlSkipReducerOnUpdateDmlSelfTest.latch.countDown();
        stopGrid(((IgniteSqlSkipReducerOnUpdateDmlSelfTest.NODE_COUNT) + 1));
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws IgniteCheckedException {
                return fut.get();
            }
        }, IgniteCheckedException.class, "Update failed because map node left topology");
    }

    /**
     *
     */
    private static class Organization {
        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        @QuerySqlField
        int rate;

        /**
         *
         */
        @QuerySqlField
        Date updated;

        /**
         * Constructor.
         *
         * @param name
         * 		Organization name.
         * @param rate
         * 		Rate.
         */
        public Organization(String name, int rate) {
            this.name = name;
            this.rate = rate;
            this.updated = new Date(System.currentTimeMillis());
        }
    }

    /**
     *
     */
    public static class PersonKey {
        /**
         *
         */
        @AffinityKeyMapped
        @QuerySqlField
        private Integer orgId;

        /**
         *
         */
        @QuerySqlField
        private Integer id;

        /**
         * Constructor.
         *
         * @param orgId
         * 		Organization id.
         * @param id
         * 		Person id.
         */
        PersonKey(int orgId, int id) {
            this.orgId = orgId;
            this.id = id;
        }
    }

    /**
     *
     */
    public static class Person {
        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        @QuerySqlField
        int position;

        /**
         *
         */
        @QuerySqlField
        int amount;

        /**
         *
         */
        @QuerySqlField
        Date updated;

        /**
         * Constructor.
         *
         * @param name
         * 		Name.
         * @param position
         * 		Position.
         * @param amount
         * 		Amount.
         */
        private Person(String name, int position, int amount) {
            this.name = name;
            this.position = position;
            this.amount = amount;
            this.updated = new Date(System.currentTimeMillis());
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return ((((name) == null ? 0 : name.hashCode()) ^ (position)) ^ (amount)) ^ ((updated) == null ? 0 : updated.hashCode());
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if (!(obj.getClass().equals(IgniteSqlSkipReducerOnUpdateDmlSelfTest.Person.class)))
                return false;

            IgniteSqlSkipReducerOnUpdateDmlSelfTest.Person other = ((IgniteSqlSkipReducerOnUpdateDmlSelfTest.Person) (obj));
            return (((F.eq(name, other.name)) && ((position) == (other.position))) && ((amount) == (other.amount))) && (F.eq(updated, other.updated));
        }
    }

    /**
     *
     */
    private static class Position {
        /**
         *
         */
        @QuerySqlField
        int id;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        @QuerySqlField
        int rate;

        /**
         * Constructor.
         *
         * @param id
         * 		Id.
         * @param name
         * 		Name.
         * @param rate
         * 		Rate.
         */
        public Position(int id, String name, int rate) {
            this.id = id;
            this.name = name;
            this.rate = rate;
        }
    }
}


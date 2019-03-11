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


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.lang.GridInClosure3;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;


/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class CacheMvccSqlQueriesAbstractTest extends CacheMvccAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAccountsTxSql_SingleNode_SinglePartition() throws Exception {
        accountsTxReadAll(1, 0, 0, 1, new InitIndexing(Integer.class, MvccTestAccount.class), false, ReadMode.SQL, WriteMode.PUT);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAccountsTxSql_WithRemoves_SingleNode_SinglePartition() throws Exception {
        accountsTxReadAll(1, 0, 0, 1, new InitIndexing(Integer.class, MvccTestAccount.class), true, ReadMode.SQL, WriteMode.PUT);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAccountsTxSql_SingleNode() throws Exception {
        accountsTxReadAll(1, 0, 0, 64, new InitIndexing(Integer.class, MvccTestAccount.class), false, ReadMode.SQL, WriteMode.PUT);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAccountsTxSql_SingleNode_Persistence() throws Exception {
        persistence = true;
        testAccountsTxSql_SingleNode();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAccountsTxSumSql_SingleNode() throws Exception {
        accountsTxReadAll(1, 0, 0, 64, new InitIndexing(Integer.class, MvccTestAccount.class), false, ReadMode.SQL_SUM, WriteMode.PUT);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAccountsTxSql_WithRemoves_SingleNode() throws Exception {
        accountsTxReadAll(1, 0, 0, 64, new InitIndexing(Integer.class, MvccTestAccount.class), true, ReadMode.SQL, WriteMode.PUT);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAccountsTxSql_WithRemoves_SingleNode_Persistence() throws Exception {
        persistence = true;
        testAccountsTxSql_WithRemoves_SingleNode();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAccountsTxSql_ClientServer_Backups2() throws Exception {
        accountsTxReadAll(4, 2, 2, 64, new InitIndexing(Integer.class, MvccTestAccount.class), false, ReadMode.SQL, WriteMode.PUT);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdateSingleValue_SingleNode() throws Exception {
        updateSingleValue(true, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdateSingleValue_LocalQuery_SingleNode() throws Exception {
        updateSingleValue(true, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdateSingleValue_ClientServer() throws Exception {
        updateSingleValue(false, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinTransactional_SingleNode() throws Exception {
        joinTransactional(true, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinTransactional_ClientServer() throws Exception {
        joinTransactional(false, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinTransactional_DistributedJoins_ClientServer() throws Exception {
        joinTransactional(false, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinTransactional_DistributedJoins_ClientServer2() throws Exception {
        final int KEYS = 100;
        final int writers = 1;
        final int readers = 4;
        final int CHILDREN_CNT = 10;
        GridInClosure3<Integer, List<TestCache>, AtomicBoolean> writer = new GridInClosure3<Integer, List<TestCache>, AtomicBoolean>() {
            @Override
            public void apply(Integer idx, List<TestCache> caches, AtomicBoolean stop) {
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                int cnt = 0;
                while (!(stop.get())) {
                    TestCache<Object, Object> cache = randomCache(caches, rnd);
                    IgniteTransactions txs = cache.cache.unwrap(Ignite.class).transactions();
                    try {
                        try (Transaction tx = txs.txStart(PESSIMISTIC, REPEATABLE_READ)) {
                            Integer key = rnd.nextInt(KEYS);
                            CacheMvccSqlQueriesAbstractTest.JoinTestParentKey parentKey = new CacheMvccSqlQueriesAbstractTest.JoinTestParentKey(key);
                            CacheMvccSqlQueriesAbstractTest.JoinTestParent parent = ((CacheMvccSqlQueriesAbstractTest.JoinTestParent) (cache.cache.get(parentKey)));
                            if (parent == null) {
                                for (int i = 0; i < CHILDREN_CNT; i++)
                                    cache.cache.put(new CacheMvccSqlQueriesAbstractTest.JoinTestChildKey(((key * 10000) + i)), new CacheMvccSqlQueriesAbstractTest.JoinTestChild(key));

                                cache.cache.put(parentKey, new CacheMvccSqlQueriesAbstractTest.JoinTestParent(key));
                            } else {
                                for (int i = 0; i < CHILDREN_CNT; i++)
                                    cache.cache.remove(new CacheMvccSqlQueriesAbstractTest.JoinTestChildKey(((key * 10000) + i)));

                                cache.cache.remove(parentKey);
                            }
                            tx.commit();
                        }
                        cnt++;
                    } finally {
                        cache.readUnlock();
                    }
                } 
                info(("Writer finished, updates: " + cnt));
            }
        };
        GridInClosure3<Integer, List<TestCache>, AtomicBoolean> reader = new GridInClosure3<Integer, List<TestCache>, AtomicBoolean>() {
            @Override
            public void apply(Integer idx, List<TestCache> caches, AtomicBoolean stop) {
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                SqlFieldsQuery qry = new SqlFieldsQuery(("select c.parentId, p.id from " + "JoinTestChild c left outer join JoinTestParent p on (c.parentId = p.id) where p.id=?")).setDistributedJoins(true);
                int cnt = 0;
                while (!(stop.get())) {
                    TestCache<Object, Object> cache = randomCache(caches, rnd);
                    qry.setArgs(rnd.nextInt(KEYS));
                    try {
                        List<List<?>> res = cache.cache.query(qry).getAll();
                        if (!(res.isEmpty()))
                            assertEquals(CHILDREN_CNT, res.size());

                        cnt++;
                    } finally {
                        cache.readUnlock();
                    }
                } 
                info(("Reader finished, read count: " + cnt));
            }
        };
        readWriteTest(null, 4, 2, 0, DFLT_PARTITION_COUNT, writers, readers, DFLT_TEST_TIME, new InitIndexing(CacheMvccSqlQueriesAbstractTest.JoinTestParentKey.class, CacheMvccSqlQueriesAbstractTest.JoinTestParent.class, CacheMvccSqlQueriesAbstractTest.JoinTestChildKey.class, CacheMvccSqlQueriesAbstractTest.JoinTestChild.class), null, writer, reader);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDistributedJoinSimple() throws Exception {
        startGridsMultiThreaded(4);
        Ignite srv0 = ignite(0);
        int[] backups = new int[]{ 0, 1, 2 };
        for (int b : backups) {
            IgniteCache<Object, Object> cache = srv0.createCache(cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, b, DFLT_PARTITION_COUNT).setIndexedTypes(CacheMvccSqlQueriesAbstractTest.JoinTestParentKey.class, CacheMvccSqlQueriesAbstractTest.JoinTestParent.class, CacheMvccSqlQueriesAbstractTest.JoinTestChildKey.class, CacheMvccSqlQueriesAbstractTest.JoinTestChild.class));
            int cntr = 0;
            int expCnt = 0;
            for (int i = 0; i < 10; i++) {
                CacheMvccSqlQueriesAbstractTest.JoinTestParentKey parentKey = new CacheMvccSqlQueriesAbstractTest.JoinTestParentKey(i);
                cache.put(parentKey, new CacheMvccSqlQueriesAbstractTest.JoinTestParent(i));
                for (int c = 0; c < i; c++) {
                    CacheMvccSqlQueriesAbstractTest.JoinTestChildKey childKey = new CacheMvccSqlQueriesAbstractTest.JoinTestChildKey((cntr++));
                    cache.put(childKey, new CacheMvccSqlQueriesAbstractTest.JoinTestChild(i));
                    expCnt++;
                }
            }
            SqlFieldsQuery qry = new SqlFieldsQuery(("select c.parentId, p.id from " + "JoinTestChild c join JoinTestParent p on (c.parentId = p.id)")).setDistributedJoins(true);
            Map<Integer, Integer> resMap = new HashMap<>();
            List<List<?>> res = cache.query(qry).getAll();
            assertEquals(expCnt, res.size());
            for (List<?> resRow : res) {
                Integer parentId = ((Integer) (resRow.get(0)));
                Integer cnt = resMap.get(parentId);
                if (cnt == null)
                    resMap.put(parentId, 1);
                else
                    resMap.put(parentId, (cnt + 1));

            }
            for (int i = 1; i < 10; i++)
                assertEquals(i, ((Object) (resMap.get(i))));

            srv0.destroyCache(cache.getName());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCacheRecreate() throws Exception {
        cacheRecreate(new InitIndexing(Integer.class, MvccTestAccount.class));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCacheRecreateChangeIndexedType() throws Exception {
        Ignite srv0 = startGrid(0);
        final int PARTS = 64;
        {
            CacheConfiguration<Object, Object> ccfg = cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 0, PARTS).setIndexedTypes(Integer.class, MvccTestAccount.class);
            IgniteCache<Integer, MvccTestAccount> cache = ((IgniteCache) (srv0.createCache(ccfg)));
            for (int k = 0; k < (PARTS * 2); k++) {
                assertNull(cache.get(k));
                int vals = (k % 3) + 1;
                for (int v = 0; v < vals; v++)
                    cache.put(k, new MvccTestAccount(v, 1));

                assertEquals((vals - 1), cache.get(k).val);
            }
            assertEquals((PARTS * 2), cache.query(new org.apache.ignite.cache.query.SqlQuery(MvccTestAccount.class, "true")).getAll().size());
            srv0.destroyCache(cache.getName());
        }
        {
            CacheConfiguration<Object, Object> ccfg = cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 0, PARTS).setIndexedTypes(Integer.class, CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue.class);
            IgniteCache<Integer, CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue> cache = ((IgniteCache) (srv0.createCache(ccfg)));
            for (int k = 0; k < (PARTS * 2); k++) {
                assertNull(cache.get(k));
                int vals = (k % 3) + 1;
                for (int v = 0; v < vals; v++)
                    cache.put(k, new CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue(v));

                assertEquals((vals - 1), cache.get(k).idxVal1);
            }
            assertEquals((PARTS * 2), cache.query(new org.apache.ignite.cache.query.SqlQuery(CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue.class, "true")).getAll().size());
            srv0.destroyCache(cache.getName());
        }
        {
            CacheConfiguration<Object, Object> ccfg = cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 0, PARTS).setIndexedTypes(Long.class, Long.class);
            IgniteCache<Long, Long> cache = ((IgniteCache) (srv0.createCache(ccfg)));
            for (int k = 0; k < (PARTS * 2); k++) {
                assertNull(cache.get(((long) (k))));
                int vals = (k % 3) + 1;
                for (int v = 0; v < vals; v++)
                    cache.put(((long) (k)), ((long) (v)));

                assertEquals(((long) (vals - 1)), ((Object) (cache.get(((long) (k))))));
            }
            assertEquals((PARTS * 2), cache.query(new org.apache.ignite.cache.query.SqlQuery(Long.class, "true")).getAll().size());
            srv0.destroyCache(cache.getName());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testChangeValueType1() throws Exception {
        Ignite srv0 = startGrid(0);
        CacheConfiguration<Object, Object> ccfg = cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 0, DFLT_PARTITION_COUNT).setIndexedTypes(Integer.class, CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue.class, Integer.class, Integer.class);
        IgniteCache<Object, Object> cache = srv0.createCache(ccfg);
        cache.put(1, new CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue(1));
        cache.put(1, new CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue(2));
        checkSingleResult(cache, new SqlFieldsQuery("select idxVal1 from MvccTestSqlIndexValue"), 2);
        cache.put(1, 1);
        assertEquals(0, cache.query(new SqlFieldsQuery("select idxVal1 from MvccTestSqlIndexValue")).getAll().size());
        checkSingleResult(cache, new SqlFieldsQuery("select _val from Integer"), 1);
        cache.put(1, 2);
        checkSingleResult(cache, new SqlFieldsQuery("select _val from Integer"), 2);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testChangeValueType2() throws Exception {
        Ignite srv0 = startGrid(0);
        CacheConfiguration<Object, Object> ccfg = cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 0, DFLT_PARTITION_COUNT).setIndexedTypes(Integer.class, CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue.class, Integer.class, Integer.class);
        IgniteCache<Object, Object> cache = srv0.createCache(ccfg);
        cache.put(1, new CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue(1));
        cache.put(1, new CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue(2));
        checkSingleResult(cache, new SqlFieldsQuery("select idxVal1 from MvccTestSqlIndexValue"), 2);
        cache.remove(1);
        assertEquals(0, cache.query(new SqlFieldsQuery("select idxVal1 from MvccTestSqlIndexValue")).getAll().size());
        cache.put(1, 1);
        assertEquals(0, cache.query(new SqlFieldsQuery("select idxVal1 from MvccTestSqlIndexValue")).getAll().size());
        checkSingleResult(cache, new SqlFieldsQuery("select _val from Integer"), 1);
        cache.put(1, 2);
        checkSingleResult(cache, new SqlFieldsQuery("select _val from Integer"), 2);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCountTransactional_SingleNode() throws Exception {
        countTransactional(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCountTransactional_ClientServer() throws Exception {
        countTransactional(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMaxMinTransactional_SingleNode() throws Exception {
        maxMinTransactional(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMaxMinTransactional_ClientServer() throws Exception {
        maxMinTransactional(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlQueriesWithMvcc() throws Exception {
        Ignite srv0 = startGrid(0);
        IgniteCache<Integer, CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue> cache = ((IgniteCache) (srv0.createCache(cacheConfiguration(cacheMode(), CacheWriteSynchronizationMode.FULL_SYNC, 0, DFLT_PARTITION_COUNT).setIndexedTypes(Integer.class, CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue.class))));
        for (int i = 0; i < 10; i++)
            cache.put(i, new CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue(i));

        sqlQueriesWithMvcc(cache, true);
        sqlQueriesWithMvcc(cache, false);
        // TODO IGNITE-8031
        // startGrid(1);
        // 
        // awaitPartitionMapExchange();
        // 
        // sqlQueriesWithMvcc(cache, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlSimple() throws Exception {
        startGrid(0);
        for (int i = 0; i < 4; i++)
            sqlSimple((i * 512));

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < 5; i++)
            sqlSimple(rnd.nextInt(2048));

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlSimplePutRemoveRandom() throws Exception {
        startGrid(0);
        testSqlSimplePutRemoveRandom(0);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < 3; i++)
            testSqlSimplePutRemoveRandom(rnd.nextInt(2048));

    }

    /**
     *
     */
    static class JoinTestParentKey implements Serializable {
        /**
         *
         */
        private int key;

        /**
         *
         *
         * @param key
         * 		Key.
         */
        JoinTestParentKey(int key) {
            this.key = key;
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

            CacheMvccSqlQueriesAbstractTest.JoinTestParentKey that = ((CacheMvccSqlQueriesAbstractTest.JoinTestParentKey) (o));
            return (key) == (that.key);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return key;
        }
    }

    /**
     *
     */
    static class JoinTestParent {
        /**
         *
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         *
         *
         * @param id
         * 		ID.
         */
        JoinTestParent(int id) {
            this.id = id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(CacheMvccSqlQueriesAbstractTest.JoinTestParent.class, this);
        }
    }

    /**
     *
     */
    static class JoinTestChildKey implements Serializable {
        /**
         *
         */
        @QuerySqlField(index = true)
        private int key;

        /**
         *
         *
         * @param key
         * 		Key.
         */
        JoinTestChildKey(int key) {
            this.key = key;
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

            CacheMvccSqlQueriesAbstractTest.JoinTestChildKey that = ((CacheMvccSqlQueriesAbstractTest.JoinTestChildKey) (o));
            return (key) == (that.key);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return key;
        }
    }

    /**
     *
     */
    static class JoinTestChild {
        /**
         *
         */
        @QuerySqlField(index = true)
        private int parentId;

        /**
         *
         *
         * @param parentId
         * 		Parent ID.
         */
        JoinTestChild(int parentId) {
            this.parentId = parentId;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(CacheMvccSqlQueriesAbstractTest.JoinTestChild.class, this);
        }
    }

    /**
     *
     */
    static class MvccTestSqlIndexValue implements Serializable {
        /**
         *
         */
        @QuerySqlField(index = true)
        private int idxVal1;

        /**
         *
         *
         * @param idxVal1
         * 		Indexed value 1.
         */
        MvccTestSqlIndexValue(int idxVal1) {
            this.idxVal1 = idxVal1;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(CacheMvccSqlQueriesAbstractTest.MvccTestSqlIndexValue.class, this);
        }
    }
}


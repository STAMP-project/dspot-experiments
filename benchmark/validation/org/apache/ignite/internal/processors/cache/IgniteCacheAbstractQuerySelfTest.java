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
import CachePeekMode.ONHEAP;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.cache.CacheException;
import javax.cache.configuration.Factory;
import javax.cache.expiry.Duration;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteBinary;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QuerySqlFunction;
import org.apache.ignite.cache.query.annotations.QueryTextField;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.events.CacheQueryExecutedEvent;
import org.apache.ignite.events.CacheQueryReadEvent;
import org.apache.ignite.events.Event;
import org.apache.ignite.internal.binary.BinaryMarshaller;
import org.apache.ignite.internal.processors.cache.query.QueryCursorEx;
import org.apache.ignite.internal.processors.query.GridQueryFieldMetadata;
import org.apache.ignite.internal.util.tostring.GridToStringExclude;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Various tests for cache queries.
 */
public abstract class IgniteCacheAbstractQuerySelfTest extends GridCommonAbstractTest {
    /**
     * Key count.
     */
    private static final int KEY_CNT = 5000;

    /**
     * Cache store.
     */
    private static IgniteCacheAbstractQuerySelfTest.TestStore store = new IgniteCacheAbstractQuerySelfTest.TestStore();

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testDifferentKeyTypes() throws Exception {
        final IgniteCache<Object, Object> cache = jcache(Object.class, Object.class);
        cache.put(1, "value");
        cache.put("key", "value");
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testDifferentValueTypes() throws Exception {
        IgniteCache<Integer, Object> cache = jcache(Integer.class, Object.class);
        cache.put(7, "value");
        // Put value of different type but for the same key type.
        // Operation should succeed but with warning log message.
        cache.put(7, 1);
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testStringType() throws Exception {
        IgniteCache<Integer, String> cache = jcache(Integer.class, String.class);
        cache.put(666, "test");
        QueryCursor<Entry<Integer, String>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, String>(String.class, "_val='test'"));
        Entry<Integer, String> entry = F.first(qry.getAll());
        assert entry != null;
        assertEquals(666, entry.getKey().intValue());
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testIntegerType() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        int key = 898;
        int val = 2;
        cache.put(key, val);
        QueryCursor<Entry<Integer, Integer>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "_key = ? and _val > 1").setArgs(key));
        Entry<Integer, Integer> entry = F.first(qry.getAll());
        assert entry != null;
        assertEquals(key, entry.getKey().intValue());
        assertEquals(val, entry.getValue().intValue());
    }

    /**
     * Test table alias in SqlQuery.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testTableAliasInSqlQuery() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        int key = 898;
        int val = 2;
        cache.put(key, val);
        org.apache.ignite.cache.query.SqlQuery<Integer, Integer> sqlQry = new org.apache.ignite.cache.query.SqlQuery(Integer.class, "t1._key = ? and t1._val > 1");
        QueryCursor<Entry<Integer, Integer>> qry = cache.query(sqlQry.setAlias("t1").setArgs(key));
        Entry<Integer, Integer> entry = F.first(qry.getAll());
        assert entry != null;
        assertEquals(key, entry.getKey().intValue());
        assertEquals(val, entry.getValue().intValue());
        sqlQry = new org.apache.ignite.cache.query.SqlQuery(Integer.class, "FROM Integer as t1 WHERE t1._key = ? and t1._val > 1");
        qry = cache.query(sqlQry.setAlias("t1").setArgs(key));
        entry = F.first(qry.getAll());
        assert entry != null;
        assertEquals(key, entry.getKey().intValue());
        assertEquals(val, entry.getValue().intValue());
    }

    /**
     * Tests UDFs.
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testUserDefinedFunction() throws IgniteCheckedException {
        // Without alias.
        final IgniteCache<Object, Object> cache = jcache(Object.class, Object.class);
        QueryCursor<List<?>> qry = cache.query(new SqlFieldsQuery("select square(1), square(2)"));
        Collection<List<?>> res = qry.getAll();
        assertEquals(1, res.size());
        List<?> row = res.iterator().next();
        assertEquals(1, row.get(0));
        assertEquals(4, row.get(1));
        // With alias.
        qry = cache.query(new SqlFieldsQuery("select _cube_(1), _cube_(2)"));
        res = qry.getAll();
        assertEquals(1, res.size());
        row = res.iterator().next();
        assertEquals(1, row.get(0));
        assertEquals(8, row.get(1));
        // Not registered.
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                cache.query(new SqlFieldsQuery("select no()"));
                return null;
            }
        }, CacheException.class, null);
    }

    /**
     * Expired entries are not included to result.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testExpiration() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        cache.withExpiryPolicy(new javax.cache.expiry.TouchedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 1000))).put(7, 1);
        List<Entry<Integer, Integer>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "1=1")).getAll();
        Entry<Integer, Integer> res = F.first(qry);
        assertEquals(1, res.getValue().intValue());
        U.sleep(300);// Less than minimal amount of time that must pass before a cache entry is considered expired.

        qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "1=1")).getAll();
        res = F.first(qry);
        assertEquals(1, res.getValue().intValue());
        U.sleep(1800);// No expiry guarantee here. Test should be refactored in case of fails.

        qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "1=1")).getAll();
        res = F.first(qry);
        assertNull(res);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIllegalBounds() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        cache.put(1, 1);
        cache.put(2, 2);
        QueryCursor<Entry<Integer, Integer>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "_key between 2 and 1"));
        assertTrue(qry.getAll().isEmpty());
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testComplexType() throws Exception {
        IgniteCache<IgniteCacheAbstractQuerySelfTest.Key, GridCacheQueryTestValue> cache = jcache(IgniteCacheAbstractQuerySelfTest.Key.class, GridCacheQueryTestValue.class);
        GridCacheQueryTestValue val1 = new GridCacheQueryTestValue();
        val1.setField1("field1");
        val1.setField2(1);
        val1.setField3(1L);
        GridCacheQueryTestValue val2 = new GridCacheQueryTestValue();
        val2.setField1("field2");
        val2.setField2(2);
        val2.setField3(2L);
        val2.setField6(null);
        cache.put(new IgniteCacheAbstractQuerySelfTest.Key(100500), val1);
        cache.put(new IgniteCacheAbstractQuerySelfTest.Key(100501), val2);
        QueryCursor<Entry<IgniteCacheAbstractQuerySelfTest.Key, GridCacheQueryTestValue>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<IgniteCacheAbstractQuerySelfTest.Key, GridCacheQueryTestValue>(GridCacheQueryTestValue.class, "fieldName='field1' and field2=1 and field3=1 and id=100500 and embeddedField2=11 and x=3"));
        Entry<IgniteCacheAbstractQuerySelfTest.Key, GridCacheQueryTestValue> entry = F.first(qry.getAll());
        assertNotNull(entry);
        assertEquals(100500, entry.getKey().id);
        assertEquals(val1, entry.getValue());
    }

    /**
     *
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testComplexTypeKeepBinary() throws Exception {
        if (((ignite().configuration().getMarshaller()) == null) || ((ignite().configuration().getMarshaller()) instanceof BinaryMarshaller)) {
            IgniteCache<IgniteCacheAbstractQuerySelfTest.Key, GridCacheQueryTestValue> cache = jcache(IgniteCacheAbstractQuerySelfTest.Key.class, GridCacheQueryTestValue.class);
            GridCacheQueryTestValue val1 = new GridCacheQueryTestValue();
            val1.setField1("field1");
            val1.setField2(1);
            val1.setField3(1L);
            GridCacheQueryTestValue val2 = new GridCacheQueryTestValue();
            val2.setField1("field2");
            val2.setField2(2);
            val2.setField3(2L);
            val2.setField6(null);
            cache.put(new IgniteCacheAbstractQuerySelfTest.Key(100500), val1);
            cache.put(new IgniteCacheAbstractQuerySelfTest.Key(100501), val2);
            QueryCursor<Entry<BinaryObject, BinaryObject>> qry = cache.withKeepBinary().query(new org.apache.ignite.cache.query.SqlQuery<BinaryObject, BinaryObject>(GridCacheQueryTestValue.class, "fieldName='field1' and field2=1 and field3=1 and id=100500 and embeddedField2=11 and x=3"));
            Entry<BinaryObject, BinaryObject> entry = F.first(qry.getAll());
            assertNotNull(entry);
            assertEquals(Long.valueOf(100500L), entry.getKey().field("id"));
            assertEquals(val1, entry.getValue().deserialize());
        }
    }

    /**
     * Complex key type.
     */
    private static class Key {
        /**
         *
         */
        @QuerySqlField
        private final long id;

        /**
         *
         *
         * @param id
         * 		Id.
         */
        private Key(long id) {
            this.id = id;
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

            IgniteCacheAbstractQuerySelfTest.Key key = ((IgniteCacheAbstractQuerySelfTest.Key) (o));
            return (id) == (key.id);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return ((int) ((id) ^ ((id) >>> 32)));
        }
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSelectQuery() throws Exception {
        IgniteCache<Integer, String> cache = jcache(Integer.class, String.class);
        cache.put(10, "value");
        QueryCursor<Entry<Integer, String>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, String>(String.class, "true"));
        Iterator<Entry<Integer, String>> iter = qry.iterator();
        assert iter != null;
        assert (iter.next()) != null;
    }

    /**
     * JUnit.
     */
    @Test
    public void testSimpleCustomTableName() {
        CacheConfiguration<Integer, Object> cacheConf = new CacheConfiguration<Integer, Object>(cacheConfiguration()).setName(DEFAULT_CACHE_NAME).setQueryEntities(Arrays.asList(new QueryEntity(Integer.class, IgniteCacheAbstractQuerySelfTest.Type1.class), new QueryEntity(Integer.class, IgniteCacheAbstractQuerySelfTest.Type2.class)));
        final IgniteCache<Integer, Object> cache = ignite().getOrCreateCache(cacheConf);
        cache.put(10, new IgniteCacheAbstractQuerySelfTest.Type1(1, "Type1 record #1"));
        cache.put(20, new IgniteCacheAbstractQuerySelfTest.Type1(2, "Type1 record #2"));
        QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.Type1>> qry1 = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.Type1>(IgniteCacheAbstractQuerySelfTest.Type1.class, "FROM Type2"));
        List<Entry<Integer, IgniteCacheAbstractQuerySelfTest.Type1>> all = qry1.getAll();
        assertEquals(2, all.size());
        QueryCursor<List<?>> qry = cache.query(new SqlFieldsQuery("SELECT name FROM Type2"));
        assertEquals(2, qry.getAll().size());
        GridTestUtils.assertThrows(log, new org.apache.ignite.internal.util.lang.GridPlainCallable<Void>() {
            @Override
            public Void call() throws Exception {
                QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.Type1>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.Type1>(IgniteCacheAbstractQuerySelfTest.Type1.class, "FROM Type1"));
                qry.getAll();
                return null;
            }
        }, CacheException.class, null);
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testMixedCustomTableName() throws Exception {
        final IgniteCache<Integer, Object> cache = jcache(Integer.class, Object.class);
        cache.put(10, new IgniteCacheAbstractQuerySelfTest.Type1(1, "Type1 record #1"));
        cache.put(20, new IgniteCacheAbstractQuerySelfTest.Type1(2, "Type1 record #2"));
        cache.put(30, new IgniteCacheAbstractQuerySelfTest.Type2(1, "Type2 record #1"));
        cache.put(40, new IgniteCacheAbstractQuerySelfTest.Type2(2, "Type2 record #2"));
        cache.put(50, new IgniteCacheAbstractQuerySelfTest.Type2(3, "Type2 record #3"));
        QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.Type1>> qry1 = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.Type1>(IgniteCacheAbstractQuerySelfTest.Type1.class, "FROM Type2"));
        List<Entry<Integer, IgniteCacheAbstractQuerySelfTest.Type1>> all = qry1.getAll();
        assertEquals(2, all.size());
        QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.Type2>> qry2 = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.Type2>(IgniteCacheAbstractQuerySelfTest.Type2.class, "FROM Type1"));
        assertEquals(3, qry2.getAll().size());
        QueryCursor<List<?>> qry = cache.query(new SqlFieldsQuery("SELECT name FROM Type1"));
        assertEquals(3, qry.getAll().size());
        qry = cache.query(new SqlFieldsQuery("SELECT name FROM Type2"));
        assertEquals(2, qry.getAll().size());
        GridTestUtils.assertThrows(log, new org.apache.ignite.internal.util.lang.GridPlainCallable<Void>() {
            @Override
            public Void call() throws Exception {
                QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.Type1>> qry1 = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.Type1>(IgniteCacheAbstractQuerySelfTest.Type1.class, "FROM Type1"));
                qry1.getAll().size();
                return null;
            }
        }, CacheException.class, null);
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testDistributedJoinCustomTableName() throws Exception {
        IgniteCache<Integer, Object> cache = jcache(Integer.class, Object.class);
        cache.put(10, new IgniteCacheAbstractQuerySelfTest.Type1(1, "Type1 record #1"));
        cache.put(20, new IgniteCacheAbstractQuerySelfTest.Type1(2, "Type1 record #2"));
        cache.put(30, new IgniteCacheAbstractQuerySelfTest.Type2(1, "Type2 record #1"));
        cache.put(40, new IgniteCacheAbstractQuerySelfTest.Type2(2, "Type2 record #2"));
        cache.put(50, new IgniteCacheAbstractQuerySelfTest.Type2(3, "Type2 record #3"));
        QueryCursor<List<?>> query = cache.query(new SqlFieldsQuery("SELECT t2.name, t1.name FROM Type2 as t2 LEFT JOIN Type1 as t1 ON t1.id = t2.id").setDistributedJoins(((cacheMode()) == (CacheMode.PARTITIONED))));
        assertEquals(2, query.getAll().size());
        query = cache.query(new SqlFieldsQuery("SELECT t2.name, t1.name FROM Type2 as t2 RIGHT JOIN Type1 as t1 ON t1.id = t2.id").setDistributedJoins(((cacheMode()) == (CacheMode.PARTITIONED))));
        assertEquals(3, query.getAll().size());
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testObjectQuery() throws Exception {
        IgniteCache<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue> cache = jcache(Integer.class, IgniteCacheAbstractQuerySelfTest.ObjectValue.class);
        IgniteCacheAbstractQuerySelfTest.ObjectValue val = new IgniteCacheAbstractQuerySelfTest.ObjectValue("test", 0);
        cache.put(1, val);
        QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, "_val=?").setArgs(val));
        Iterator<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>> iter = qry.iterator();
        assert iter != null;
        int expCnt = 1;
        for (int i = 0; i < expCnt; i++)
            assert (iter.next()) != null;

        assert !(iter.hasNext());
        qry = cache.query(new org.apache.ignite.cache.query.TextQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, "test"));
        iter = qry.iterator();
        assert iter != null;
        for (int i = 0; i < expCnt; i++)
            assert (iter.next()) != null;

        assert !(iter.hasNext());
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testObjectWithString() throws Exception {
        IgniteCache<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue2> cache = jcache(Integer.class, IgniteCacheAbstractQuerySelfTest.ObjectValue2.class);
        cache.put(1, new IgniteCacheAbstractQuerySelfTest.ObjectValue2("value 1"));
        cache.put(2, new IgniteCacheAbstractQuerySelfTest.ObjectValue2("value 2"));
        cache.put(3, new IgniteCacheAbstractQuerySelfTest.ObjectValue2("value 3"));
        QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue2>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue2>(IgniteCacheAbstractQuerySelfTest.ObjectValue2.class, "strVal like ?").setArgs("value%"));
        int expCnt = 3;
        List<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue2>> results = qry.getAll();
        assertEquals(expCnt, results.size());
        qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue2>(IgniteCacheAbstractQuerySelfTest.ObjectValue2.class, "strVal > ?").setArgs("value 1"));
        results = qry.getAll();
        assertEquals((expCnt - 1), results.size());
        qry = cache.query(new org.apache.ignite.cache.query.TextQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue2>(IgniteCacheAbstractQuerySelfTest.ObjectValue2.class, "value"));
        results = qry.getAll();
        assertEquals(0, results.size());// Should fail for FULL_TEXT index, but SORTED

    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testEnumObjectQuery() throws Exception {
        final IgniteCache<Long, IgniteCacheAbstractQuerySelfTest.EnumObject> cache = jcache(Long.class, IgniteCacheAbstractQuerySelfTest.EnumObject.class);
        for (long i = 0; i < 50; i++)
            cache.put(i, new IgniteCacheAbstractQuerySelfTest.EnumObject(i, ((i % 2) == 0 ? IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A : IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_B)));

        assertEnumQry("type = ?", IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A, cache, 25);
        assertEnumQry("type > ?", IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_B, cache, 25);
        assertEnumQry("type < ?", IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_B, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A, cache, 25);
        assertEnumQry("type != ?", IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_B, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A, cache, 25);
        assertEmptyEnumQry("type = ?", null, cache);
        assertEmptyEnumQry("type > ?", IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_B, cache);
        assertEmptyEnumQry("type < ?", IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A, cache);
        cache.put(50L, new IgniteCacheAbstractQuerySelfTest.EnumObject(50, null));
        assertNoArgEnumQry("type is null", null, cache, 1);
        assertAnyResTypeEnumQry("type is not null", cache, 50);
        // Additional tests for binary enums.
        IgniteBinary binary = ignite().binary();
        if (binary != null) {
            assertEnumQry("type = ?", IgniteCacheAbstractQuerySelfTest.binaryEnum(binary, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A), IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A, cache, 25);
            assertEnumQry("type > ?", IgniteCacheAbstractQuerySelfTest.binaryEnum(binary, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A), IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_B, cache, 25);
            assertEnumQry("type < ?", IgniteCacheAbstractQuerySelfTest.binaryEnum(binary, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_B), IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A, cache, 25);
            assertEnumQry("type != ?", IgniteCacheAbstractQuerySelfTest.binaryEnum(binary, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_B), IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A, cache, 25);
            assertEmptyEnumQry("type > ?", IgniteCacheAbstractQuerySelfTest.binaryEnum(binary, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_B), cache);
            assertEmptyEnumQry("type < ?", IgniteCacheAbstractQuerySelfTest.binaryEnum(binary, IgniteCacheAbstractQuerySelfTest.EnumType.TYPE_A), cache);
        }
    }

    /**
     * JUnit.
     */
    @Test
    public void testObjectQueryWithSwap() {
        CacheConfiguration<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue> config = new CacheConfiguration<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>(cacheConfiguration());
        config.setOnheapCacheEnabled(true);
        IgniteCache<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue> cache = jcache(ignite(), config, Integer.class, IgniteCacheAbstractQuerySelfTest.ObjectValue.class);
        boolean partitioned = (cache.getConfiguration(CacheConfiguration.class).getCacheMode()) == (CacheMode.PARTITIONED);
        int cnt = 10;
        for (int i = 0; i < cnt; i++)
            cache.put(i, new IgniteCacheAbstractQuerySelfTest.ObjectValue(("test" + i), i));

        for (Ignite g : G.allGrids()) {
            IgniteCache<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue> c = g.cache(cache.getName());
            for (int i = 0; i < cnt; i++) {
                assertNotNull(c.localPeek(i, ONHEAP));
                c.localEvict(Collections.singleton(i));// Swap.

                if ((!partitioned) || (g.affinity(cache.getName()).mapKeyToNode(i).isLocal())) {
                    IgniteCacheAbstractQuerySelfTest.ObjectValue peekVal = c.localPeek(i, ONHEAP);
                    assertNull((((("Non-null value for peek [key=" + i) + ", val=") + peekVal) + ']'), peekVal);
                }
            }
        }
        QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>> qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, "intVal >= ? order by intVal").setArgs(0));
        Iterator<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>> iter = qry.iterator();
        assert iter != null;
        Collection<Integer> set = new HashSet<>(cnt);
        Entry<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue> next;
        while (iter.hasNext()) {
            next = iter.next();
            IgniteCacheAbstractQuerySelfTest.ObjectValue v = next.getValue();
            assert !(set.contains(v.intValue()));
            set.add(v.intValue());
        } 
        assert !(iter.hasNext());
        assertEquals(cnt, set.size());
        for (int i = 0; i < cnt; i++)
            assert set.contains(i);

        qry = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, "MOD(intVal, 2) = ? order by intVal").setArgs(0));
        iter = qry.iterator();
        assert iter != null;
        set.clear();
        while (iter.hasNext()) {
            next = iter.next();
            IgniteCacheAbstractQuerySelfTest.ObjectValue v = next.getValue();
            assert !(set.contains(v.intValue()));
            set.add(v.intValue());
        } 
        assert !(iter.hasNext());
        assertEquals((cnt / 2), set.size());
        for (int i = 0; i < cnt; i++)
            if ((i % 2) == 0)
                assert set.contains(i);
            else
                assert !(set.contains(i));


    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testFullTextSearch() throws Exception {
        IgniteCache<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue> cache = jcache(Integer.class, IgniteCacheAbstractQuerySelfTest.ObjectValue.class);
        // Try to execute on empty cache first.
        QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>> qry = cache.query(new org.apache.ignite.cache.query.TextQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, "full"));
        assert qry.getAll().isEmpty();
        qry = cache.query(new org.apache.ignite.cache.query.TextQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, "full"));
        assert qry.getAll().isEmpty();
        // Now put indexed values into cache.
        int key1 = 1;
        IgniteCacheAbstractQuerySelfTest.ObjectValue val1 = new IgniteCacheAbstractQuerySelfTest.ObjectValue("test full text", 0);
        cache.put(key1, val1);
        int key2 = 2;
        IgniteCacheAbstractQuerySelfTest.ObjectValue val2 = new IgniteCacheAbstractQuerySelfTest.ObjectValue("test full text more", 0);
        cache.put(key2, val2);
        qry = cache.query(new org.apache.ignite.cache.query.TextQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, "full"));
        Collection<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>> res = qry.getAll();
        assertNotNull(res);
        assertEquals(2, res.size());
        qry = cache.query(new org.apache.ignite.cache.query.TextQuery<Integer, IgniteCacheAbstractQuerySelfTest.ObjectValue>(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, "full"));
        res = qry.getAll();
        assertNotNull(res);
        assertEquals(2, res.size());
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testScanQuery() throws Exception {
        IgniteCache<Integer, String> c1 = jcache(Integer.class, String.class);
        Map<Integer, String> map = new HashMap<Integer, String>() {
            {
                for (int i = 0; i < 5000; i++)
                    put(i, ("str" + i));

            }
        };
        for (Map.Entry<Integer, String> e : map.entrySet())
            c1.put(e.getKey(), e.getValue());

        // Scan query.
        QueryCursor<Entry<Integer, String>> qry = c1.query(new org.apache.ignite.cache.query.ScanQuery<Integer, String>());
        Iterator<Entry<Integer, String>> iter = qry.iterator();
        assert iter != null;
        int cnt = 0;
        while (iter.hasNext()) {
            Entry<Integer, String> e = iter.next();
            String expVal = map.get(e.getKey());
            assertNotNull(expVal);
            assertEquals(expVal, e.getValue());
            cnt++;
        } 
        assertEquals(map.size(), cnt);
    }

    /**
     *
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testScanPartitionQuery() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        GridCacheContext cctx = context();
        Map<Integer, Map<Integer, Integer>> entries = new HashMap<>();
        for (int i = 0; i < (IgniteCacheAbstractQuerySelfTest.KEY_CNT); i++) {
            cache.put(i, i);
            int part = cctx.affinity().partition(i);
            Map<Integer, Integer> partEntries = entries.get(part);
            if (partEntries == null)
                entries.put(part, (partEntries = new HashMap<>()));

            partEntries.put(i, i);
        }
        for (int i = 0; i < (cctx.affinity().partitions()); i++) {
            org.apache.ignite.cache.query.ScanQuery<Integer, Integer> scan = new org.apache.ignite.cache.query.ScanQuery(i);
            Collection<Entry<Integer, Integer>> actual = cache.query(scan).getAll();
            Map<Integer, Integer> exp = entries.get(i);
            int size = (exp == null) ? 0 : exp.size();
            assertEquals(("Failed for partition: " + i), size, actual.size());
            if (exp == null)
                assertTrue(actual.isEmpty());
            else
                for (Entry<Integer, Integer> entry : actual)
                    assertTrue(entry.getValue().equals(exp.get(entry.getKey())));


        }
    }

    /**
     * JUnit.
     */
    @Test
    public void testTwoObjectsTextSearch() {
        CacheConfiguration<Object, Object> conf = new CacheConfiguration(cacheConfiguration());
        conf.setQueryEntities(Arrays.asList(new QueryEntity(Integer.class, IgniteCacheAbstractQuerySelfTest.ObjectValue.class), new QueryEntity(String.class, IgniteCacheAbstractQuerySelfTest.ObjectValueOther.class)));
        IgniteCache<Object, Object> c = jcache(ignite(), conf, Object.class, Object.class);
        c.put(1, new IgniteCacheAbstractQuerySelfTest.ObjectValue("ObjectValue str", 1));
        c.put("key", new IgniteCacheAbstractQuerySelfTest.ObjectValueOther("ObjectValueOther str"));
        Collection<Entry<Object, Object>> res = c.query(new org.apache.ignite.cache.query.TextQuery(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, "str")).getAll();
        assert res != null;
        int expCnt = 1;
        assert (res.size()) == expCnt;
        assert (F.first(res).getValue().getClass()) == (IgniteCacheAbstractQuerySelfTest.ObjectValue.class);
        res = c.query(new org.apache.ignite.cache.query.TextQuery(IgniteCacheAbstractQuerySelfTest.ObjectValueOther.class, "str")).getAll();
        assert res != null;
        assert (res.size()) == expCnt;
        assert (F.first(res).getValue().getClass()) == (IgniteCacheAbstractQuerySelfTest.ObjectValueOther.class);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPrimitiveType() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        cache.put(1, 1);
        cache.put(2, 2);
        QueryCursor<Entry<Integer, Integer>> q = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "_val > 1"));
        Collection<Entry<Integer, Integer>> res = q.getAll();
        assertEquals(1, res.size());
        for (Entry<Integer, Integer> e : res) {
            assertEquals(2, ((int) (e.getKey())));
            assertEquals(2, ((int) (e.getValue())));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPaginationIteratorDefaultCache() throws Exception {
        testPaginationIterator(jcache(ignite(), cacheConfiguration(), DEFAULT_CACHE_NAME, Integer.class, Integer.class));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPaginationIteratorNamedCache() throws Exception {
        testPaginationIterator(jcache(ignite(), cacheConfiguration(), Integer.class, Integer.class));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPaginationGetDefaultCache() throws Exception {
        testPaginationGet(jcache(ignite(), cacheConfiguration(), DEFAULT_CACHE_NAME, Integer.class, Integer.class));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPaginationGetNamedCache() throws Exception {
        testPaginationGet(jcache(ignite(), cacheConfiguration(), Integer.class, Integer.class));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanFilters() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        for (int i = 0; i < 50; i++)
            cache.put(i, i);

        QueryCursor<Entry<Integer, Integer>> q = cache.query(new org.apache.ignite.cache.query.ScanQuery(new org.apache.ignite.lang.IgniteBiPredicate<Integer, Integer>() {
            @Override
            public boolean apply(Integer k, Integer v) {
                assertNotNull(k);
                assertNotNull(v);
                return (k >= 20) && (v < 40);
            }
        }));
        List<Entry<Integer, Integer>> list = new java.util.ArrayList(q.getAll());
        Collections.sort(list, new java.util.Comparator<Entry<Integer, Integer>>() {
            @Override
            public int compare(Entry<Integer, Integer> e1, Entry<Integer, Integer> e2) {
                return e1.getKey().compareTo(e2.getKey());
            }
        });
        assertEquals(20, list.size());
        for (int i = 20; i < 40; i++) {
            Entry<Integer, Integer> e = list.get((i - 20));
            assertEquals(i, ((int) (e.getKey())));
            assertEquals(i, ((int) (e.getValue())));
        }
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		if failed.
     */
    @Test
    public void testBadHashObjectKey() throws IgniteCheckedException {
        IgniteCache<IgniteCacheAbstractQuerySelfTest.BadHashKeyObject, Byte> cache = jcache(IgniteCacheAbstractQuerySelfTest.BadHashKeyObject.class, Byte.class);
        cache.put(new IgniteCacheAbstractQuerySelfTest.BadHashKeyObject("test_key1"), ((byte) (1)));
        cache.put(new IgniteCacheAbstractQuerySelfTest.BadHashKeyObject("test_key0"), ((byte) (10)));
        cache.put(new IgniteCacheAbstractQuerySelfTest.BadHashKeyObject("test_key1"), ((byte) (7)));
        assertEquals(10, cache.query(new org.apache.ignite.cache.query.SqlQuery<IgniteCacheAbstractQuerySelfTest.BadHashKeyObject, Byte>(Byte.class, "_key = ?").setArgs(new IgniteCacheAbstractQuerySelfTest.BadHashKeyObject("test_key0"))).getAll().get(0).getValue().intValue());
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		if failed.
     */
    @Test
    public void testTextIndexedKey() throws IgniteCheckedException {
        IgniteCache<IgniteCacheAbstractQuerySelfTest.ObjectValue, Long> cache = jcache(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, Long.class);
        cache.put(new IgniteCacheAbstractQuerySelfTest.ObjectValue("test_key1", 10), 19L);
        cache.put(new IgniteCacheAbstractQuerySelfTest.ObjectValue("test_key0", 11), 11005L);
        cache.put(new IgniteCacheAbstractQuerySelfTest.ObjectValue("test_key1", 12), 17L);
        assertEquals(11005L, cache.query(new org.apache.ignite.cache.query.TextQuery<IgniteCacheAbstractQuerySelfTest.ObjectValue, Long>(Long.class, "test_key0")).getAll().get(0).getValue().intValue());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOrderByOnly() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        for (int i = 0; i < 10; i++)
            cache.put(i, i);

        QueryCursor<Entry<Integer, Integer>> q = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "_key >= 0"));
        Collection<Entry<Integer, Integer>> res = q.getAll();
        assertEquals(10, res.size());
        if ((cacheMode()) != (CacheMode.PARTITIONED)) {
            Iterator<Entry<Integer, Integer>> it = res.iterator();
            for (Integer i = 0; i < 10; i++) {
                assertTrue(it.hasNext());
                Entry<Integer, Integer> e = it.next();
                assertEquals(i, e.getKey());
                assertEquals(i, e.getValue());
            }
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLimitOnly() throws Exception {
        IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        for (int i = 0; i < 10; i++)
            cache.put(i, i);

        QueryCursor<Entry<Integer, Integer>> q = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, Integer>(Integer.class, "limit 5"));
        Collection<Entry<Integer, Integer>> res = q.getAll();
        assertEquals(5, res.size());
        Set<Integer> checkDuplicate = new HashSet<>();
        for (Entry<Integer, Integer> e : res) {
            assert ((e.getKey()) < 10) && ((e.getKey()) >= 0);
            assert ((e.getValue()) < 10) && ((e.getValue()) >= 0);
            checkDuplicate.add(e.getValue());
        }
        assertEquals(5, checkDuplicate.size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testArray() throws Exception {
        IgniteCache<Integer, IgniteCacheAbstractQuerySelfTest.ArrayObject> cache = jcache(Integer.class, IgniteCacheAbstractQuerySelfTest.ArrayObject.class);
        cache.put(1, new IgniteCacheAbstractQuerySelfTest.ArrayObject(new Long[]{ 1L, null, 3L }));
        cache.put(2, new IgniteCacheAbstractQuerySelfTest.ArrayObject(new Long[]{ 4L, 5L, 6L }));
        QueryCursor<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ArrayObject>> q = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, IgniteCacheAbstractQuerySelfTest.ArrayObject>(IgniteCacheAbstractQuerySelfTest.ArrayObject.class, "array_contains(arr, cast(? as long))").setArgs(4));
        Collection<Entry<Integer, IgniteCacheAbstractQuerySelfTest.ArrayObject>> res = q.getAll();
        assertEquals(1, res.size());
        Entry<Integer, IgniteCacheAbstractQuerySelfTest.ArrayObject> e = F.first(res);
        assertEquals(2, ((int) (e.getKey())));
        Assert.assertArrayEquals(new Long[]{ 4L, 5L, 6L }, e.getValue().arr);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlQueryEvents() throws Exception {
        checkSqlQueryEvents();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFieldsQueryMetadata() throws Exception {
        IgniteCache<UUID, IgniteCacheAbstractQuerySelfTest.Person> cache = jcache(UUID.class, IgniteCacheAbstractQuerySelfTest.Person.class);
        for (int i = 0; i < 100; i++)
            cache.put(UUID.randomUUID(), new IgniteCacheAbstractQuerySelfTest.Person(("name-" + i), ((i + 1) * 100)));

        QueryCursor<List<?>> cur = cache.query(new SqlFieldsQuery("select name, salary from Person where name like ?").setArgs("name-"));
        assertTrue((cur instanceof QueryCursorEx));
        QueryCursorEx<List<?>> curEx = ((QueryCursorEx<List<?>>) (cur));
        List<GridQueryFieldMetadata> meta = curEx.fieldsMeta();
        assertNotNull(meta);
        assertEquals(2, meta.size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanQueryEvents() throws Exception {
        final Map<Integer, Integer> map = new ConcurrentHashMap<>();
        final IgniteCache<Integer, Integer> cache = jcache(Integer.class, Integer.class);
        final boolean evtsDisabled = cache.getConfiguration(CacheConfiguration.class).isEventsDisabled();
        final CountDownLatch latch = new CountDownLatch((evtsDisabled ? 0 : 10));
        final CountDownLatch execLatch = new CountDownLatch((evtsDisabled ? 0 : (cacheMode()) == (CacheMode.REPLICATED) ? 1 : gridCount()));
        IgnitePredicate[] objReadLsnrs = new IgnitePredicate[gridCount()];
        IgnitePredicate[] qryExecLsnrs = new IgnitePredicate[gridCount()];
        for (int i = 0; i < (gridCount()); i++) {
            IgnitePredicate<Event> pred = new IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    assert evt instanceof CacheQueryReadEvent;
                    if (evtsDisabled)
                        fail("Cache events are disabled");

                    CacheQueryReadEvent<Integer, Integer> qe = ((CacheQueryReadEvent<Integer, Integer>) (evt));
                    assertEquals(SCAN.name(), qe.queryType());
                    assertEquals(cache.getName(), qe.cacheName());
                    assertNull(qe.className());
                    assertNull(null, qe.clause());
                    assertNotNull(qe.scanQueryFilter());
                    assertNull(qe.continuousQueryFilter());
                    assertNull(qe.arguments());
                    map.put(qe.key(), qe.value());
                    latch.countDown();
                    return true;
                }
            };
            grid(i).events().localListen(pred, EVT_CACHE_QUERY_OBJECT_READ);
            objReadLsnrs[i] = pred;
            IgnitePredicate<Event> execPred = new IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    assert evt instanceof CacheQueryExecutedEvent;
                    if (evtsDisabled)
                        fail("Cache events are disabled");

                    CacheQueryExecutedEvent qe = ((CacheQueryExecutedEvent) (evt));
                    assertEquals(SCAN.name(), qe.queryType());
                    assertEquals(cache.getName(), qe.cacheName());
                    assertNull(qe.className());
                    assertNull(null, qe.clause());
                    assertNotNull(qe.scanQueryFilter());
                    assertNull(qe.continuousQueryFilter());
                    assertNull(qe.arguments());
                    execLatch.countDown();
                    return true;
                }
            };
            grid(i).events().localListen(execPred, EVT_CACHE_QUERY_EXECUTED);
            qryExecLsnrs[i] = execPred;
        }
        try {
            for (int i = 0; i < 20; i++)
                cache.put(i, i);

            org.apache.ignite.lang.IgniteBiPredicate<Integer, Integer> filter = new org.apache.ignite.lang.IgniteBiPredicate<Integer, Integer>() {
                @Override
                public boolean apply(Integer k, Integer v) {
                    return k >= 10;
                }
            };
            QueryCursor<Entry<Integer, Integer>> q = cache.query(new org.apache.ignite.cache.query.ScanQuery(filter));
            q.getAll();
            assert latch.await(1000, TimeUnit.MILLISECONDS);
            assert execLatch.await(1000, TimeUnit.MILLISECONDS);
            if (!evtsDisabled) {
                assertEquals(10, map.size());
                for (int i = 10; i < 20; i++)
                    assertEquals(i, map.get(i).intValue());

            }
        } finally {
            for (int i = 0; i < (gridCount()); i++) {
                grid(i).events().stopLocalListen(objReadLsnrs[i]);
                grid(i).events().stopLocalListen(qryExecLsnrs[i]);
            }
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTextQueryEvents() throws Exception {
        final Map<UUID, IgniteCacheAbstractQuerySelfTest.Person> map = new ConcurrentHashMap<>();
        final IgniteCache<UUID, IgniteCacheAbstractQuerySelfTest.Person> cache = jcache(UUID.class, IgniteCacheAbstractQuerySelfTest.Person.class);
        final boolean evtsDisabled = cache.getConfiguration(CacheConfiguration.class).isEventsDisabled();
        final CountDownLatch latch = new CountDownLatch((evtsDisabled ? 0 : 2));
        final CountDownLatch execLatch = new CountDownLatch((evtsDisabled ? 0 : (cacheMode()) == (CacheMode.REPLICATED) ? 1 : gridCount()));
        IgnitePredicate[] objReadLsnrs = new IgnitePredicate[gridCount()];
        IgnitePredicate[] qryExecLsnrs = new IgnitePredicate[gridCount()];
        for (int i = 0; i < (gridCount()); i++) {
            IgnitePredicate<Event> objReadPred = new IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    assert evt instanceof CacheQueryReadEvent;
                    if (evtsDisabled)
                        fail("Cache events are disabled");

                    CacheQueryReadEvent<UUID, IgniteCacheAbstractQuerySelfTest.Person> qe = ((CacheQueryReadEvent<UUID, IgniteCacheAbstractQuerySelfTest.Person>) (evt));
                    assertEquals(FULL_TEXT.name(), qe.queryType());
                    assertEquals(cache.getName(), qe.cacheName());
                    assertEquals("Person", qe.className());
                    assertEquals("White", qe.clause());
                    assertNull(qe.scanQueryFilter());
                    assertNull(qe.continuousQueryFilter());
                    assertNull(qe.arguments());
                    map.put(qe.key(), qe.value());
                    latch.countDown();
                    return true;
                }
            };
            grid(i).events().localListen(objReadPred, EVT_CACHE_QUERY_OBJECT_READ);
            objReadLsnrs[i] = objReadPred;
            IgnitePredicate<Event> qryExecPred = new IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    assert evt instanceof CacheQueryExecutedEvent;
                    if (evtsDisabled)
                        fail("Cache events are disabled");

                    CacheQueryExecutedEvent qe = ((CacheQueryExecutedEvent) (evt));
                    assertEquals(FULL_TEXT.name(), qe.queryType());
                    assertEquals(cache.getName(), qe.cacheName());
                    assertEquals("Person", qe.className());
                    assertEquals("White", qe.clause());
                    assertNull(qe.scanQueryFilter());
                    assertNull(qe.continuousQueryFilter());
                    assertNull(qe.arguments());
                    execLatch.countDown();
                    return true;
                }
            };
            grid(i).events().localListen(qryExecPred, EVT_CACHE_QUERY_EXECUTED);
            qryExecLsnrs[i] = qryExecPred;
        }
        try {
            UUID k1 = UUID.randomUUID();
            UUID k2 = UUID.randomUUID();
            UUID k3 = UUID.randomUUID();
            cache.put(k1, new IgniteCacheAbstractQuerySelfTest.Person("Bob White", 1000));
            cache.put(k2, new IgniteCacheAbstractQuerySelfTest.Person("Tom White", 1000));
            cache.put(k3, new IgniteCacheAbstractQuerySelfTest.Person("Mike Green", 1000));
            QueryCursor<Entry<UUID, IgniteCacheAbstractQuerySelfTest.Person>> q = cache.query(new org.apache.ignite.cache.query.TextQuery<UUID, IgniteCacheAbstractQuerySelfTest.Person>(IgniteCacheAbstractQuerySelfTest.Person.class, "White"));
            q.getAll();
            assert latch.await(1000, TimeUnit.MILLISECONDS);
            assert execLatch.await(1000, TimeUnit.MILLISECONDS);
            if (!evtsDisabled) {
                assertEquals(2, map.size());
                assertEquals("Bob White", map.get(k1).name());
                assertEquals("Tom White", map.get(k2).name());
            }
        } finally {
            for (int i = 0; i < (gridCount()); i++) {
                grid(i).events().stopLocalListen(objReadLsnrs[i]);
                grid(i).events().stopLocalListen(qryExecLsnrs[i]);
            }
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFieldsQueryEvents() throws Exception {
        final IgniteCache<UUID, IgniteCacheAbstractQuerySelfTest.Person> cache = jcache(UUID.class, IgniteCacheAbstractQuerySelfTest.Person.class);
        final boolean evtsDisabled = cache.getConfiguration(CacheConfiguration.class).isEventsDisabled();
        final CountDownLatch execLatch = new CountDownLatch((evtsDisabled ? 0 : (cacheMode()) == (CacheMode.REPLICATED) ? 1 : gridCount()));
        IgnitePredicate[] qryExecLsnrs = new IgnitePredicate[gridCount()];
        for (int i = 0; i < (gridCount()); i++) {
            IgnitePredicate<Event> pred = new IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    assert evt instanceof CacheQueryExecutedEvent;
                    if (evtsDisabled)
                        fail("Cache events are disabled");

                    CacheQueryExecutedEvent qe = ((CacheQueryExecutedEvent) (evt));
                    assertEquals(cache.getName(), qe.cacheName());
                    assertNotNull(qe.clause());
                    assertNull(qe.scanQueryFilter());
                    assertNull(qe.continuousQueryFilter());
                    Assert.assertArrayEquals(new Integer[]{ 10 }, qe.arguments());
                    execLatch.countDown();
                    return true;
                }
            };
            grid(i).events().localListen(pred, EVT_CACHE_QUERY_EXECUTED);
            qryExecLsnrs[i] = pred;
        }
        try {
            for (int i = 1; i <= 20; i++)
                cache.put(UUID.randomUUID(), new IgniteCacheAbstractQuerySelfTest.Person(("Person " + i), i));

            QueryCursor<List<?>> q = cache.query(new SqlFieldsQuery("select _key, name from Person where salary > ?").setArgs(10));
            q.getAll();
            assert execLatch.await(1000, TimeUnit.MILLISECONDS);
        } finally {
            for (int i = 0; i < (gridCount()); i++)
                grid(i).events().stopLocalListen(qryExecLsnrs[i]);

        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLocalSqlQueryFromClient() throws Exception {
        try {
            Ignite g = startGrid("client");
            IgniteCache<Integer, Integer> c = jcache(g, Integer.class, Integer.class);
            for (int i = 0; i < 10; i++)
                c.put(i, i);

            org.apache.ignite.cache.query.SqlQuery<Integer, Integer> qry = new org.apache.ignite.cache.query.SqlQuery(Integer.class, "_key >= 5 order by _key");
            qry.setLocal(true);
            assertThrowsWithCause(() -> c.query(qry), CacheException.class);
        } finally {
            stopGrid("client");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLocalSqlFieldsQueryFromClient() throws Exception {
        try {
            Ignite g = startGrid("client");
            IgniteCache<UUID, IgniteCacheAbstractQuerySelfTest.Person> c = jcache(g, UUID.class, IgniteCacheAbstractQuerySelfTest.Person.class);
            IgniteCacheAbstractQuerySelfTest.Person p = new IgniteCacheAbstractQuerySelfTest.Person("Jon", 1500);
            c.put(p.id(), p);
            SqlFieldsQuery qry = new SqlFieldsQuery("select count(*) from Person");
            qry.setLocal(true);
            assertThrowsWithCause(() -> c.query(qry), CacheException.class);
        } finally {
            stopGrid("client");
        }
    }

    /**
     *
     */
    private static class ArrayObject implements Serializable {
        /**
         *
         */
        @QuerySqlField
        private Long[] arr;

        /**
         *
         *
         * @param arr
         * 		Array.
         */
        private ArrayObject(Long[] arr) {
            this.arr = arr;
        }
    }

    /**
     *
     */
    public static class Person implements Externalizable {
        /**
         *
         */
        @GridToStringExclude
        @QuerySqlField
        private UUID id = UUID.randomUUID();

        /**
         *
         */
        @QuerySqlField
        @QueryTextField
        private String name;

        /**
         *
         */
        @QuerySqlField
        private int salary;

        /**
         *
         */
        @QuerySqlField(index = true)
        private int fake$Field;

        /**
         * Required by {@link Externalizable}.
         */
        public Person() {
            // No-op.
        }

        /**
         *
         *
         * @param name
         * 		Name.
         * @param salary
         * 		Salary.
         */
        public Person(String name, int salary) {
            assert name != null;
            assert salary > 0;
            this.name = name;
            this.salary = salary;
        }

        /**
         *
         *
         * @return Id.
         */
        public UUID id() {
            return id;
        }

        /**
         *
         *
         * @return Name.
         */
        public String name() {
            return name;
        }

        /**
         *
         *
         * @return Salary.
         */
        public int salary() {
            return salary;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            U.writeUuid(out, id);
            U.writeString(out, name);
            out.writeInt(salary);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            id = U.readUuid(in);
            name = U.readString(in);
            salary = in.readInt();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return ((id.hashCode()) + (31 * (name.hashCode()))) + ((31 * 31) * (salary));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == (this))
                return true;

            if (!(obj instanceof IgniteCacheAbstractQuerySelfTest.Person))
                return false;

            IgniteCacheAbstractQuerySelfTest.Person that = ((IgniteCacheAbstractQuerySelfTest.Person) (obj));
            return ((that.id.equals(id)) && (that.name.equals(name))) && ((that.salary) == (salary));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheAbstractQuerySelfTest.Person.class, this);
        }
    }

    /**
     *
     */
    public static class Type1 implements Serializable {
        /**
         *
         */
        private int id;

        /**
         *
         */
        private String name;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param name
         * 		Name.
         */
        Type1(int id, String name) {
            assert name != null;
            assert id > 0;
            this.name = name;
            this.id = id;
        }

        /**
         *
         *
         * @return Name.
         */
        public String name() {
            return name;
        }

        /**
         *
         *
         * @return ID.
         */
        public int id() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return (name.hashCode()) + (31 * (id));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == (this))
                return true;

            if (!(obj instanceof IgniteCacheAbstractQuerySelfTest.Type1))
                return false;

            IgniteCacheAbstractQuerySelfTest.Type1 that = ((IgniteCacheAbstractQuerySelfTest.Type1) (obj));
            return (that.name.equals(name)) && ((that.id) == (id));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheAbstractQuerySelfTest.Type1.class, this);
        }
    }

    /**
     *
     */
    public static class Type2 implements Serializable {
        /**
         *
         */
        private int id;

        /**
         *
         */
        private String name;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param name
         * 		Name.
         */
        Type2(int id, String name) {
            assert name != null;
            assert id > 0;
            this.name = name;
            this.id = id;
        }

        /**
         *
         *
         * @return Name.
         */
        public String name() {
            return name;
        }

        /**
         *
         *
         * @return ID.
         */
        public int id() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return (name.hashCode()) + (31 * (id));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == (this))
                return true;

            if (!(obj instanceof IgniteCacheAbstractQuerySelfTest.Type2))
                return false;

            IgniteCacheAbstractQuerySelfTest.Type2 that = ((IgniteCacheAbstractQuerySelfTest.Type2) (obj));
            return (that.name.equals(name)) && ((that.id) == (id));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheAbstractQuerySelfTest.Type2.class, this);
        }
    }

    /**
     * Test value object.
     */
    @SuppressWarnings("PublicInnerClass")
    public static class ObjectValue implements Serializable {
        /**
         * String value.
         */
        @QueryTextField
        private String strVal;

        /**
         * Integer value.
         */
        @QuerySqlField
        private int intVal;

        /**
         * Constructor.
         *
         * @param strVal
         * 		String value.
         * @param intVal
         * 		Integer value.
         */
        ObjectValue(String strVal, int intVal) {
            this.strVal = strVal;
            this.intVal = intVal;
        }

        /**
         * Gets value.
         *
         * @return Value.
         */
        public String getStringValue() {
            return strVal;
        }

        /**
         *
         *
         * @return Integer value.
         */
        public int intValue() {
            return intVal;
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

            IgniteCacheAbstractQuerySelfTest.ObjectValue other = ((IgniteCacheAbstractQuerySelfTest.ObjectValue) (o));
            return (strVal) == null ? (other.strVal) == null : strVal.equals(other.strVal);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return (strVal) != null ? strVal.hashCode() : 0;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheAbstractQuerySelfTest.ObjectValue.class, this);
        }
    }

    /**
     * Another test value object.
     */
    private static class ObjectValueOther {
        /**
         * Value.
         */
        @QueryTextField
        private String val;

        /**
         *
         *
         * @param val
         * 		String value.
         */
        ObjectValueOther(String val) {
            this.val = val;
        }

        /**
         * Gets value.
         *
         * @return Value.
         */
        public String value() {
            return val;
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

            IgniteCacheAbstractQuerySelfTest.ObjectValueOther other = ((IgniteCacheAbstractQuerySelfTest.ObjectValueOther) (o));
            return (val) == null ? (other.val) == null : val.equals(other.val);
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
            return S.toString(IgniteCacheAbstractQuerySelfTest.ObjectValueOther.class, this);
        }
    }

    /**
     * Another test value object.
     */
    private static class ObjectValue2 {
        /**
         * Value.
         */
        private String strVal;

        /**
         *
         *
         * @param strVal
         * 		String value.
         */
        ObjectValue2(String strVal) {
            this.strVal = strVal;
        }

        /**
         * Gets value.
         *
         * @return Value.
         */
        public String value() {
            return strVal;
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

            IgniteCacheAbstractQuerySelfTest.ObjectValue2 other = ((IgniteCacheAbstractQuerySelfTest.ObjectValue2) (o));
            return (strVal) == null ? (other.strVal) == null : strVal.equals(other.strVal);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return (strVal) != null ? strVal.hashCode() : 0;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheAbstractQuerySelfTest.ObjectValue2.class, this);
        }
    }

    /**
     *
     */
    private static class BadHashKeyObject implements Serializable , Comparable<IgniteCacheAbstractQuerySelfTest.BadHashKeyObject> {
        /**
         *
         */
        @QuerySqlField(index = false)
        private final String str;

        /**
         *
         *
         * @param str
         * 		String.
         */
        private BadHashKeyObject(String str) {
            this.str = (str == null) ? "" : str;
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

            IgniteCacheAbstractQuerySelfTest.BadHashKeyObject keyObj = ((IgniteCacheAbstractQuerySelfTest.BadHashKeyObject) (o));
            return str.equals(keyObj.str);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return 10;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int compareTo(IgniteCacheAbstractQuerySelfTest.BadHashKeyObject o) {
            return str.compareTo(o.str);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheAbstractQuerySelfTest.BadHashKeyObject.class, this);
        }
    }

    /**
     * Test store.
     */
    private static class TestStore extends CacheStoreAdapter<Object, Object> {
        /**
         *
         */
        private Map<Object, Object> map = new ConcurrentHashMap<>();

        /**
         *
         */
        void reset() {
            map.clear();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Object load(Object key) {
            return map.get(key);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void write(javax.cache.Cache.Entry<? extends Object, ? extends Object> e) {
            map.put(e.getKey(), e.getValue());
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void delete(Object key) {
            map.remove(key);
        }
    }

    /**
     * Functions for test.
     */
    @SuppressWarnings("PublicInnerClass")
    public static class SqlFunctions {
        /**
         *
         *
         * @param x
         * 		Argument.
         * @return Square of given value.
         */
        @QuerySqlFunction
        public static int square(int x) {
            return x * x;
        }

        /**
         *
         *
         * @param x
         * 		Argument.
         * @return Cube of given value.
         */
        @QuerySqlFunction(alias = "_cube_")
        public static int cube(int x) {
            return (x * x) * x;
        }

        /**
         * Method which should not be registered.
         *
         * @return Nothing.
         */
        public static int no() {
            throw new IllegalStateException();
        }
    }

    /**
     *
     */
    private static class StoreFactory implements Factory<CacheStore> {
        @Override
        public CacheStore create() {
            return IgniteCacheAbstractQuerySelfTest.store;
        }
    }

    /**
     * Test enum class.
     */
    private static class EnumObject {
        /**
         * Test id.
         */
        @QuerySqlField(index = true)
        private long id;

        /**
         * Test enum.
         */
        @QuerySqlField
        private IgniteCacheAbstractQuerySelfTest.EnumType type;

        /**
         *
         *
         * @param id
         * 		id.
         * @param type
         * 		enum.
         */
        public EnumObject(long id, IgniteCacheAbstractQuerySelfTest.EnumType type) {
            this.id = id;
            this.type = type;
        }

        /**
         *
         *
         * @return string representation of object.
         */
        @Override
        public String toString() {
            return (((("EnumObject{" + "id=") + (id)) + ", type=") + (type)) + '}';
        }
    }

    /**
     * Test enum.
     */
    private enum EnumType {

        /**
         *
         */
        TYPE_A,
        /**
         *
         */
        TYPE_B;}
}


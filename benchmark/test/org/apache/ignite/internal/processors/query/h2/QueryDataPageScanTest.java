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
package org.apache.ignite.internal.processors.query.h2;


import CacheAtomicityMode.ATOMIC;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.tree.CacheDataTree;
import org.apache.ignite.internal.processors.query.GridQueryCancel;
import org.apache.ignite.internal.processors.query.GridQueryProcessor;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 *
 */
public class QueryDataPageScanTest extends GridCommonAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleIndexedTypes() throws Exception {
        final String cacheName = "test_multi_type";
        IgniteEx server = startGrid(0);
        server.cluster().active(true);
        CacheConfiguration<Object, Object> ccfg = new CacheConfiguration(cacheName);
        ccfg.setAffinity(new RendezvousAffinityFunction(false, 1));
        ccfg.setAtomicityMode(ATOMIC);
        ccfg.setIndexedTypes(Integer.class, Integer.class, Long.class, String.class, Long.class, QueryDataPageScanTest.TestData.class);
        ccfg.setQueryEntities(Arrays.asList(new QueryEntity().setValueType(UUID.class.getName()).setKeyType(Integer.class.getName()).setTableName("Uuids"), new QueryEntity().setValueType(QueryDataPageScanTest.Person.class.getName()).setKeyType(Integer.class.getName()).setTableName("My_Persons").setFields(QueryDataPageScanTest.Person.getFields())));
        IgniteCache<Object, Object> cache = server.createCache(ccfg);
        cache.put(1L, "bla-bla");
        cache.put(2L, new QueryDataPageScanTest.TestData(777L));
        cache.put(3, 3);
        cache.put(7, UUID.randomUUID());
        cache.put(9, new QueryDataPageScanTest.Person("Vasya", 99));
        CacheDataTree.isLastFindWithDataPageScan();
        List<List<?>> res = cache.query(new SqlFieldsQuery("select z, _key, _val from TestData use index()").setDataPageScanEnabled(true)).getAll();
        assertEquals(1, res.size());
        assertEquals(777L, res.get(0).get(0));
        assertTrue(CacheDataTree.isLastFindWithDataPageScan());
        res = cache.query(new SqlFieldsQuery("select _val, _key from String use index()").setDataPageScanEnabled(true)).getAll();
        assertEquals(1, res.size());
        assertEquals("bla-bla", res.get(0).get(0));
        assertTrue(CacheDataTree.isLastFindWithDataPageScan());
        res = cache.query(new SqlFieldsQuery("select _key, _val from Integer use index()").setDataPageScanEnabled(true)).getAll();
        assertEquals(1, res.size());
        assertEquals(3, res.get(0).get(0));
        assertTrue(CacheDataTree.isLastFindWithDataPageScan());
        res = cache.query(new SqlFieldsQuery("select _key, _val from uuids use index()").setDataPageScanEnabled(true)).getAll();
        assertEquals(1, res.size());
        assertEquals(7, res.get(0).get(0));
        assertTrue(CacheDataTree.isLastFindWithDataPageScan());
        res = cache.query(new SqlFieldsQuery("select age, name from my_persons use index()").setDataPageScanEnabled(true)).getAll();
        assertEquals(1, res.size());
        assertEquals(99, res.get(0).get(0));
        assertEquals("Vasya", res.get(0).get(1));
        assertTrue(CacheDataTree.isLastFindWithDataPageScan());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConcurrentUpdatesWithMvcc() throws Exception {
        doTestConcurrentUpdates(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConcurrentUpdatesNoMvcc() throws Exception {
        try {
            doTestConcurrentUpdates(false);
            throw new IllegalStateException("Expected to detect data inconsistency.");
        } catch (AssertionError e) {
            assertTrue(e.getMessage().startsWith("wrong sum!"));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDataPageScan() throws Exception {
        final String cacheName = "test";
        GridQueryProcessor.idxCls = QueryDataPageScanTest.DirectPageScanIndexing.class;
        IgniteEx server = startGrid(0);
        server.cluster().active(true);
        Ignition.setClientMode(true);
        IgniteEx client = startGrid(1);
        CacheConfiguration<Long, QueryDataPageScanTest.TestData> ccfg = new CacheConfiguration(cacheName);
        ccfg.setIndexedTypes(Long.class, QueryDataPageScanTest.TestData.class);
        ccfg.setSqlFunctionClasses(QueryDataPageScanTest.class);
        IgniteCache<Long, QueryDataPageScanTest.TestData> clientCache = client.createCache(ccfg);
        final int keysCnt = 1000;
        for (long i = 0; i < keysCnt; i++)
            clientCache.put(i, new QueryDataPageScanTest.TestData(i));

        IgniteCache<Long, QueryDataPageScanTest.TestData> serverCache = server.cache(cacheName);
        doTestScanQuery(clientCache, keysCnt);
        doTestScanQuery(serverCache, keysCnt);
        doTestSqlQuery(clientCache);
        doTestSqlQuery(serverCache);
        doTestDml(clientCache);
        doTestDml(serverCache);
        doTestLazySql(clientCache, keysCnt);
        doTestLazySql(serverCache, keysCnt);
    }

    /**
     *
     */
    static class DirectPageScanIndexing extends IgniteH2Indexing {
        /**
         *
         */
        static volatile Boolean expectedDataPageScanEnabled;

        /**
         *
         */
        static final AtomicInteger callsCnt = new AtomicInteger();

        /**
         * {@inheritDoc }
         */
        @Override
        public ResultSet executeSqlQueryWithTimer(PreparedStatement stmt, Connection conn, String sql, @Nullable
        Collection<Object> params, int timeoutMillis, @Nullable
        GridQueryCancel cancel, Boolean dataPageScanEnabled) throws IgniteCheckedException {
            QueryDataPageScanTest.DirectPageScanIndexing.callsCnt.incrementAndGet();
            assertEquals(QueryDataPageScanTest.DirectPageScanIndexing.expectedDataPageScanEnabled, dataPageScanEnabled);
            return super.executeSqlQueryWithTimer(stmt, conn, sql, params, timeoutMillis, cancel, dataPageScanEnabled);
        }
    }

    /**
     *
     */
    static class TestPredicate implements IgniteBiPredicate<Long, QueryDataPageScanTest.TestData> {
        /**
         *
         */
        static final AtomicInteger callsCnt = new AtomicInteger();

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean apply(Long k, QueryDataPageScanTest.TestData v) {
            QueryDataPageScanTest.TestPredicate.callsCnt.incrementAndGet();
            return false;
        }
    }

    /**
     *
     */
    static class TestData implements Serializable {
        /**
         *
         */
        static final long serialVersionUID = 42L;

        /**
         *
         */
        @QuerySqlField
        long z;

        /**
         *
         */
        TestData(long z) {
            this.z = z;
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

            QueryDataPageScanTest.TestData testData = ((QueryDataPageScanTest.TestData) (o));
            return (z) == (testData.z);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return ((int) ((z) ^ ((z) >>> 32)));
        }
    }

    /**
     * Externalizable class to make it non-binary.
     */
    static class Person implements Externalizable {
        String name;

        int age;

        public Person() {
            // No-op
        }

        Person(String name, int age) {
            this.name = Objects.requireNonNull(name);
            this.age = age;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(name);
            out.writeInt(age);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            name = in.readUTF();
            age = in.readInt();
        }

        static LinkedHashMap<String, String> getFields() {
            LinkedHashMap<String, String> m = new LinkedHashMap<>();
            m.put("age", "INT");
            m.put("name", "VARCHAR");
            return m;
        }
    }
}


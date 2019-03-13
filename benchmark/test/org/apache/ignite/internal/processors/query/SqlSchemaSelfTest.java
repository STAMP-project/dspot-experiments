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


import QueryUtils.DFLT_SCHEMA;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.cache.CacheException;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.processors.query.schema.SchemaOperationException;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Tests for schemas.
 */
public class SqlSchemaSelfTest extends AbstractIndexingCommonTest {
    /**
     * Person cache name.
     */
    private static final String CACHE_PERSON = "PersonCache";

    /**
     * Person cache 2 name.
     */
    private static final String CACHE_PERSON_2 = "PersonCache2";

    /**
     * Node.
     */
    private IgniteEx node;

    /**
     * Test query without caches.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryWithoutCacheOnPublicSchema() throws Exception {
        GridQueryProcessor qryProc = node.context().query();
        SqlFieldsQuery qry = new SqlFieldsQuery("SELECT 1").setSchema("PUBLIC");
        List<List<?>> res = qryProc.querySqlFields(qry, true).getAll();
        assertEquals(1, res.size());
        assertEquals(1, res.get(0).size());
        assertEquals(1, res.get(0).get(0));
        Iterator<List<?>> iter = qryProc.querySqlFields(qry, true).iterator();
        assertTrue(iter.hasNext());
        List<?> row = iter.next();
        assertEquals(1, row.size());
        assertEquals(1, row.get(0));
        assertFalse(iter.hasNext());
    }

    /**
     * Test query without caches.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryWithoutCacheOnCacheSchema() throws Exception {
        node.createCache(new org.apache.ignite.configuration.CacheConfiguration<SqlSchemaSelfTest.PersonKey, SqlSchemaSelfTest.Person>().setName(SqlSchemaSelfTest.CACHE_PERSON).setIndexedTypes(SqlSchemaSelfTest.PersonKey.class, SqlSchemaSelfTest.Person.class));
        GridQueryProcessor qryProc = node.context().query();
        SqlFieldsQuery qry = new SqlFieldsQuery("SELECT 1").setSchema(SqlSchemaSelfTest.CACHE_PERSON);
        List<List<?>> res = qryProc.querySqlFields(qry, true).getAll();
        assertEquals(1, res.size());
        assertEquals(1, res.get(0).size());
        assertEquals(1, res.get(0).get(0));
        Iterator<List<?>> iter = qryProc.querySqlFields(qry, true).iterator();
        assertTrue(iter.hasNext());
        List<?> row = iter.next();
        assertEquals(1, row.size());
        assertEquals(1, row.get(0));
        assertFalse(iter.hasNext());
    }

    /**
     * Test simple query.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSchemaChange() throws Exception {
        IgniteCache<SqlSchemaSelfTest.PersonKey, SqlSchemaSelfTest.Person> cache = node.createCache(new org.apache.ignite.configuration.CacheConfiguration<SqlSchemaSelfTest.PersonKey, SqlSchemaSelfTest.Person>().setName(SqlSchemaSelfTest.CACHE_PERSON).setIndexedTypes(SqlSchemaSelfTest.PersonKey.class, SqlSchemaSelfTest.Person.class));
        node.createCache(new org.apache.ignite.configuration.CacheConfiguration<SqlSchemaSelfTest.PersonKey, SqlSchemaSelfTest.Person>().setName(SqlSchemaSelfTest.CACHE_PERSON_2).setIndexedTypes(SqlSchemaSelfTest.PersonKey.class, SqlSchemaSelfTest.Person.class));
        cache.put(new SqlSchemaSelfTest.PersonKey(1), new SqlSchemaSelfTest.Person("Vasya", 2));
        // Normal calls.
        assertEquals(1, cache.query(new SqlFieldsQuery("SELECT id, name, orgId FROM Person")).getAll().size());
        assertEquals(1, cache.query(new SqlFieldsQuery("SELECT id, name, orgId FROM Person").setSchema(SqlSchemaSelfTest.CACHE_PERSON)).getAll().size());
        assertEquals(1, cache.query(new SqlFieldsQuery("SELECT id, name, orgId FROM \"PersonCache\".Person")).getAll().size());
        // Call from default schema.
        assertEquals(1, cache.query(new SqlFieldsQuery("SELECT id, name, orgId FROM \"PersonCache\".Person").setSchema(DFLT_SCHEMA)).getAll().size());
        // Call from another schema.
        assertEquals(1, cache.query(new SqlFieldsQuery("SELECT id, name, orgId FROM \"PersonCache\".Person").setSchema(SqlSchemaSelfTest.CACHE_PERSON_2)).getAll().size());
    }

    /**
     * Test simple query.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSchemaChangeOnCacheWithPublicSchema() throws Exception {
        IgniteCache<SqlSchemaSelfTest.PersonKey, SqlSchemaSelfTest.Person> cache = node.createCache(new org.apache.ignite.configuration.CacheConfiguration<SqlSchemaSelfTest.PersonKey, SqlSchemaSelfTest.Person>().setName(SqlSchemaSelfTest.CACHE_PERSON).setIndexedTypes(SqlSchemaSelfTest.PersonKey.class, SqlSchemaSelfTest.Person.class).setSqlSchema(DFLT_SCHEMA));
        node.createCache(new org.apache.ignite.configuration.CacheConfiguration<SqlSchemaSelfTest.PersonKey, SqlSchemaSelfTest.Person>().setName(SqlSchemaSelfTest.CACHE_PERSON_2).setIndexedTypes(SqlSchemaSelfTest.PersonKey.class, SqlSchemaSelfTest.Person.class));
        cache.put(new SqlSchemaSelfTest.PersonKey(1), new SqlSchemaSelfTest.Person("Vasya", 2));
        // Normal calls.
        assertEquals(1, cache.query(new SqlFieldsQuery("SELECT id, name, orgId FROM Person")).getAll().size());
        assertEquals(1, cache.query(new SqlFieldsQuery("SELECT id, name, orgId FROM Person").setSchema(DFLT_SCHEMA)).getAll().size());
        // Call from another schema.
        assertEquals(1, cache.query(new SqlFieldsQuery("SELECT id, name, orgId FROM public.Person").setSchema(SqlSchemaSelfTest.CACHE_PERSON_2)).getAll().size());
        assertEquals(1, cache.query(new SqlFieldsQuery("SELECT id, name, orgId FROM \"PUBLIC\".Person").setSchema(SqlSchemaSelfTest.CACHE_PERSON_2)).getAll().size());
    }

    /**
     * Test simple query.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomSchemaName() throws Exception {
        IgniteCache<Long, SqlSchemaSelfTest.Person> cache = registerQueryEntity("Person", SqlSchemaSelfTest.CACHE_PERSON);
        testQueryEntity(cache, "Person");
    }

    /**
     * Test multiple caches having the same schema.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomSchemaMultipleCaches() throws Exception {
        for (int i = 1; i <= 3; i++) {
            String tbl = "Person" + i;
            IgniteCache<Long, SqlSchemaSelfTest.Person> cache = registerQueryEntity(tbl, ("PersonCache" + i));
            testQueryEntity(cache, tbl);
        }
        for (int i = 1; i < 3; i++) {
            IgniteCache<Long, SqlSchemaSelfTest.Person> cache = node.cache(("PersonCache" + i));
            testQueryEntity(cache, ("Person" + i));
        }
    }

    /**
     * Test concurrent schema creation and destruction.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomSchemaConcurrentUse() throws Exception {
        final AtomicInteger maxIdx = new AtomicInteger();
        GridTestUtils.runMultiThreaded(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    int idx = maxIdx.incrementAndGet();
                    String tbl = "Person" + idx;
                    IgniteCache<Long, SqlSchemaSelfTest.Person> cache = registerQueryEntity(tbl, ("PersonCache" + idx));
                    testQueryEntity(cache, tbl);
                    cache.destroy();
                }
            }
        }, 4, "schema-test");
    }

    /**
     * Test type conflict in public schema.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTypeConflictInPublicSchema() throws Exception {
        node.createCache(new org.apache.ignite.configuration.CacheConfiguration<SqlSchemaSelfTest.PersonKey, SqlSchemaSelfTest.Person>().setName(SqlSchemaSelfTest.CACHE_PERSON).setIndexedTypes(SqlSchemaSelfTest.PersonKey.class, SqlSchemaSelfTest.Person.class).setSqlSchema(DFLT_SCHEMA));
        Throwable th = GridTestUtils.assertThrows(log, ((Callable<Void>) (() -> {
            node.createCache(new org.apache.ignite.configuration.CacheConfiguration<SqlSchemaSelfTest.PersonKey, SqlSchemaSelfTest.Person>().setName(SqlSchemaSelfTest.CACHE_PERSON_2).setIndexedTypes(SqlSchemaSelfTest.PersonKey.class, SqlSchemaSelfTest.Person.class).setSqlSchema(DFLT_SCHEMA));
            return null;
        })), CacheException.class, null);
        SchemaOperationException e = X.cause(th, SchemaOperationException.class);
        assertEquals(SchemaOperationException.CODE_TABLE_EXISTS, e.code());
    }

    /**
     * Person key.
     */
    public static class PersonKey {
        @QuerySqlField
        public long id;

        /**
         * Constructor.
         *
         * @param id
         * 		ID.
         */
        PersonKey(long id) {
            this.id = id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return ((int) (id));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            return ((obj != null) && (obj instanceof SqlSchemaSelfTest.PersonKey)) && (F.eq(id, ((SqlSchemaSelfTest.PersonKey) (obj)).id));
        }
    }

    /**
     * Person.
     */
    public static class Person {
        /**
         * Name.
         */
        @QuerySqlField
        public String name;

        /**
         * Organization ID.
         */
        @QuerySqlField(index = true)
        public long orgId;

        /**
         * Constructor.
         *
         * @param name
         * 		Name.
         * @param orgId
         * 		Orgainzation ID.
         */
        public Person(String name, long orgId) {
            this.name = name;
            this.orgId = orgId;
        }
    }
}


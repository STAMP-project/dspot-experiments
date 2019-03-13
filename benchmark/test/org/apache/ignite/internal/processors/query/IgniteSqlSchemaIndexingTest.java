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


import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 * Tests {@link IgniteH2Indexing} support {@link CacheConfiguration#setSqlSchema(String)} configuration.
 */
@SuppressWarnings("unchecked")
public class IgniteSqlSchemaIndexingTest extends AbstractIndexingCommonTest {
    /**
     * Tests unregistration of previous scheme.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCacheUnregistration() throws Exception {
        startGridsMultiThreaded(3, true);
        final CacheConfiguration<Integer, IgniteSqlSchemaIndexingTest.Fact> cfg = IgniteSqlSchemaIndexingTest.cacheConfig("Insensitive_Cache", true, Integer.class, IgniteSqlSchemaIndexingTest.Fact.class).setSqlSchema("Insensitive_Cache");
        final CacheConfiguration<Integer, IgniteSqlSchemaIndexingTest.Fact> collisionCfg = IgniteSqlSchemaIndexingTest.cacheConfig("InsensitiveCache", true, Integer.class, IgniteSqlSchemaIndexingTest.Fact.class).setSqlSchema("Insensitive_Cache");
        IgniteCache<Integer, IgniteSqlSchemaIndexingTest.Fact> cache = ignite(0).createCache(cfg);
        SqlFieldsQuery qry = new SqlFieldsQuery("select f.id, f.name from InSENSitive_Cache.Fact f");
        cache.put(1, new IgniteSqlSchemaIndexingTest.Fact(1, "cacheInsensitive"));
        for (List<?> row : cache.query(qry)) {
            assertEquals(2, row.size());
            assertEquals(1, row.get(0));
            assertEquals("cacheInsensitive", row.get(1));
        }
        ignite(0).destroyCache(cache.getName());
        cache = ignite(0).createCache(collisionCfg);// Previous collision should be removed by now.

        cache.put(1, new IgniteSqlSchemaIndexingTest.Fact(1, "cacheInsensitive"));
        cache.put(2, new IgniteSqlSchemaIndexingTest.Fact(2, "ThisIsANewCache"));
        cache.put(3, new IgniteSqlSchemaIndexingTest.Fact(3, "With3RecordsAndAnotherName"));
        assertEquals(3, cache.query(qry).getAll().size());
        ignite(0).destroyCache(cache.getName());
    }

    /**
     * Tests escapeAll and sqlSchema apposition.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSchemaEscapeAll() throws Exception {
        startGridsMultiThreaded(3, true);
        final CacheConfiguration<Integer, IgniteSqlSchemaIndexingTest.Fact> cfg = IgniteSqlSchemaIndexingTest.cacheConfig("simpleSchema", true, Integer.class, IgniteSqlSchemaIndexingTest.Fact.class).setSqlSchema("SchemaName1").setSqlEscapeAll(true);
        final CacheConfiguration<Integer, IgniteSqlSchemaIndexingTest.Fact> cfgEsc = IgniteSqlSchemaIndexingTest.cacheConfig("escapedSchema", true, Integer.class, IgniteSqlSchemaIndexingTest.Fact.class).setSqlSchema("\"SchemaName2\"").setSqlEscapeAll(true);
        IgniteSqlSchemaIndexingTest.escapeCheckSchemaName(ignite(0).createCache(cfg), log, cfg.getSqlSchema(), false, "Table \"FACT\" not found");
        IgniteSqlSchemaIndexingTest.escapeCheckSchemaName(ignite(0).createCache(cfgEsc), log, "SchemaName2", true, "Schema \"SCHEMANAME2\" not found");
        ignite(0).destroyCache(cfg.getName());
        ignite(0).destroyCache(cfgEsc.getName());
    }

    // TODO add tests with dynamic cache unregistration - IGNITE-1094 resolved
    /**
     * Test class as query entity
     */
    private static class Fact {
        /**
         * Primary key.
         */
        @QuerySqlField
        private int id;

        @QuerySqlField
        private String name;

        /**
         * Constructs a fact.
         *
         * @param id
         * 		fact ID.
         * @param name
         * 		fact name.
         */
        Fact(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * Gets fact ID.
         *
         * @return fact ID.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets fact name.
         *
         * @return Fact name.
         */
        public String getName() {
            return name;
        }
    }
}


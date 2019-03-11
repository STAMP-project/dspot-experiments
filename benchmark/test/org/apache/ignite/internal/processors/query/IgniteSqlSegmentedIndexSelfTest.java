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


import java.io.Serializable;
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.eviction.fifo.FifoEvictionPolicy;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 * Tests for correct distributed queries with index consisted of many segments.
 */
public class IgniteSqlSegmentedIndexSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final String ORG_CACHE_NAME = "org";

    /**
     *
     */
    private static final String PERSON_CAHE_NAME = "pers";

    /**
     *
     */
    private static final int ORG_CACHE_SIZE = 500;

    /**
     *
     */
    private static final int PERSON_CACHE_SIZE = 1000;

    /**
     *
     */
    private static final int ORPHAN_ROWS = 10;

    /**
     *
     */
    private static final int QRY_PARALLELISM_LVL = 97;

    /**
     * Test segmented index.
     */
    @Test
    public void testSegmentedIndex() {
        ignite(0).createCache(cacheConfig(IgniteSqlSegmentedIndexSelfTest.PERSON_CAHE_NAME, true, IgniteSqlSegmentedIndexSelfTest.PersonKey.class, IgniteSqlSegmentedIndexSelfTest.Person.class));
        ignite(0).createCache(cacheConfig(IgniteSqlSegmentedIndexSelfTest.ORG_CACHE_NAME, true, Integer.class, IgniteSqlSegmentedIndexSelfTest.Organization.class));
        fillCache();
        checkDistributedQueryWithSegmentedIndex();
        checkLocalQueryWithSegmentedIndex();
        checkLocalSizeQueryWithSegmentedIndex();
    }

    /**
     * Check correct index snapshots with segmented indices.
     */
    @Test
    public void testSegmentedIndexReproducableResults() {
        ignite(0).createCache(cacheConfig(IgniteSqlSegmentedIndexSelfTest.ORG_CACHE_NAME, true, Integer.class, IgniteSqlSegmentedIndexSelfTest.Organization.class));
        IgniteCache<Object, Object> cache = ignite(0).cache(IgniteSqlSegmentedIndexSelfTest.ORG_CACHE_NAME);
        // Unequal entries distribution among partitions.
        int expSize = (((nodesCount()) * (IgniteSqlSegmentedIndexSelfTest.QRY_PARALLELISM_LVL)) * 3) / 2;
        for (int i = 0; i < expSize; i++)
            cache.put(i, new IgniteSqlSegmentedIndexSelfTest.Organization(("org-" + i)));

        String select0 = "select * from \"org\".Organization o";
        // Check for stable results.
        for (int i = 0; i < 10; i++) {
            List<List<?>> res = cache.query(new SqlFieldsQuery(select0)).getAll();
            assertEquals(expSize, res.size());
        }
    }

    /**
     * Checks correct <code>select count(*)</code> result with segmented indices.
     */
    @Test
    public void testSegmentedIndexSizeReproducableResults() {
        ignite(0).createCache(cacheConfig(IgniteSqlSegmentedIndexSelfTest.ORG_CACHE_NAME, true, Integer.class, IgniteSqlSegmentedIndexSelfTest.Organization.class));
        IgniteCache<Object, Object> cache = ignite(0).cache(IgniteSqlSegmentedIndexSelfTest.ORG_CACHE_NAME);
        // Unequal entries distribution among partitions.
        long expSize = (((nodesCount()) * (IgniteSqlSegmentedIndexSelfTest.QRY_PARALLELISM_LVL)) * 3) / 2;
        for (int i = 0; i < expSize; i++)
            cache.put(i, new IgniteSqlSegmentedIndexSelfTest.Organization(("org-" + i)));

        String select0 = "select count(*) from \"org\".Organization o";
        // Check for stable results.
        for (int i = 0; i < 10; i++) {
            List<List<?>> res = cache.query(new SqlFieldsQuery(select0)).getAll();
            assertEquals(expSize, res.get(0).get(0));
        }
    }

    /**
     * Run tests on single-node grid.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testSegmentedIndexWithEvictionPolicy() {
        final IgniteCache<Object, Object> cache = ignite(0).createCache(cacheConfig(IgniteSqlSegmentedIndexSelfTest.ORG_CACHE_NAME, true, Integer.class, IgniteSqlSegmentedIndexSelfTest.Organization.class).setEvictionPolicy(new FifoEvictionPolicy(10)).setOnheapCacheEnabled(true));
        final long SIZE = 20;
        for (int i = 0; i < SIZE; i++)
            cache.put(i, new IgniteSqlSegmentedIndexSelfTest.Organization(("org-" + i)));

        String select0 = "select name from \"org\".Organization";
        List<List<?>> res = cache.query(new SqlFieldsQuery(select0)).getAll();
        assertEquals(SIZE, res.size());
    }

    /**
     * Verifies that <code>select count(*)</code> return valid result on a single-node grid.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testSizeOnSegmentedIndexWithEvictionPolicy() {
        final IgniteCache<Object, Object> cache = ignite(0).createCache(cacheConfig(IgniteSqlSegmentedIndexSelfTest.ORG_CACHE_NAME, true, Integer.class, IgniteSqlSegmentedIndexSelfTest.Organization.class).setEvictionPolicy(new FifoEvictionPolicy(10)).setOnheapCacheEnabled(true));
        final long SIZE = 20;
        for (int i = 0; i < SIZE; i++)
            cache.put(i, new IgniteSqlSegmentedIndexSelfTest.Organization(("org-" + i)));

        String select0 = "select count(*) from \"org\".Organization";
        List<List<?>> res = cache.query(new SqlFieldsQuery(select0)).getAll();
        assertEquals(SIZE, res.get(0).get(0));
    }

    /**
     * Run tests on multi-node grid
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSegmentedPartitionedWithReplicated() throws Exception {
        ignite(0).createCache(cacheConfig(IgniteSqlSegmentedIndexSelfTest.PERSON_CAHE_NAME, true, IgniteSqlSegmentedIndexSelfTest.PersonKey.class, IgniteSqlSegmentedIndexSelfTest.Person.class));
        ignite(0).createCache(cacheConfig(IgniteSqlSegmentedIndexSelfTest.ORG_CACHE_NAME, false, Integer.class, IgniteSqlSegmentedIndexSelfTest.Organization.class));
        fillCache();
        checkDistributedQueryWithSegmentedIndex();
        checkLocalQueryWithSegmentedIndex();
        checkLocalSizeQueryWithSegmentedIndex();
    }

    private static class PersonKey {
        @QuerySqlField
        int id;

        /**
         *
         */
        @AffinityKeyMapped
        @QuerySqlField
        Integer orgId;

        public PersonKey(int id, Integer orgId) {
            this.id = id;
            this.orgId = orgId;
        }
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public Person(String name) {
            this.name = name;
        }
    }

    /**
     *
     */
    private static class Organization implements Serializable {
        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        public Organization() {
            // No-op.
        }

        /**
         *
         *
         * @param name
         * 		Organization name.
         */
        public Organization(String name) {
            this.name = name;
        }
    }
}


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
package org.apache.ignite.internal.processors.database;


import QueryUtils.DFLT_SCHEMA;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.query.QueryUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgnitePersistentStoreSchemaLoadTest extends GridCommonAbstractTest {
    /**
     * Cache name.
     */
    private static final String TMPL_NAME = "test_cache*";

    /**
     * Table name.
     */
    private static final String TBL_NAME = IgnitePersistentStoreSchemaLoadTest.Person.class.getSimpleName();

    /**
     * Name of the cache created with {@code CREATE TABLE}.
     */
    private static final String SQL_CACHE_NAME = QueryUtils.createTableCacheName(DFLT_SCHEMA, IgnitePersistentStoreSchemaLoadTest.TBL_NAME);

    /**
     * Name of the cache created upon cluster start.
     */
    private static final String STATIC_CACHE_NAME = IgnitePersistentStoreSchemaLoadTest.TBL_NAME;

    /**
     *
     */
    @Test
    public void testDynamicSchemaChangesPersistence() throws Exception {
        checkSchemaStateAfterNodeRestart(false);
    }

    /**
     *
     */
    @Test
    public void testDynamicSchemaChangesPersistenceWithAliveCluster() throws Exception {
        checkSchemaStateAfterNodeRestart(true);
    }

    /**
     *
     */
    @Test
    public void testDynamicSchemaChangesPersistenceWithStaticCache() throws Exception {
        IgniteEx node = startGrid(getConfigurationWithStaticCache(getTestIgniteInstanceName(0)));
        node.active(true);
        IgniteCache cache = node.cache(IgnitePersistentStoreSchemaLoadTest.STATIC_CACHE_NAME);
        assertNotNull(cache);
        CountDownLatch cnt = checkpointLatch(node);
        assertEquals(0, indexCnt(node, IgnitePersistentStoreSchemaLoadTest.STATIC_CACHE_NAME));
        makeDynamicSchemaChanges(node, IgnitePersistentStoreSchemaLoadTest.STATIC_CACHE_NAME);
        checkDynamicSchemaChanges(node, IgnitePersistentStoreSchemaLoadTest.STATIC_CACHE_NAME);
        cnt.await();
        stopGrid(0);
        // Restarting with no-cache configuration - otherwise stored configurations
        // will be ignored due to cache names duplication.
        node = startGrid(0);
        node.active(true);
        checkDynamicSchemaChanges(node, IgnitePersistentStoreSchemaLoadTest.STATIC_CACHE_NAME);
    }

    /**
     *
     */
    protected static class Person implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         *
         */
        @SuppressWarnings("unused")
        private Person() {
            // No-op.
        }

        /**
         *
         */
        public Person(int id) {
            this.id = id;
        }

        /**
         *
         */
        @QuerySqlField
        protected int id;

        /**
         *
         */
        @QuerySqlField
        protected String name;

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            IgnitePersistentStoreSchemaLoadTest.Person person = ((IgnitePersistentStoreSchemaLoadTest.Person) (o));
            return ((id) == (person.id)) && ((name) != null ? name.equals(person.name) : (person.name) == null);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            int res = id;
            res = (31 * res) + ((name) != null ? name.hashCode() : 0);
            return res;
        }
    }
}


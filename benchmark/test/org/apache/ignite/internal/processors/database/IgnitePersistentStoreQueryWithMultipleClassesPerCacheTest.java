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


import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgnitePersistentStoreQueryWithMultipleClassesPerCacheTest extends GridCommonAbstractTest {
    /**
     * Cache name.
     */
    private static final String CACHE_NAME = "test_cache";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimple() throws Exception {
        IgniteEx ig0 = startGrid(0);
        ig0.active(true);
        ig0.cache(IgnitePersistentStoreQueryWithMultipleClassesPerCacheTest.CACHE_NAME).put(0, new IgnitePersistentStoreQueryWithMultipleClassesPerCacheTest.Person(0));
        ig0.cache(IgnitePersistentStoreQueryWithMultipleClassesPerCacheTest.CACHE_NAME).put(1, new IgnitePersistentStoreQueryWithMultipleClassesPerCacheTest.Country());
        List<List<?>> all = ig0.cache(IgnitePersistentStoreQueryWithMultipleClassesPerCacheTest.CACHE_NAME).query(new SqlFieldsQuery((("select depId FROM \"" + (IgnitePersistentStoreQueryWithMultipleClassesPerCacheTest.CACHE_NAME)) + "\".Person"))).getAll();
        assert (all.size()) == 1;
    }

    /**
     *
     */
    public static class Person implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         *
         */
        @QuerySqlField
        protected int depId;

        /**
         *
         */
        @QuerySqlField
        protected String name;

        /**
         *
         */
        @QuerySqlField
        protected UUID id = UUID.randomUUID();

        /**
         *
         */
        @SuppressWarnings("unused")
        private Person() {
            // No-op.
        }

        /**
         *
         *
         * @param depId
         * 		Department ID.
         */
        private Person(int depId) {
            this.depId = depId;
            name = (("Name-" + (id)) + " ") + depId;
        }
    }

    /**
     *
     */
    public static class Country {
        /**
         *
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         *
         */
        @QuerySqlField
        private String name;

        /**
         *
         */
        @QuerySqlField
        private UUID uuid = UUID.randomUUID();
    }
}


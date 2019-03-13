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


import java.io.Serializable;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.query.h2.sql.AbstractH2CompareQueryTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheUnionDuplicatesTest extends AbstractH2CompareQueryTest {
    /**
     *
     */
    private static IgniteCache<Integer, IgniteCacheUnionDuplicatesTest.Organization> pCache;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUnionDuplicateFilter() throws Exception {
        compareQueryRes0(IgniteCacheUnionDuplicatesTest.pCache, ("select name from \"part\".Organization " + ("union " + "select name2 from \"part\".Organization")));
    }

    /**
     * Organization class. Stored at partitioned cache.
     */
    private static class Organization implements Serializable {
        /**
         *
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         *
         */
        @QuerySqlField(index = true)
        private String name;

        /**
         *
         */
        @QuerySqlField(index = true)
        private String name2;

        /**
         * Create Organization.
         *
         * @param id
         * 		Organization ID.
         * @param name
         * 		Organization name.
         * @param name2
         * 		Name2.
         */
        Organization(int id, String name, String name2) {
            this.id = id;
            this.name = name;
            this.name2 = name2;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof IgniteCacheUnionDuplicatesTest.Organization) && ((id) == (((IgniteCacheUnionDuplicatesTest.Organization) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return ((((("Organization [id=" + (id)) + ", name=") + (name)) + ", name2=") + (name2)) + ']';
        }
    }
}


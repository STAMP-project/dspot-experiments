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
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 * Tests for correct distributed sql joins.
 */
public class IgniteSqlDistributedJoinSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final int NODES_COUNT = 2;

    /**
     *
     */
    private static final int ORG_COUNT = IgniteSqlDistributedJoinSelfTest.NODES_COUNT;

    /**
     *
     */
    private static final int PERSON_PER_ORG_COUNT = 50;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNonCollocatedDistributedJoin() throws Exception {
        CacheConfiguration ccfg1 = cacheConfig("pers", true, String.class, IgniteSqlDistributedJoinSelfTest.Person.class);
        CacheConfiguration ccfg2 = cacheConfig("org", true, String.class, IgniteSqlDistributedJoinSelfTest.Organization.class);
        IgniteCache<String, IgniteSqlDistributedJoinSelfTest.Person> c1 = ignite(0).getOrCreateCache(ccfg1);
        IgniteCache<String, IgniteSqlDistributedJoinSelfTest.Organization> c2 = ignite(0).getOrCreateCache(ccfg2);
        try {
            awaitPartitionMapExchange();
            populateDataIntoCaches(c1, c2);
            String joinSql = "select * from Person, \"org\".Organization as org " + ("where Person.orgId = org.id " + "and lower(org.name) = lower(?)");
            SqlQuery qry = new SqlQuery<String, IgniteSqlDistributedJoinSelfTest.Person>(IgniteSqlDistributedJoinSelfTest.Person.class, joinSql).setArgs("Organization #0");
            qry.setDistributedJoins(true);
            List<IgniteSqlDistributedJoinSelfTest.Person> prns = c1.query(qry).getAll();
            assertEquals(IgniteSqlDistributedJoinSelfTest.PERSON_PER_ORG_COUNT, prns.size());
        } finally {
            c1.destroy();
            c2.destroy();
        }
    }

    /**
     *
     */
    private static class Person {
        /**
         *
         */
        @QuerySqlField(index = true)
        private String id;

        /**
         *
         */
        @QuerySqlField(index = true)
        private String orgId;

        /**
         *
         */
        @QuerySqlField(index = true)
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrgId() {
            return orgId;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     *
     */
    private static class Organization {
        /**
         *
         */
        @QuerySqlField(index = true)
        private String id;

        /**
         *
         */
        @QuerySqlField(index = true)
        private String name;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}


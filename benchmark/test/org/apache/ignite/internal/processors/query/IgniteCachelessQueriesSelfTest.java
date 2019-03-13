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
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for behavior in various cases of local and distributed queries.
 */
public class IgniteCachelessQueriesSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String SELECT = "select count(*) from \"pers\".Person p, \"org\".Organization o where p.orgId = o._key";

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
    @Test
    public void testDistributedQueryOnPartitionedCaches() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, false, false);
        assertDistributedQuery();
    }

    /**
     *
     */
    @Test
    public void testDistributedQueryOnPartitionedAndReplicatedCache() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, false, false);
        assertDistributedQuery();
    }

    /**
     *
     */
    @Test
    public void testDistributedQueryOnReplicatedCaches() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, false, false);
        assertLocalQuery();
    }

    /**
     *
     */
    @Test
    public void testDistributedQueryOnSegmentedCaches() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, false, false);
        assertDistributedQuery();
    }

    /**
     *
     */
    @Test
    public void testDistributedQueryOnReplicatedAndSegmentedCache() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, false, false);
        assertDistributedQuery();
    }

    /**
     *
     */
    @Test
    public void testDistributedQueryOnPartitionedCachesWithReplicatedFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, true, false);
        assertDistributedQuery();
    }

    /**
     *
     */
    @Test
    public void testDistributedQueryOnPartitionedAndReplicatedCacheWithReplicatedFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, true, false);
        assertDistributedQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalQueryOnReplicatedCachesWithReplicatedFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, true, false);
        assertLocalQuery();
    }

    /**
     *
     */
    @Test
    public void testDistributedQueryOnSegmentedCachesWithReplicatedFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, true, false);
        assertDistributedQuery();
    }

    /**
     *
     */
    @Test
    public void testDistributedQueryOnReplicatedAndSegmentedCacheWithReplicatedFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, true, false);
        assertDistributedQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalQueryOnPartitionedCachesWithLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, false, true);
        assertLocalQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalQueryOnPartitionedAndReplicatedCacheWithLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, false, true);
        assertLocalQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalQueryOnReplicatedCachesWithLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, false, true);
        assertLocalQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalTwoStepQueryOnSegmentedCachesWithLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, false, true);
        assertLocalTwoStepQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalTwoStepQueryOnReplicatedAndSegmentedCacheWithLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, false, true);
        assertLocalTwoStepQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalQueryOnPartitionedCachesWithReplicatedAndLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, false, true);
        assertLocalQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalQueryOnPartitionedAndReplicatedCacheWithReplicatedAndLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.PARTITIONED, IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, true, true);
        assertLocalQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalQueryOnReplicatedCachesWithReplicatedAndLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, true, true);
        assertLocalQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalTwoStepQueryOnSegmentedCachesWithReplicatedAndLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, true, true);
        assertLocalTwoStepQuery();
    }

    /**
     *
     */
    @Test
    public void testLocalTwoStepQueryOnReplicatedAndSegmentedCacheWithReplicatedAndLocalFlag() {
        createCachesAndExecuteQuery(IgniteCachelessQueriesSelfTest.TestCacheMode.REPLICATED, IgniteCachelessQueriesSelfTest.TestCacheMode.SEGMENTED, true, true);
        assertLocalTwoStepQuery();
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        @QuerySqlField(index = true)
        Integer orgId;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        public Person() {
            // No-op.
        }

        /**
         *
         *
         * @param orgId
         * 		Organization ID.
         * @param name
         * 		Name.
         */
        public Person(int orgId, String name) {
            this.orgId = orgId;
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

    /**
     * Mode for test cache.
     */
    private enum TestCacheMode {

        /**
         *
         */
        SEGMENTED,
        /**
         *
         */
        PARTITIONED,
        /**
         *
         */
        REPLICATED;}
}


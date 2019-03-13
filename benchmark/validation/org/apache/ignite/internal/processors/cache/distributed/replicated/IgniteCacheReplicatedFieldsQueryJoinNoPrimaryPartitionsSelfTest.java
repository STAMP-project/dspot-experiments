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
package org.apache.ignite.internal.processors.cache.distributed.replicated;


import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests non-collocated join with REPLICATED cache and no primary partitions for that cache on some nodes.
 */
@SuppressWarnings("unused")
public class IgniteCacheReplicatedFieldsQueryJoinNoPrimaryPartitionsSelfTest extends GridCommonAbstractTest {
    /**
     * Client node name.
     */
    public static final String NODE_CLI = "client";

    /**
     *
     */
    public static final String CACHE_PARTITIONED = "partitioned";

    /**
     *
     */
    public static final String CACHE_REPLICATED = "replicated";

    /**
     *
     */
    public static final int REP_CNT = 3;

    /**
     *
     */
    public static final int PART_CNT = 100;

    /**
     * Test non-colocated join.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoinNonCollocated() throws Exception {
        SqlFieldsQuery qry = new SqlFieldsQuery("SELECT COUNT(*) FROM PartValue p, RepValue r WHERE p.repId=r.id");
        long cnt = ((Long) (grid(IgniteCacheReplicatedFieldsQueryJoinNoPrimaryPartitionsSelfTest.NODE_CLI).cache(IgniteCacheReplicatedFieldsQueryJoinNoPrimaryPartitionsSelfTest.CACHE_PARTITIONED).query(qry).getAll().get(0).get(0)));
        assertEquals(IgniteCacheReplicatedFieldsQueryJoinNoPrimaryPartitionsSelfTest.PART_CNT, cnt);
    }

    /**
     * Value for PARTITIONED cache.
     */
    public static class PartValue {
        /**
         * Id.
         */
        @QuerySqlField
        private int id;

        /**
         * Rep id.
         */
        @QuerySqlField
        private int repId;

        /**
         *
         *
         * @param id
         * 		Id.
         * @param repId
         * 		Rep id.
         */
        public PartValue(int id, int repId) {
            this.id = id;
            this.repId = repId;
        }
    }

    /**
     * Value for REPLICATED cache.
     */
    public static class RepValue {
        /**
         * Id.
         */
        @QuerySqlField
        private int id;

        /**
         *
         *
         * @param id
         * 		Id.
         */
        public RepValue(int id) {
            this.id = id;
        }
    }
}


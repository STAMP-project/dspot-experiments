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


import Cache.Entry;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Tests cache in-place modification logic with iterative value increment.
 */
@Ignore("https://issues.apache.org/jira/browse/IGNITE-2229")
public class IgniteCacheFullTextQueryNodeJoiningSelfTest extends GridCommonAbstractTest {
    /**
     * Number of nodes to test on.
     */
    private static final int GRID_CNT = 3;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFullTextQueryNodeJoin() throws Exception {
        for (int r = 0; r < 5; r++) {
            startGrids(IgniteCacheFullTextQueryNodeJoiningSelfTest.GRID_CNT);
            try {
                for (int i = 0; i < 1000; i++) {
                    IgniteCacheFullTextQueryNodeJoiningSelfTest.IndexedEntity entity = new IgniteCacheFullTextQueryNodeJoiningSelfTest.IndexedEntity(("indexed " + i));
                    grid(0).cache(DEFAULT_CACHE_NAME).put(new AffinityKey(i, i), entity);
                }
                Ignite started = startGrid(IgniteCacheFullTextQueryNodeJoiningSelfTest.GRID_CNT);
                for (int i = 0; i < 100; i++) {
                    QueryCursor<Entry<AffinityKey<Integer>, IgniteCacheFullTextQueryNodeJoiningSelfTest.IndexedEntity>> res = started.cache(DEFAULT_CACHE_NAME).query(new org.apache.ignite.cache.query.TextQuery<AffinityKey<Integer>, IgniteCacheFullTextQueryNodeJoiningSelfTest.IndexedEntity>(IgniteCacheFullTextQueryNodeJoiningSelfTest.IndexedEntity.class, "indexed"));
                    assertEquals(("Failed iteration: " + i), 1000, res.getAll().size());
                }
            } finally {
                stopAllGrids();
            }
        }
    }

    /**
     *
     */
    private static class IndexedEntity {
        /**
         *
         */
        private String val;

        /**
         *
         *
         * @param val
         * 		Value.
         */
        private IndexedEntity(String val) {
            this.val = val;
        }
    }
}


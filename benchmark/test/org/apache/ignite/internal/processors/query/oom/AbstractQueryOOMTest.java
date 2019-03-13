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
package org.apache.ignite.internal.processors.query.oom;


import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for OOME on query.
 */
@RunWith(JUnit4.class)
public abstract class AbstractQueryOOMTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final long KEY_CNT = 2000000L;

    /**
     *
     */
    private static final String CACHE_NAME = "test_cache";

    /**
     *
     */
    private static final String HAS_CACHE = "HAS_CACHE";

    /**
     *
     */
    private static final int RMT_NODES_CNT = 3;

    /**
     *
     */
    private static final long HANG_TIMEOUT = (15 * 60) * 1000;

    /**
     *
     *
     * @throws Exception
     * 		On error.
     */
    @Test
    public void testHeavyScanLazy() throws Exception {
        startTestGrid();
        checkQuery("SELECT * from test", AbstractQueryOOMTest.KEY_CNT, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		On error.
     */
    @Test
    public void testHeavyGroupByPkLazy() throws Exception {
        startTestGrid();
        checkQuery("SELECT id, sum(val) from test GROUP BY id", AbstractQueryOOMTest.KEY_CNT, true, true);
    }

    /**
     *
     */
    public static class Value {
        /**
         * Secondary ID.
         */
        @QuerySqlField(index = true)
        private long indexed;

        /**
         * Secondary ID.
         */
        @QuerySqlField
        private long val;

        /**
         * String value.
         */
        @QuerySqlField
        private String str;

        /**
         *
         *
         * @param id
         * 		ID.
         */
        public Value(long id) {
            indexed = id / 10;
            val = id;
            str = "value " + id;
        }
    }

    /**
     *
     */
    public static class TestNodeFilter implements IgnitePredicate<ClusterNode> {
        /**
         * {@inheritDoc }
         */
        @Override
        public boolean apply(ClusterNode node) {
            return (node.attribute(AbstractQueryOOMTest.HAS_CACHE)) != null;
        }
    }
}


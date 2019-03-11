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


import CacheAtomicityMode.ATOMIC;
import CacheAtomicityMode.TRANSACTIONAL;
import CacheMode.PARTITIONED;
import CacheMode.REPLICATED;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests that server nodes do not need class definitions to execute queries.
 */
public class IgniteBinaryObjectFieldsQuerySelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    public static final String PERSON_KEY_CLS_NAME = "org.apache.ignite.tests.p2p.cache.PersonKey";

    /**
     * Grid count.
     */
    public static final int GRID_CNT = 4;

    /**
     *
     */
    private static ClassLoader extClassLoader;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryPartitionedAtomic() throws Exception {
        checkQuery(PARTITIONED, ATOMIC);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryReplicatedAtomic() throws Exception {
        checkQuery(REPLICATED, ATOMIC);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryPartitionedTransactional() throws Exception {
        checkQuery(PARTITIONED, TRANSACTIONAL);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryReplicatedTransactional() throws Exception {
        checkQuery(REPLICATED, TRANSACTIONAL);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFieldsQueryPartitionedAtomic() throws Exception {
        checkFieldsQuery(PARTITIONED, ATOMIC);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFieldsQueryReplicatedAtomic() throws Exception {
        checkFieldsQuery(REPLICATED, ATOMIC);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFieldsQueryPartitionedTransactional() throws Exception {
        checkFieldsQuery(PARTITIONED, TRANSACTIONAL);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFieldsQueryReplicatedTransactional() throws Exception {
        checkFieldsQuery(REPLICATED, TRANSACTIONAL);
    }

    /**
     *
     */
    private static class PersonKeyFilter implements IgniteBiPredicate<BinaryObject, BinaryObject> {
        /**
         * Max ID allowed.
         */
        private int maxId;

        /**
         *
         *
         * @param maxId
         * 		Max ID allowed.
         */
        public PersonKeyFilter(int maxId) {
            this.maxId = maxId;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean apply(BinaryObject key, BinaryObject val) {
            return (key.<Integer>field("id")) <= (maxId);
        }
    }
}


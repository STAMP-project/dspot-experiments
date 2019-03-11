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
package org.apache.ignite.internal.processors.query.h2.sql;


import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test type inference for SQL parameters.
 */
public class ParameterTypeInferenceTest extends GridCommonAbstractTest {
    /**
     * IP finder.
     */
    private static final TcpDiscoveryVmIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /**
     * Cache.
     */
    private static final String CACHE_NAME = "cache";

    /**
     * Number of nodes.
     */
    private static final int NODE_CNT = 2;

    /**
     * Test type inference for local query.
     */
    @Test
    public void testInferenceLocal() {
        check("SELECT ? FROM cache", true);
        check("SELECT ? FROM cache ORDER BY val", true);
    }

    /**
     * Test type inference for query without reducer.
     */
    @Test
    public void testInferenceNoReduce() {
        check("SELECT ? FROM cache", false);
    }

    /**
     * Test type inference for query with reducer.
     */
    @Test
    public void testInferenceReduce() {
        check("SELECT ? FROM cache ORDER BY val", false);
    }

    /**
     * Key class.
     */
    @SuppressWarnings("unused")
    private static class InferenceKey {
        /**
         * Key.
         */
        @QuerySqlField
        private int key;

        /**
         *
         *
         * @param key
         * 		Key.
         */
        private InferenceKey(int key) {
            this.key = key;
        }
    }

    /**
     * Value class.
     */
    @SuppressWarnings("unused")
    private static class InferenceValue {
        /**
         * Value.
         */
        @QuerySqlField
        private int val;

        /**
         *
         *
         * @param val
         * 		Value.
         */
        private InferenceValue(int val) {
            this.val = val;
        }
    }
}


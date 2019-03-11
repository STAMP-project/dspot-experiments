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
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class CacheRandomOperationsMultithreadedTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int KEYS = 1000;

    /**
     *
     */
    private static final int NODES = 4;

    /**
     *
     */
    private boolean client;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAtomicOffheapEviction() throws Exception {
        CacheConfiguration<Object, Object> ccfg = cacheConfiguration(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, new org.apache.ignite.cache.eviction.lru.LruEvictionPolicy(10), false);
        randomOperations(ccfg);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAtomicOffheapEvictionIndexing() throws Exception {
        CacheConfiguration<Object, Object> ccfg = cacheConfiguration(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, new org.apache.ignite.cache.eviction.lru.LruEvictionPolicy(10), true);
        randomOperations(ccfg);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTxOffheapEviction() throws Exception {
        CacheConfiguration<Object, Object> ccfg = cacheConfiguration(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, new org.apache.ignite.cache.eviction.lru.LruEvictionPolicy(10), false);
        randomOperations(ccfg);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTxOffheapEvictionIndexing() throws Exception {
        CacheConfiguration<Object, Object> ccfg = cacheConfiguration(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, new org.apache.ignite.cache.eviction.lru.LruEvictionPolicy(10), true);
        randomOperations(ccfg);
    }

    /**
     *
     */
    static class TestFilter implements IgniteBiPredicate<Object, Object> {
        /**
         * {@inheritDoc }
         */
        @Override
        public boolean apply(Object key, Object val) {
            return (ThreadLocalRandom.current().nextInt(10)) == 0;
        }
    }

    /**
     *
     */
    static class TestKey implements Serializable , Comparable<CacheRandomOperationsMultithreadedTest.TestKey> {
        /**
         *
         */
        private int key;

        /**
         *
         */
        private byte[] byteVal;

        /**
         * {@inheritDoc }
         */
        @Override
        public int compareTo(CacheRandomOperationsMultithreadedTest.TestKey o) {
            return Integer.compare(key, o.key);
        }

        /**
         *
         *
         * @param key
         * 		Key.
         * @param rnd
         * 		Random generator.
         */
        public TestKey(int key, ThreadLocalRandom rnd) {
            this.key = key;
            byteVal = new byte[rnd.nextInt(100)];
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            CacheRandomOperationsMultithreadedTest.TestKey testKey = ((CacheRandomOperationsMultithreadedTest.TestKey) (o));
            return (key) == (testKey.key);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return key;
        }
    }

    /**
     *
     */
    static class TestData implements Serializable {
        /**
         *
         */
        @QuerySqlField(index = true)
        private int val1;

        /**
         *
         */
        private long val2;

        /**
         *
         */
        @QuerySqlField(index = true)
        private String val3;

        /**
         *
         */
        private byte[] val4;

        /**
         *
         *
         * @param rnd
         * 		Random generator.
         */
        public TestData(ThreadLocalRandom rnd) {
            val1 = rnd.nextInt();
            val2 = val1;
            val3 = String.valueOf(val1);
            val4 = new byte[rnd.nextInt(1024)];
        }
    }
}


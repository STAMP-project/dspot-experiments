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


import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
@SuppressWarnings("unchecked")
public class CacheBinaryKeyConcurrentQueryTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int NODES = 3;

    /**
     *
     */
    private static final int KEYS = 1000;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutAndQueries() throws Exception {
        Ignite ignite = ignite(0);
        IgniteCache cache1 = ignite.createCache(cacheConfiguration("cache1", CacheAtomicityMode.ATOMIC));
        IgniteCache cache2 = ignite.createCache(cacheConfiguration("cache2", CacheAtomicityMode.TRANSACTIONAL));
        insertData(ignite, cache1.getName());
        insertData(ignite, cache2.getName());
        IgniteInternalFuture<?> fut1 = startUpdate(cache1.getName());
        IgniteInternalFuture<?> fut2 = startUpdate(cache2.getName());
        fut1.get();
        fut2.get();
    }

    /**
     *
     */
    static class TestKey {
        /**
         *
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         *
         *
         * @param id
         * 		ID.
         */
        public TestKey(int id) {
            this.id = id;
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

            CacheBinaryKeyConcurrentQueryTest.TestKey testKey = ((CacheBinaryKeyConcurrentQueryTest.TestKey) (o));
            return (id) == (testKey.id);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     *
     */
    static class TestValue {
        /**
         *
         */
        @QuerySqlField(index = true)
        private int val;

        /**
         *
         *
         * @param val
         * 		Value.
         */
        public TestValue(int val) {
            this.val = val;
        }
    }
}


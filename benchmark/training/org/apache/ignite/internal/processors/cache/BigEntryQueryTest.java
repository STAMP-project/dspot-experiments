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


import CacheMode.PARTITIONED;
import CacheWriteSynchronizationMode.FULL_SYNC;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * This is a specific test for IGNITE-8900.
 */
public class BigEntryQueryTest extends GridCommonAbstractTest {
    /**
     *
     */
    public static final String CACHE = "cache";

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testBigEntriesSelect() throws Exception {
        startGrids(2);
        Random random = new Random(1);
        Ignition.setClientMode(true);
        Ignite client = startGrid(2);
        int ctr = 0;
        long time0 = System.currentTimeMillis();
        while (((System.currentTimeMillis()) - time0) < 30000) {
            String cacheName = (BigEntryQueryTest.CACHE) + (ctr++);
            IgniteCache<Long, BigEntryQueryTest.Value> cache = client.getOrCreateCache(new org.apache.ignite.configuration.CacheConfiguration<Long, BigEntryQueryTest.Value>(cacheName).setCacheMode(PARTITIONED).setBackups(1).setWriteSynchronizationMode(FULL_SYNC).setIndexedTypes(Long.class, BigEntryQueryTest.Value.class));
            cache.putAll(LongStream.range(610026643276160000L, 610026643276170000L).boxed().collect(Collectors.toMap(Function.identity(), ( t) -> org.apache.ignite.internal.processors.cache.Value.of(new byte[(random.nextInt(16)) * 1000]))));
            for (int i = 0; i < 10; i++) {
                long start = 610026643276160000L;
                long end = start + (random.nextInt(10));
                int expectedResultCount = ((int) ((end - start) + 1));
                String sql = String.format(("SELECT _KEY " + ("FROM %s " + "WHERE _KEY >= %d AND _KEY <= %d")), BigEntryQueryTest.Value.class.getSimpleName().toLowerCase(), start, end);
                Set<Long> keySet = new HashSet<>();
                for (long l = start; l < (end + 1); l++)
                    keySet.add(l);

                List<Long> resultKeys;
                try (FieldsQueryCursor<List<?>> results = cache.query(new SqlFieldsQuery(sql))) {
                    resultKeys = new ArrayList<>();
                    results.forEach(( objects) -> resultKeys.add(((Long) (objects.get(0)))));
                    Collections.sort(resultKeys);
                }
                assertEquals(expectedResultCount, resultKeys.size());
            }
            cache.destroy();
        } 
    }

    /**
     * Class containing value to be placed into the cache.
     */
    public static class Value implements Serializable {
        /**
         *
         */
        final byte[] data;

        /**
         *
         *
         * @param data
         * 		Data.
         */
        public Value(final byte[] data) {
            this.data = data;
        }

        /**
         *
         *
         * @param bytes
         * 		Bytes.
         * @return Value.
         */
        public static BigEntryQueryTest.Value of(final byte[] bytes) {
            return new BigEntryQueryTest.Value(bytes);
        }
    }
}


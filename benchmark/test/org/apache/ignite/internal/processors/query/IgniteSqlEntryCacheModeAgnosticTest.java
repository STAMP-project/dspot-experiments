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
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.util.typedef.F;
import org.junit.Test;


/**
 * Test different cache modes for query entry
 */
public class IgniteSqlEntryCacheModeAgnosticTest extends AbstractIndexingCommonTest {
    /**
     * Host.
     */
    public static final String HOST = "127.0.0.1";

    /**
     * Partitioned cache name.
     */
    private static final String PARTITIONED_CACHE_NAME = "PART_CACHE";

    /**
     * Replicated cache name.
     */
    private static final String REPLICATED_CACHE_NAME = "REPL_CACHE";

    /**
     * Local cache name.
     */
    private static final String LOCAL_CACHE_NAME = "LOCAL_CACHE";

    /**
     * It should not matter what cache mode does entry cache use, if there is no join
     */
    @Test
    public void testCrossCacheModeQuery() throws Exception {
        Ignite ignite = startGrid();
        ignite.cache(IgniteSqlEntryCacheModeAgnosticTest.LOCAL_CACHE_NAME).put(1, IgniteSqlEntryCacheModeAgnosticTest.LOCAL_CACHE_NAME);
        ignite.cache(IgniteSqlEntryCacheModeAgnosticTest.REPLICATED_CACHE_NAME).put(1, IgniteSqlEntryCacheModeAgnosticTest.REPLICATED_CACHE_NAME);
        ignite.cache(IgniteSqlEntryCacheModeAgnosticTest.PARTITIONED_CACHE_NAME).put(1, IgniteSqlEntryCacheModeAgnosticTest.PARTITIONED_CACHE_NAME);
        final List<String> cacheNamesList = F.asList(IgniteSqlEntryCacheModeAgnosticTest.LOCAL_CACHE_NAME, IgniteSqlEntryCacheModeAgnosticTest.REPLICATED_CACHE_NAME, IgniteSqlEntryCacheModeAgnosticTest.PARTITIONED_CACHE_NAME);
        for (String entryCacheName : cacheNamesList) {
            for (String qryCacheName : cacheNamesList) {
                if (entryCacheName.equals(qryCacheName))
                    continue;

                QueryCursor<List<?>> cursor = ignite.cache(entryCacheName).query(new SqlFieldsQuery((("SELECT _VAL FROM \"" + qryCacheName) + "\".String")));
                assertEquals(qryCacheName, ((String) (cursor.getAll().get(0).get(0))));
            }
        }
    }
}


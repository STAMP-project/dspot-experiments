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
package org.apache.ignite.internal.processors.cache.persistence.db;


import com.google.common.base.Strings;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test creates a lot of index pages in the cache with low number of partitions.<br>
 * Then cache entries are removed to enforce all pages to come to a free list. <br>
 * Then creation of data pages with long data will probably result in page rotation.<br>
 * Expected behaviour: all {@link InitNewPageRecord} should have consistent partition IDs.
 */
public class IgniteTcBotInitNewPageTest extends GridCommonAbstractTest {
    /**
     * Cache name.
     */
    public static final String CACHE = "cache";

    /**
     *
     */
    @Test
    public void testInitNewPagePageIdConsistency() throws Exception {
        IgniteEx ignite = startGrid(0);
        ignite.cluster().active(true);
        IgniteCache<Object, Object> cache = ignite.cache(IgniteTcBotInitNewPageTest.CACHE);
        for (int i = 0; i < 1000000; i++)
            cache.put(i, i);

        cache.clear();
        for (int i = 0; i < 1000; i++)
            cache.put(i, Strings.repeat("Apache Ignite", 1000));

    }
}


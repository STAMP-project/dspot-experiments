/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map.impl.querycache;


import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.mapreduce.helpers.Employee;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheDataSyncWithMapTest extends HazelcastTestSupport {
    protected String mapName = HazelcastTestSupport.randomString();

    protected String cacheName = HazelcastTestSupport.randomString();

    protected TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();

    protected IMap<Integer, Employee> map;

    protected QueryCache<Integer, Employee> queryCache;

    @Test
    public void map_and_queryCache_data_sync_after_map_clear() {
        test_map_wide_event(QueryCacheDataSyncWithMapTest.MethodName.CLEAR);
    }

    @Test
    public void map_and_queryCache_data_sync_after_map_evictAll() {
        test_map_wide_event(QueryCacheDataSyncWithMapTest.MethodName.EVICT_ALL);
    }

    enum MethodName {

        CLEAR,
        EVICT_ALL;}
}


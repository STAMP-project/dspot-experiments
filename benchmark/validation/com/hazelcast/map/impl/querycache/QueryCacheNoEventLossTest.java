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


import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.annotation.SlowTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheNoEventLossTest extends HazelcastTestSupport {
    private static final String MAP_NAME = "mapName";

    private static final String QUERY_CACHE_NAME = "cacheName";

    private static final String PARTITION_COUNT = "1999";

    private static final int TEST_DURATION_SECONDS = 3;

    private TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();

    @Test
    public void no_event_lost_during_migrations__with_one_parallel_node() {
        no_event_lost_during_migrations(1, 0);
    }

    @Test
    @Category(SlowTest.class)
    public void no_event_lost_during_migrations__with_many_parallel_nodes() {
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                try {
                    no_event_lost_during_migrations(3, 5);
                } finally {
                    factory.shutdownAll();
                }
            }
        });
    }
}


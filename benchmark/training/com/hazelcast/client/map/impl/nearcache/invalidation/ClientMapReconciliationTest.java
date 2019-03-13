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
package com.hazelcast.client.map.impl.nearcache.invalidation;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMapReconciliationTest extends HazelcastTestSupport {
    private static final String MAP_NAME = "ClientMapReconciliationTest";

    private static final int RECONCILIATION_INTERVAL_SECS = 3;

    private final TestHazelcastFactory factory = new TestHazelcastFactory();

    private ClientConfig clientConfig;

    private IMap<Integer, Integer> serverMap;

    private IMap<Integer, Integer> clientMap;

    @Test
    public void test_reconciliation_does_not_cause_premature_removal() {
        int total = 100;
        for (int i = 0; i < total; i++) {
            serverMap.put(i, i);
        }
        for (int i = 0; i < total; i++) {
            clientMap.get(i);
        }
        IMap mapFromNewClient = createMapFromNewClient();
        for (int i = 0; i < total; i++) {
            mapFromNewClient.get(i);
        }
        NearCacheStats nearCacheStats = mapFromNewClient.getLocalMapStats().getNearCacheStats();
        assertStats(nearCacheStats, total, 0, total);
        sleepSeconds((2 * (ClientMapReconciliationTest.RECONCILIATION_INTERVAL_SECS)));
        for (int i = 0; i < total; i++) {
            mapFromNewClient.get(i);
        }
        assertStats(nearCacheStats, total, total, total);
    }
}


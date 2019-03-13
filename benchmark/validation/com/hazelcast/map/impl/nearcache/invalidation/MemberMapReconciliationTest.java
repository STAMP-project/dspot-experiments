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
package com.hazelcast.map.impl.nearcache.invalidation;


import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MemberMapReconciliationTest extends HazelcastTestSupport {
    private static final String MAP_NAME = "MemberMapReconciliationTest";

    private static final int RECONCILIATION_INTERVAL_SECS = 3;

    @Parameterized.Parameter
    public InMemoryFormat mapInMemoryFormat;

    @Parameterized.Parameter(1)
    public InMemoryFormat nearCacheInMemoryFormat;

    private final TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory();

    private Config config;

    private HazelcastInstance server;

    private IMap<Integer, Integer> serverMap;

    private IMap<Integer, Integer> nearCachedServerMap;

    @Test
    public void test_reconciliation_does_not_cause_premature_removal() {
        int total = 100;
        for (int i = 0; i < total; i++) {
            serverMap.put(i, i);
        }
        for (int i = 0; i < total; i++) {
            nearCachedServerMap.get(i);
        }
        final IMap<Integer, Integer> nearCachedMapFromNewServer = nearCachedMapFromNewServer();
        HazelcastTestSupport.warmUpPartitions(factory.getAllHazelcastInstances());
        HazelcastTestSupport.waitAllForSafeState(factory.getAllHazelcastInstances());
        waitForNearCacheInvalidationMetadata(nearCachedMapFromNewServer, server);
        for (int i = 0; i < total; i++) {
            nearCachedMapFromNewServer.get(i);
        }
        NearCacheStats nearCacheStats = nearCachedMapFromNewServer.getLocalMapStats().getNearCacheStats();
        MemberMapReconciliationTest.assertStats(nearCacheStats, total, 0, total);
        HazelcastTestSupport.sleepSeconds((2 * (MemberMapReconciliationTest.RECONCILIATION_INTERVAL_SECS)));
        for (int i = 0; i < total; i++) {
            nearCachedMapFromNewServer.get(i);
        }
        MemberMapReconciliationTest.assertStats(nearCacheStats, total, total, total);
    }
}


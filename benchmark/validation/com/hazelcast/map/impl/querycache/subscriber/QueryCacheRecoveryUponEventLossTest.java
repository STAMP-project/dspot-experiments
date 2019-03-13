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
package com.hazelcast.map.impl.querycache.subscriber;


import GroupProperty.PARTITION_COUNT;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EventLostEvent;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.impl.querycache.AbstractQueryCacheTestSupport;
import com.hazelcast.map.listener.EventLostListener;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheRecoveryUponEventLossTest extends HazelcastTestSupport {
    @Test
    public void testForceConsistency() {
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(3);
        final String mapName = HazelcastTestSupport.randomString();
        String queryCacheName = HazelcastTestSupport.randomString();
        Config config = new Config();
        config.setProperty(PARTITION_COUNT.getName(), "1");
        QueryCacheConfig queryCacheConfig = new QueryCacheConfig(queryCacheName);
        queryCacheConfig.setBatchSize(1111);
        queryCacheConfig.setDelaySeconds(3);
        MapConfig mapConfig = config.getMapConfig(mapName);
        mapConfig.addQueryCacheConfig(queryCacheConfig);
        mapConfig.setBackupCount(0);
        final HazelcastInstance node = instanceFactory.newHazelcastInstance(config);
        HazelcastInstance node2 = instanceFactory.newHazelcastInstance(config);
        setTestSequencer(node, 9);
        setTestSequencer(node2, 9);
        IMap<Integer, Integer> map = AbstractQueryCacheTestSupport.getMap(node, mapName);
        node2.getMap(mapName);
        final CountDownLatch waitEventLossNotification = new CountDownLatch(1);
        final QueryCache queryCache = map.getQueryCache(queryCacheName, new SqlPredicate("this > 20"), true);
        queryCache.addEntryListener(new EventLostListener() {
            @Override
            public void eventLost(EventLostEvent event) {
                queryCache.tryRecover();
                waitEventLossNotification.countDown();
            }
        }, false);
        int count = 30;
        for (int i = 0; i < count; i++) {
            map.put(i, i);
        }
        HazelcastTestSupport.assertOpenEventually(waitEventLossNotification);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(9, queryCache.size());
            }
        });
        // re-put entries and check if broken-sequences holder map will be empty
        for (int i = 0; i < count; i++) {
            map.put(i, i);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Map brokenSequences = getBrokenSequences(node, mapName, queryCache);
                Assert.assertTrue("After recovery, there should be no broken sequences left", brokenSequences.isEmpty());
            }
        });
    }
}


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
package com.hazelcast.cache;


import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.partition.AbstractPartitionLostListenerTest;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.cache.Cache;
import javax.cache.CacheManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(SlowTest.class)
public class CachePartitionLostListenerStressTest extends AbstractPartitionLostListenerTest {
    @Parameterized.Parameter(0)
    public int numberOfNodesToCrash;

    @Parameterized.Parameter(1)
    public boolean withData;

    @Parameterized.Parameter(2)
    public AbstractPartitionLostListenerTest.NodeLeaveType nodeLeaveType;

    @Parameterized.Parameter(3)
    public boolean shouldExpectPartitionLostEvents;

    @Test
    public void testCachePartitionLostListener() {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp();
        List<HazelcastInstance> survivingInstances = new ArrayList<HazelcastInstance>(instances);
        List<HazelcastInstance> terminatingInstances = survivingInstances.subList(0, numberOfNodesToCrash);
        survivingInstances = survivingInstances.subList(numberOfNodesToCrash, instances.size());
        HazelcastInstance instance = survivingInstances.get(0);
        HazelcastServerCachingProvider cachingProvider = HazelcastServerCachingProvider.createCachingProvider(instance);
        CacheManager cacheManager = cachingProvider.getCacheManager();
        List<CachePartitionLostListenerTest.EventCollectingCachePartitionLostListener> listeners = registerListeners(cacheManager);
        if (withData) {
            for (int i = 0; i < (getNodeCount()); i++) {
                Cache<Integer, Integer> cache = cacheManager.getCache(getIthCacheName(i));
                for (int j = 0; j < (getCacheEntryCount()); j++) {
                    cache.put(j, j);
                }
            }
        }
        final String log = (("Surviving: " + survivingInstances) + " Terminating: ") + terminatingInstances;
        Map<Integer, Integer> survivingPartitions = getMinReplicaIndicesByPartitionId(survivingInstances);
        stopInstances(terminatingInstances, nodeLeaveType);
        HazelcastTestSupport.waitAllForSafeState(survivingInstances);
        if (shouldExpectPartitionLostEvents) {
            for (int i = 0; i < (getNodeCount()); i++) {
                assertListenerInvocationsEventually(log, i, numberOfNodesToCrash, listeners.get(i), survivingPartitions);
            }
        } else {
            for (final CachePartitionLostListenerTest.EventCollectingCachePartitionLostListener listener : listeners) {
                HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
                    @Override
                    public void run() throws Exception {
                        Assert.assertTrue(listener.getEvents().isEmpty());
                    }
                }, 1);
            }
        }
        for (int i = 0; i < (getNodeCount()); i++) {
            cacheManager.destroyCache(getIthCacheName(i));
        }
        cacheManager.close();
        cachingProvider.close();
    }
}


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
package com.hazelcast.internal.eviction;


import com.hazelcast.cache.HazelcastExpiryPolicy;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheConfiguration;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.LifecycleServiceImpl;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static Duration.ONE_HOUR;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheExpirationManagerTest extends AbstractExpirationManagerTest {
    @Test
    public void restarts_running_backgroundClearTask_when_lifecycleState_turns_to_MERGED() {
        Config config = new Config();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        HazelcastInstance node = createHazelcastInstance(config);
        final CacheExpirationManagerTest.SimpleEntryListener<Integer, Integer> simpleEntryListener = new CacheExpirationManagerTest.SimpleEntryListener<Integer, Integer>();
        CacheManager cacheManager = createCacheManager(node);
        CacheConfiguration<Integer, Integer> cacheConfig = createCacheConfig(simpleEntryListener, new HazelcastExpiryPolicy(3000, 3000, 3000));
        Cache<Integer, Integer> cache = cacheManager.createCache("test", cacheConfig);
        cache.put(1, 1);
        ((LifecycleServiceImpl) (node.getLifecycleService())).fireLifecycleEvent(MERGING);
        ((LifecycleServiceImpl) (node.getLifecycleService())).fireLifecycleEvent(MERGED);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                int expirationCount = simpleEntryListener.expiredCount.get();
                Assert.assertEquals(String.format("Expecting 1 expiration but found:%d", expirationCount), 1, expirationCount);
            }
        });
    }

    @Test
    public void clearExpiredRecordsTask_should_not_be_started_if_cache_has_no_expirable_records() {
        Config config = new Config();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        final HazelcastInstance node = createHazelcastInstance(config);
        CacheManager cacheManager = createCacheManager(node);
        cacheManager.createCache("test", new CacheConfig());
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse("There should be zero CacheClearExpiredRecordsTask", hasClearExpiredRecordsTaskStarted(node));
            }
        }, 3);
    }

    @Test
    public void clearExpiredRecordsTask_should_not_be_started_if_member_is_lite() {
        Config liteMemberConfig = new Config();
        liteMemberConfig.setLiteMember(true);
        liteMemberConfig.setProperty(taskPeriodSecondsPropName(), "1");
        Config dataMemberConfig = new Config();
        dataMemberConfig.setProperty(taskPeriodSecondsPropName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance liteMember = factory.newHazelcastInstance(liteMemberConfig);
        factory.newHazelcastInstance(dataMemberConfig);
        CacheManager cacheManager = createCacheManager(liteMember);
        CacheConfiguration<Integer, Integer> cacheConfig = createCacheConfig(new CacheExpirationManagerTest.SimpleEntryListener(), new HazelcastExpiryPolicy(1, 1, 1));
        Cache<Integer, Integer> cache = cacheManager.createCache("test", cacheConfig);
        cache.put(1, 1);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse("There should be zero CacheClearExpiredRecordsTask", hasClearExpiredRecordsTaskStarted(liteMember));
            }
        }, 3);
    }

    @Test
    public void stops_running_backgroundClearTask_when_lifecycleState_SHUTTING_DOWN() {
        backgroundClearTaskStops_whenLifecycleState(SHUTTING_DOWN);
    }

    @Test
    public void stops_running_backgroundClearTask_when_lifecycleState_MERGING() {
        backgroundClearTaskStops_whenLifecycleState(MERGING);
    }

    @Test
    public void no_expiration_task_starts_on_new_node_after_migration_when_there_is_no_expirable_entry() {
        Config config = new Config();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance node1 = factory.newHazelcastInstance(config);
        CacheManager cacheManager = createCacheManager(node1);
        Cache cache = cacheManager.createCache("test", new CacheConfig());
        cache.put(1, 1);
        final HazelcastInstance node2 = factory.newHazelcastInstance(config);
        node1.shutdown();
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse("There should be zero CacheClearExpiredRecordsTask", hasClearExpiredRecordsTaskStarted(node2));
            }
        }, 3);
    }

    @Test
    public void expiration_task_starts_on_new_node_after_migration_when_there_is_expirable_entry() {
        Config config = new Config();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance node1 = factory.newHazelcastInstance(config);
        CacheManager cacheManager = createCacheManager(node1);
        Cache cache = cacheManager.createCache("test", new CacheConfig());
        put(1, 1, new HazelcastExpiryPolicy(ONE_HOUR, ONE_HOUR, ONE_HOUR));
        final HazelcastInstance node2 = factory.newHazelcastInstance(config);
        node1.shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue("There should be one ClearExpiredRecordsTask started", hasClearExpiredRecordsTaskStarted(node2));
            }
        });
    }

    public static class SimpleEntryListener<K, V> implements Serializable , CacheEntryExpiredListener<K, V> {
        AtomicInteger expiredCount = new AtomicInteger();

        @Override
        public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
            expiredCount.incrementAndGet();
        }
    }
}


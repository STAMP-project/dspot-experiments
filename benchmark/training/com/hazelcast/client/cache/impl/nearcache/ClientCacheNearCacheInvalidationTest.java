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
package com.hazelcast.client.cache.impl.nearcache;


import LifecycleEvent.LifecycleState.SHUTTING_DOWN;
import LocalUpdatePolicy.CACHE_ON_UPDATE;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.LifecycleServiceImpl;
import com.hazelcast.internal.nearcache.NearCacheTestContext;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.SlowTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests that Near Cache invalidation events are delivered when a cache is:
 * <ul>
 * <li>{@code cleared}</li>
 * <li>{@code destroyed} (via {@link ICache#destroy()})</li>
 * <li>{@code destroyed} (via its client-side {@link CacheManager#destroyCache(String)})</li>
 * </ul>
 * and <b>not delivered</b> when a cache is closed (via {@link ICache#close()}).
 * <p>
 * Respective operations are tested when executed either from member or client-side Cache proxies with the exception of
 * {@link CacheManager#destroyCache(String)}, in which case the Near Cache is already destroyed on the client-side and
 * the listener registration is removed <b>before</b> the invocation for destroying the cache is sent to the member,
 * so no event can be received.
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ SlowTest.class, ParallelTest.class })
@SuppressWarnings("WeakerAccess")
public class ClientCacheNearCacheInvalidationTest extends HazelcastTestSupport {
    private static final String DEFAULT_CACHE_NAME = "ClientCacheNearCacheInvalidationTest";

    // time to wait until invalidation event is delivered (when used with assertTrueEventually)
    // and time to wait when testing that no invalidation event is delivered (used with assertTrueAllTheTime)
    private static final int TIMEOUT = 10;

    // some events are delivered exactly once, some are delivered MEMBER_COUNT times
    // we start MEMBER_COUNT members in the test and validate count of events against this number
    private static final int MEMBER_COUNT = 2;

    private static final int INITIAL_POPULATION_COUNT = 1000;

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    // when true, invoke operations which are supposed to deliver invalidation events from a Cache instance on a member
    // otherwise use the client-side proxy.
    @Parameterized.Parameter(1)
    public boolean invokeCacheOperationsFromMember;

    private TestHazelcastFactory hazelcastFactory;

    private NearCacheTestContext<Integer, String, Object, String> testContext;

    private ClientCacheInvalidationListener invalidationListener;

    @Test
    public void putToCacheAndGetInvalidationEventWhenNodeShutdown() {
        Config config = getConfig().setProperty(CACHE_INVALIDATION_MESSAGE_BATCH_ENABLED.getName(), "true").setProperty(CACHE_INVALIDATION_MESSAGE_BATCH_SIZE.getName(), String.valueOf(Integer.MAX_VALUE)).setProperty(CACHE_INVALIDATION_MESSAGE_BATCH_FREQUENCY_SECONDS.getName(), String.valueOf(Integer.MAX_VALUE));
        HazelcastInstance instanceToShutdown = hazelcastFactory.newHazelcastInstance(config);
        warmUpPartitions(testContext.dataInstance, instanceToShutdown);
        waitAllForSafeState(testContext.dataInstance, instanceToShutdown);
        NearCacheConfig nearCacheConfig = getNearCacheConfig(inMemoryFormat).setInvalidateOnChange(true).setLocalUpdatePolicy(CACHE_ON_UPDATE);
        CacheConfig<String, String> cacheConfig = getCacheConfig(inMemoryFormat);
        final NearCacheTestContext<String, String, Object, String> nearCacheTestContext1 = createNearCacheTest(ClientCacheNearCacheInvalidationTest.DEFAULT_CACHE_NAME, nearCacheConfig, cacheConfig);
        final NearCacheTestContext<String, String, Object, String> nearCacheTestContext2 = createNearCacheTest(ClientCacheNearCacheInvalidationTest.DEFAULT_CACHE_NAME, nearCacheConfig, cacheConfig);
        Map<String, String> keyAndValues = new HashMap<String, String>();
        // put cache record from client-1 to instance which is going to be shutdown
        for (int i = 0; i < (ClientCacheNearCacheInvalidationTest.INITIAL_POPULATION_COUNT); i++) {
            String key = generateKeyOwnedBy(instanceToShutdown);
            String value = ClientNearCacheTestSupport.generateValueFromKey(i);
            nearCacheTestContext1.nearCacheAdapter.put(key, value);
            keyAndValues.put(key, value);
        }
        // verify that records are exist at Near Cache of client-1 because `local-update-policy` is `CACHE`
        for (Map.Entry<String, String> entry : keyAndValues.entrySet()) {
            String key = entry.getKey();
            String exceptedValue = entry.getValue();
            String actualValue = ClientCacheNearCacheInvalidationTest.getFromNearCache(nearCacheTestContext1, key);
            Assert.assertEquals(exceptedValue, actualValue);
        }
        // remove records through client-2 so there will be invalidation events
        // to send to client to invalidate its Near Cache
        for (Map.Entry<String, String> entry : keyAndValues.entrySet()) {
            nearCacheTestContext2.nearCacheAdapter.remove(entry.getKey());
        }
        // we don't shutdown the instance because in case of shutdown even though events are published to event queue,
        // they may not be processed in the event queue due to shutdown event queue executor or may not be sent
        // to client endpoint due to IO handler shutdown
        // for not to making test fragile, we just simulate shutting down by sending its event through `LifeCycleService`,
        // so the node should flush invalidation events before shutdown
        ((LifecycleServiceImpl) (instanceToShutdown.getLifecycleService())).fireLifecycleEvent(SHUTTING_DOWN);
        // verify that records in the Near Cache of client-1 are invalidated eventually when instance shutdown
        for (Map.Entry<String, String> entry : keyAndValues.entrySet()) {
            final String key = entry.getKey();
            assertTrueEventually(new AssertTask() {
                @Override
                public void run() {
                    Assert.assertNull(ClientCacheNearCacheInvalidationTest.getFromNearCache(nearCacheTestContext1, key));
                }
            });
        }
    }

    @Test
    public void putToCacheAndDoNotInvalidateFromClientNearCacheWhenPerEntryInvalidationIsDisabled() {
        // we need to use another cache name, to get the invalidation setting working
        String cacheName = "disabledPerEntryInvalidationCache";
        NearCacheConfig nearCacheConfig = getNearCacheConfig(inMemoryFormat).setName(cacheName).setInvalidateOnChange(true);
        CacheConfig<Integer, String> cacheConfig = getCacheConfig(inMemoryFormat);
        cacheConfig.setName(cacheName);
        cacheConfig.setDisablePerEntryInvalidationEvents(true);
        final NearCacheTestContext<Integer, String, Object, String> nearCacheTestContext1 = createNearCacheTest(cacheName, nearCacheConfig, cacheConfig);
        final NearCacheTestContext<Integer, String, Object, String> nearCacheTestContext2 = createNearCacheTest(cacheName, nearCacheConfig, cacheConfig);
        // put cache record from client-1
        for (int i = 0; i < (ClientCacheNearCacheInvalidationTest.INITIAL_POPULATION_COUNT); i++) {
            nearCacheTestContext1.nearCacheAdapter.put(i, ClientNearCacheTestSupport.generateValueFromKey(i));
        }
        // get records from client-2
        for (int i = 0; i < (ClientCacheNearCacheInvalidationTest.INITIAL_POPULATION_COUNT); i++) {
            final Integer key = i;
            final String value = nearCacheTestContext2.nearCacheAdapter.get(key);
            // records are stored in the cache as async not sync, so these records will be there in cache eventually
            assertTrueEventually(new AssertTask() {
                @Override
                public void run() {
                    Assert.assertEquals(value, ClientCacheNearCacheInvalidationTest.getFromNearCache(nearCacheTestContext2, key));
                }
            });
        }
        // update cache record from client-1
        for (int i = 0; i < (ClientCacheNearCacheInvalidationTest.INITIAL_POPULATION_COUNT); i++) {
            // update the cache records with new values
            nearCacheTestContext1.nearCacheAdapter.put(i, ClientNearCacheTestSupport.generateValueFromKey((i + (ClientCacheNearCacheInvalidationTest.INITIAL_POPULATION_COUNT))));
        }
        int invalidationEventFlushFreq = Integer.parseInt(CACHE_INVALIDATION_MESSAGE_BATCH_FREQUENCY_SECONDS.getDefaultValue());
        // wait some time and if there are invalidation events to be sent in batch
        // (we assume that they should be flushed, received and processed in this time window already)
        sleepSeconds((2 * invalidationEventFlushFreq));
        // get records from client-2
        for (int i = 0; i < (ClientCacheNearCacheInvalidationTest.INITIAL_POPULATION_COUNT); i++) {
            String actualValue = nearCacheTestContext2.nearCacheAdapter.get(i);
            String expectedValue = ClientNearCacheTestSupport.generateValueFromKey(i);
            // verify that still we have old records in the Near Cache, because, per entry invalidation events are disabled
            Assert.assertEquals(expectedValue, actualValue);
        }
        nearCacheTestContext1.nearCacheAdapter.clear();
        // can't get expired records from client-2
        for (int i = 0; i < (ClientCacheNearCacheInvalidationTest.INITIAL_POPULATION_COUNT); i++) {
            final int key = i;
            // records are stored in the Near Cache will be invalidated eventually, since cache records are cleared
            // because we just disable per entry invalidation events, not full-flush events
            assertTrueEventually(new AssertTask() {
                @Override
                public void run() {
                    Assert.assertNull(ClientCacheNearCacheInvalidationTest.getFromNearCache(nearCacheTestContext2, key));
                }
            });
        }
    }

    @Test
    public void when_shuttingDown_invalidationEventIsNotReceived() {
        populateMemberCache();
        if (invokeCacheOperationsFromMember) {
            testContext.dataInstance.shutdown();
        } else {
            testContext.nearCacheInstance.shutdown();
        }
        assertNoFurtherInvalidation();
    }

    @Test
    public void when_cacheDestroyed_invalidationEventIsReceived() {
        populateMemberCache();
        if (invokeCacheOperationsFromMember) {
            testContext.dataAdapter.destroy();
        } else {
            testContext.nearCacheAdapter.destroy();
        }
        assertLeastInvalidationCount(1);
    }

    @Test
    public void when_cacheCleared_invalidationEventIsReceived() {
        populateMemberCache();
        if (invokeCacheOperationsFromMember) {
            testContext.dataAdapter.clear();
        } else {
            testContext.nearCacheAdapter.clear();
        }
        assertNoFurtherInvalidationThan(1);
    }

    @Test
    public void when_cacheClosed_invalidationEventIsNotReceived() {
        populateMemberCache();
        if (invokeCacheOperationsFromMember) {
            testContext.dataAdapter.close();
        } else {
            testContext.nearCacheAdapter.close();
        }
        assertNoFurtherInvalidation();
    }

    /**
     * When CacheManager.destroyCache() is invoked from client-side CacheManager, an invalidation event is received.
     * When invoked from a member-side CacheManager, invalidation event is not received.
     */
    @Test
    public void when_cacheManagerDestroyCacheInvoked_invalidationEventMayBeReceived() {
        populateMemberCache();
        if (invokeCacheOperationsFromMember) {
            testContext.memberCacheManager.destroyCache(ClientCacheNearCacheInvalidationTest.DEFAULT_CACHE_NAME);
        } else {
            testContext.cacheManager.destroyCache(ClientCacheNearCacheInvalidationTest.DEFAULT_CACHE_NAME);
        }
        assertLeastInvalidationCount(1);
    }
}


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
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.nearcache.NearCacheTestSupport;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.util.RandomPicker;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(NightlyTest.class)
@SuppressWarnings("WeakerAccess")
public class MemberMapInvalidationMemberAddRemoveTest extends NearCacheTestSupport {
    private static final int TEST_RUN_SECONDS = 30;

    private static final int KEY_COUNT = 100000;

    private static final int INVALIDATION_BATCH_SIZE = 10000;

    private static final int RECONCILIATION_INTERVAL_SECS = 30;

    private final TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory();

    @Test
    public void ensure_nearCached_and_actual_data_sync_eventually() {
        final String mapName = "MemberMapInvalidationMemberAddRemoveTest";
        final AtomicBoolean stopTest = new AtomicBoolean();
        // members are created
        MapConfig mapConfig = createMapConfig(mapName);
        final Config config = createConfig().addMapConfig(mapConfig);
        HazelcastInstance member = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        // map is populated form member
        final IMap<Integer, Integer> memberMap = member.getMap(mapName);
        for (int i = 0; i < (MemberMapInvalidationMemberAddRemoveTest.KEY_COUNT); i++) {
            memberMap.put(i, i);
        }
        // a new member comes with Near Cache configured
        MapConfig nearCachedMapConfig = createMapConfig(mapName).setNearCacheConfig(createNearCacheConfig(mapName));
        Config nearCachedConfig = createConfig().addMapConfig(nearCachedMapConfig);
        HazelcastInstance nearCachedMember = factory.newHazelcastInstance(nearCachedConfig);
        final IMap<Integer, Integer> nearCachedMap = nearCachedMember.getMap(mapName);
        List<Thread> threads = new ArrayList<Thread>();
        // continuously adds and removes member
        Thread shadowMember = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stopTest.get())) {
                    HazelcastInstance member = factory.newHazelcastInstance(config);
                    HazelcastTestSupport.sleepSeconds(5);
                    member.getLifecycleService().terminate();
                } 
            }
        });
        threads.add(shadowMember);
        // populates client Near Cache
        Thread populateClientNearCache = new Thread(new Runnable() {
            public void run() {
                while (!(stopTest.get())) {
                    for (int i = 0; i < (MemberMapInvalidationMemberAddRemoveTest.KEY_COUNT); i++) {
                        nearCachedMap.get(i);
                    }
                } 
            }
        });
        threads.add(populateClientNearCache);
        // updates map data from member
        Thread putFromMember = new Thread(new Runnable() {
            public void run() {
                while (!(stopTest.get())) {
                    int key = RandomPicker.getInt(MemberMapInvalidationMemberAddRemoveTest.KEY_COUNT);
                    int value = RandomPicker.getInt(Integer.MAX_VALUE);
                    memberMap.put(key, value);
                    HazelcastTestSupport.sleepAtLeastMillis(5);
                } 
            }
        });
        threads.add(putFromMember);
        Thread clearFromMember = new Thread(new Runnable() {
            public void run() {
                while (!(stopTest.get())) {
                    memberMap.clear();
                    HazelcastTestSupport.sleepSeconds(5);
                } 
            }
        });
        threads.add(clearFromMember);
        // start threads
        for (Thread thread : threads) {
            thread.start();
        }
        // stress system some seconds
        HazelcastTestSupport.sleepSeconds(MemberMapInvalidationMemberAddRemoveTest.TEST_RUN_SECONDS);
        // stop threads
        stopTest.set(true);
        for (Thread thread : threads) {
            HazelcastTestSupport.assertJoinable(thread);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < (MemberMapInvalidationMemberAddRemoveTest.KEY_COUNT); i++) {
                    Integer valueSeenFromMember = memberMap.get(i);
                    Integer valueSeenFromClient = nearCachedMap.get(i);
                    Assert.assertEquals(valueSeenFromMember, valueSeenFromClient);
                }
            }
        });
    }
}

